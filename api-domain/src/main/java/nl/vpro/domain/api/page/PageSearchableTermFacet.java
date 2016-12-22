/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.SearchableFacet;
import nl.vpro.domain.api.TermSearch;

/**
 * @author Michiel Meeuwissen
 * @since 3.6
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "pageSearchableTermFacetType")
public class PageSearchableTermFacet extends PageFacet implements SearchableFacet<TermSearch> {

    @Valid
    TermSearch subSearch;

    public PageSearchableTermFacet() {
    }

    public PageSearchableTermFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    @XmlElement
    public TermSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(TermSearch subSearch) {
        this.subSearch = subSearch;
    }
}
