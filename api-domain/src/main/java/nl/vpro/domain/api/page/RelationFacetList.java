/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.AbstractFacet;
import nl.vpro.domain.api.SearchableFacet;
import nl.vpro.domain.api.jackson.page.RelationFacetListJson;

/**
 * @author Michiel Meeuwissen
 * @since 4.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageRelationFacetListType", propOrder = {"filter", "subSearch", "facets"})
@JsonSerialize(using = RelationFacetListJson.Serializer.class)
@JsonDeserialize(using = RelationFacetListJson.Deserializer.class)
public class RelationFacetList extends AbstractFacet<PageSearch> implements SearchableFacet<PageSearch, RelationSearch>, Iterable<RelationFacet> {

    @Getter
    @Setter
    @Valid
    private PageSearch filter;

    @Getter
    @Setter
    @Valid
    private RelationSearch subSearch;

    @XmlElement(name = "facet")
    @Valid
    protected List<RelationFacet> facets;

    public RelationFacetList() {
    }

    public RelationFacetList(List<RelationFacet> facets) {
        this.facets = facets;
    }



    @lombok.Builder
    private RelationFacetList(
        @Singular  List<RelationFacet> facets,
        RelationSearch subSearch,
        PageSearch filter
        ) {
        this.facets = facets;
        this.filter = filter;
        this.subSearch = subSearch;
    }


    /**
     * Use iterator if you want to initialise the facet names. Clients may supply there own custom name, but
     * this is optional
     */
    public List<RelationFacet> getFacets() {
        return facets;
    }

    public void setFacets(List<RelationFacet> facets) {
        this.facets = facets;
    }

    public boolean isEmpty() {
        return facets == null || facets.isEmpty();
    }

    public int size() {
        return facets == null ? 0 : facets.size();
    }

    @Override
    public Iterator<RelationFacet> iterator() {
        return new Iterator<RelationFacet>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return facets != null && index < facets.size();
            }

            @Override
            public RelationFacet next() {
                RelationFacet relationFacet = facets.get(index++);
                if(relationFacet.getName() == null) {
                    relationFacet.setName("relations" + index);
                }
                return relationFacet;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Unsupported");
            }
        };
    }
}
