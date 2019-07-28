package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "targetGroupsType")
@Getter
@Setter
public class TargetGroups  extends AbstractMediaObjectOwnableList<TargetGroups, TargetGroup> {


    public TargetGroups() {}

    @lombok.Builder(builderClassName = "Builder")
    private TargetGroups(@NonNull @Singular  List<TargetGroupType> values, @NonNull OwnerType owner) {
        this.values = values.stream().map(TargetGroup::new).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }


    public TargetGroups copy() {
        return new TargetGroups(values.stream().map(TargetGroup::getValue).collect(Collectors.toList()), owner);
    }



    @Override
    @org.checkerframework.checker.nullness.qual.NonNull
    @XmlElement(name="targetGroup")
    @JsonIgnore
    public List<TargetGroup> getValues() {
        return super.getValues();
    }
    @Override
    public void setValues(List<TargetGroup> list) {
        super.setValues(list);
    }
}
