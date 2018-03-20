package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transcodeType")
@XmlRootElement(name = "transcode")
public class TranscodeRequest {


    @NotNull
    @XmlAttribute
    private String mid;

    @NotNull
    private String fileName;

    @NotNull
    private Encryption encryption;

    @NotNull
    private Priority priority;


    public TranscodeRequest() {

    }

    @XmlType(name = "encryptionType")
    public enum Encryption implements  Displayable {
        DRM("DRM"),
        NONE("Geen");
        @Getter
        private final String displayName;


        Encryption(String displayName) {
            this.displayName = displayName;
        }
    }
    @XmlType(name = "priorityType")
    public enum Priority implements Displayable {
        LOW("Laag"),
        NORMAL("Normaal"), HIGH("Hoog"), URGENT("Urgent");

        @Getter
        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }
    }
}
