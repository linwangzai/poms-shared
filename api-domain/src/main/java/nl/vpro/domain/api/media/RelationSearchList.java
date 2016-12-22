package nl.vpro.domain.api.media;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.RelationSearchListJson;
import nl.vpro.domain.media.Relation;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaRelationSearchListType")
@JsonSerialize(using = RelationSearchListJson.Serializer.class)
@JsonDeserialize(using = RelationSearchListJson.Deserializer.class)
public class RelationSearchList implements Predicate<Relation>, Iterable<RelationSearch> {

    @XmlElement(name = "relationSearch")
    @Valid
    protected List<RelationSearch> relationSearches = new ArrayList<>();

    public RelationSearchList() {
        // jaxb needs empty constructor
    }


    public RelationSearchList(RelationSearch... relationSearches) {
        this.relationSearches.addAll(Arrays.asList(relationSearches));
    }


    public List<RelationSearch> asList() {
        return relationSearches;
    }

    @Override
    public boolean test(@Nullable Relation input) {
        throw new UnsupportedOperationException("not used");
    }


    public boolean hasSearches() {
        for (RelationSearch relationSearch : this) {
            if (relationSearch.hasSearches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<RelationSearch> iterator() {
        return asList().iterator();
    }

    public int size() {
        return asList().size();
    }
}
