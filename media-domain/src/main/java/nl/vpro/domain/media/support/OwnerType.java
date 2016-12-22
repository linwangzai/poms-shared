package nl.vpro.domain.media.support;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;


/**
 * An owner is a data supplier and authority which can be held responsible for parts of the
 * media data set. {@link #MIS} for example is responsible for all schedule data and some
 * parts of the media meta-data.
 * <p/>
 * When a field gets an owner and an 1..n to n association, different owners can supply there
 * own value for this field to be kept for future usage and updates.
 * <p/>
 * The order of the values for this Enum is relevant because it is responsible for the ordering of Ownable types
 * when obtained from data or send to clients.
 * <p/>
 * Owners should not be confused with users or groups. They don't take any part in security
 * related scenarios.
 *
 * @author roekoe
 * @see nl.vpro.domain.media.support.Title
 * @see nl.vpro.domain.media.support.Description
 * @see nl.vpro.domain.media.Location
 * @see nl.vpro.domain.media.support.Image
 * @since 0.4
 */
@XmlEnum
@XmlType(name = "ownerTypeEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum OwnerType implements nl.vpro.domain.Displayable {

    /**
     * Represents data coming from a user or system working for a broadcaster that is allowed
     * to update an Ownable entity.
     */
    BROADCASTER("Omroep"),

    /**
     * Represents Ownable data from Radiobox. Radiobox delivers schedules and metadata for RAD1/2/3/4/5/6.
     *
     * @since 1.6
     */
    RADIOBOX("Radiobox 2"),

    /**
     * Represents Ownable data updated by NEBO batch updates. Before version 1.4 we did not distinguish between NEBO
     * and Ceres as different sources. There might be legacy data incorrectly revering to NEBO as it's owner.
     */
    NEBO("Nebo"),

    /**
     * MSE-3358
     * @since 4.7
     */
    NPO("NPO"),

    /**
    /**
     * Represents Ownable data updated by MIS via TVAnytime deliveries
     */
    MIS("MIS"),

    /**
     * Represents Ownable data updated by CERES via predict, metadata, notify or revoke file delivery. It is possible
     * that data obtained from CERES indirectly results from NEBO, MIS or What's-On, but we can't detect these origins
     * from a Ceres delivery.
     *
     * @since 1.4
     */
    CERES("Ceres"),

     /**
     * Represents Ownable data updated by PLUTO via predict, metadata, notify or revoke file delivery. It is possible
     * that data obtained from PLUTO indirectly results from NEBO, MIS or What's-On, but we can't detect these origins
     * from a Pluto delivery.
     *
     * @since 3.4
     */
    PLUTO("Pluto"),

    /**
     * Represents Ownable data updated by PLUTO via predict, metadata, notify or revoke file delivery. It is possible
     * that data obtained from PLUTO indirectly results from NEBO, MIS or What's-On, but we can't detect these origins
     * from a Pluto delivery.
     *
     * @since 3.4
     */
    PROJECTM("Project M"),

    /**
     * Represents Ownable data from What's-On send to Poms by Ceres. Twice a day Ceres queries What's-On for the
     * complete NED1/2/3 schedules. The output from these queries is the first source for media and schedule
     * meta-data in Poms and will later on be updated by sources more upstream.
     *
     * @since 1.4
     */
    WHATS_ON("Whats'On"),

    /**
     * Represents Ownable data from Immix ("Beeld en Geluid") send to Poms via updates from their archive.
     *
     * @since 1.4
     */
    IMMIX("Immix");

    private String displayName;

    OwnerType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
