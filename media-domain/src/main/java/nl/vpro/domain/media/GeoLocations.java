package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;


/**
 *
 * @author Giorgio Vinci
 * @since 5.11
 */
@Entity
@XmlType(name = "geoLocationsType")
@Getter
@Setter
public class GeoLocations extends AbstractMediaObjectOwnableList<GeoLocations, GeoLocation> {

    public GeoLocations() {
    }
    public GeoLocations(@lombok.NonNull  OwnerType owner) {
        this.owner = owner;
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoLocations(
        @lombok.NonNull @Singular List<GeoLocation> values,
        @lombok.NonNull OwnerType owner) {
        this.values = values.stream().map(GeoLocation::clone).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }

    @Override
    @NonNull
    @XmlElement(name="geoLocation")
    @JsonIgnore
    public List<GeoLocation> getValues() {
        return values;
    }

    public void setValues(List<GeoLocation> list) {
        this.values = list;
    }

    @Override
    public GeoLocations clone() {
        return new GeoLocations(values.stream().map(GeoLocation::clone).collect(Collectors.toList()), owner);
    }
}
