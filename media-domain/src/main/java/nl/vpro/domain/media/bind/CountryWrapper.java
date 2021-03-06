package nl.vpro.domain.media.bind;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meeuw.i18n.regions.Region;
import org.meeuw.i18n.regions.RegionService;
import org.meeuw.i18n.regions.bind.jaxb.Code;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.i18n.Locales;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "countryType", propOrder = {"name"})
@JsonPropertyOrder({"code", "value"})
@Slf4j
public class CountryWrapper {

    @XmlAttribute
    @XmlJavaTypeAdapter(Code.class)
    private Region code;

    public CountryWrapper() {
    }

    public CountryWrapper(String code) {
        this.code = RegionService.getInstance().getByCode(code).orElse(new UnknownRegion(code));
    }

    public CountryWrapper(Region code) {
        if (code == null) {
            throw new IllegalArgumentException();
        }
        this.code = code;
    }

    @XmlValue
    @JsonProperty("value")
    public String getName() {
        return Locales.getCountryName(code,  Locales.NETHERLANDISH);
    }

    public void setName(String name) {
        // i hate jaxb
    }

    public Region getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ":" + getName();
    }

    public static class UnknownRegion implements Region {
        private final String code;

        public UnknownRegion(String code) {
            log.warn("Found unknown code {}", code);
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;

        }
        @Override
        public Type getType() {
            return Type.UNDEFINED;
        }

        @Override
        public String getName() {
            return code;

        }
    }
}
