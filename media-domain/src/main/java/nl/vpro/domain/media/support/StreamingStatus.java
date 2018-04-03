package nl.vpro.domain.media.support;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.media.Encryption;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public enum StreamingStatus implements Displayable {

    /**
     * Not notified by NEP
     */
    NOT_AVAILABLE(null, false, "niet beschikbaar"),

    /**
     * Explicitely notified by NEP to be offline
     */
    OFFLINE(null, false, "offline"),

    /**
     * Explicitely notified by NEP to be online
     */
    AVAILABLE(false, true, "beschikbaar"),

    /**
     * Explicitely notified by NEP to be online with DRM
     */
    AVAILABLE_WITH_DRM(true, true, "beschikbaar met DRM"),

    AVAILABLE_WITH_AND_WITHOUT_DRM(true, true, "beschikbaar met en zonder DRM");



    private final Boolean drm;
    private final boolean available;
    @Getter
    private final String displayName;


    StreamingStatus(Boolean drm, boolean available, String displayName) {
        this.drm = drm;
        this.available = available;
        this.displayName = displayName;
    }

    public static StreamingStatus available(boolean drm, StreamingStatus existing) {
        if (drm) {
            switch (existing) {
                case OFFLINE:
                case NOT_AVAILABLE:
                case AVAILABLE_WITH_DRM:
                    return AVAILABLE_WITH_DRM;
                case AVAILABLE:
                case AVAILABLE_WITH_AND_WITHOUT_DRM:
                    return AVAILABLE_WITH_AND_WITHOUT_DRM;
            }
        } else {
            switch (existing) {
                case OFFLINE:
                case NOT_AVAILABLE:
                case AVAILABLE:
                    return AVAILABLE;
                case AVAILABLE_WITH_DRM:
                case AVAILABLE_WITH_AND_WITHOUT_DRM:
                    return AVAILABLE_WITH_AND_WITHOUT_DRM;
            }
        }
        throw new IllegalStateException();
    }

    public static Collection<StreamingStatus> availableStatuses() {
        return Arrays.stream(values())
            .filter(StreamingStatus::isAvailable)
            .collect(Collectors.toSet());
    }


    public static Collection<StreamingStatus> notAvailableStatuses() {
        return Arrays.stream(values()).filter(s -> ! s.isAvailable()).collect(Collectors.toSet());
    }

    public boolean hasDrm() {
        return drm != null && drm;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean matches(Encryption encryption) {
        return encryption == null ||
            (encryption == Encryption.DRM && hasDrm()) ||
            (encryption == Encryption.NONE && ! hasDrm());
    }

}
