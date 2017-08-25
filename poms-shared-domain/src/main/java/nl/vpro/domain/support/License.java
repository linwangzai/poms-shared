package nl.vpro.domain.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Id;
import javax.swing.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 * @TODO Licenses have no support for versions. I now suggested 4.0 for the the CC-licenses, but other version may occur too!
 *       Perhaps License should not be enum, or have many more values.
 *       The trouble with enum is any way that java clients are hard to keep in sync.
 */

@ToString
@XmlType(name = "licenseType", namespace = Xmlns.SHARED_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@JsonPropertyOrder({"id", "name"})
public class License implements nl.vpro.domain.Displayable {

    //region
    public static License COPYRIGHTED = new License("Copyrighted", null, true);
    public static License PUBLIC_DOMAIN = new License("Publiek domein", null, true);
    public static License CC_BY = new License("Naamsvermelding", null, true);
    public static License CC_BY_1_0 = new License("Naamsvermelding_1.0", null, true);
    public static License CC_BY_2_0 = new License("Naamsvermelding_2.0", null, true);
    public static License CC_BY_3_0 = new License("Naamsvermelding_3.0", null, true);
    public static License CC_BY_4_0 = new License("Naamsvermelding_4.0", null, true);
    public static License CC_BY_SA = new License("Naamsvermelding-GelijkDelen", null, true);
    public static License CC_BY_SA_1_0 = new License("Naamsvermelding-GelijkDelen_1.0", null, true);
    public static License CC_BY_SA_2_0 = new License("Naamsvermelding-GelijkDelen_2.0", null, true);
    public static License CC_BY_SA_3_0 = new License("Naamsvermelding-GelijkDelen_3.0", null, true);
    public static License CC_BY_SA_4_0 = new License("Naamsvermelding-GelijkDelen_4.0", null, true);
    public static License CC_BY_ND = new License("Naamsvermelding-GeenAfgeleideWerken", null, true);
    public static License CC_BY_ND_1_0 = new License("Naamsvermelding-GeenAfgeleideWerken_1.0", null, true);
    public static License CC_BY_ND_2_0 = new License("Naamsvermelding-GeenAfgeleideWerken_2.0", null, true);
    public static License CC_BY_ND_3_0 = new License("Naamsvermelding-GeenAfgeleideWerken_3.0", null, true);
    public static License CC_BY_ND_4_0 = new License("Naamsvermelding-GeenAfgeleideWerken_4.0", null, true);
    public static License CC_BY_NC = new License("Naamsvermelding-NietCommercieel", null, true);
    public static License CC_BY_NC_1_0 = new License("Naamsvermelding-NietCommercieel_1.0", null, true);
    public static License CC_BY_NC_2_0 = new License("Naamsvermelding-NietCommercieel_2.0", null, true);
    public static License CC_BY_NC_3_0 = new License("Naamsvermelding-NietCommercieel_3.0", null, true);
    public static License CC_BY_NC_4_0 = new License("Naamsvermelding-NietCommercieel_4.0", null, true);
    public static License CC_BY_NC_SA = new License("Naamsvermelding-NietCommercieel-GelijkDelen", null, true);
    public static License CC_BY_NC_SA_1_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_1.0", null, true);
    public static License CC_BY_NC_SA_2_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_2.0", null, true);
    public static License CC_BY_NC_SA_3_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_3.0", null, true);
    public static License CC_BY_NC_SA_4_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_4.0", null, true);
    public static License CC_BY_NC_ND = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken", null, true);
    public static License CC_BY_NC_ND_1_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_1.0", null, true);
    public static License CC_BY_NC_ND_2_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_2.0", null, true);
    public static License CC_BY_NC_ND_3_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_3.0", null, true);
    public static License CC_BY_NC_ND_4_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_4.0", null, true);
    public static License USA_GOV = new License("United States Government Work", null, true);
    //endregion

    private static final License[] ALL;

    static {
        List<License> alls = new ArrayList<>();
        for(Field field : License.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && License.class.isAssignableFrom(field.getType())) {
                try {
                    License license = (License) field.get(null);
                    license.id = field.getName();
                    alls.add(license);
                } catch (Exception ignored ) {

                }
            }
        }
        ALL =  alls.toArray(new License[alls.size()]);
    }


//
//    CC_BY("Naamsvermelding", "https://creativecommons.org/licenses/by/4.0/", true),
//    CC_BY_SA("Naamsvermelding-GelijkDelen", "https://creativecommons.org/licenses/by-sa/4.0/", true),
//    CC_BY_ND("Naamsvermelding-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nd/4.0/", true),
//    CC_BY_NC("Naamsvermelding-NietCommercieel", "https://creativecommons.org/licenses/by-nc/4.0/", true),
//    CC_BY_NC_SA("Naamsvermelding-NietCommercieel-GelijkDelen", "https://creativecommons.org/licenses/by-nc-sa/4.0/", true),
//    CC_BY_NC_ND("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nc-nd/4.0/", true),
//    USA_GOV("United States Government Work", "http://www.usa.gov/copyright.shtml", false)


    private String id;

    @NonNull
    private String displayName;

    @Getter
    private URI url;

    @Getter
    private boolean listed;

    public License (){}

    public License(String id) {
        this.id = id;
    }


    License(String displayName, String url, boolean listed) {

        this.displayName = displayName;
        this.url = url == null ? null : URI.create(url);
        this.listed = listed;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }


    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static License valueOfOrNull(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        } else {
            return new License(id);
        }

    }

    public static License getLicenseById(String id) {

        return Arrays.stream(values()).filter(l -> l.getId().equals(id)).findFirst().orElse(new License(id));
    }

    public static License[] values() {
        return ALL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        License license = (License) o;

        return id.equals(license.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
