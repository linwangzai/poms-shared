/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.ExtendedTextFacet;
import nl.vpro.domain.api.FacetOrder;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "extendedPageFacetType")
public class ExtendedPageFacet extends ExtendedTextFacet<PageSearch> {
    public ExtendedPageFacet() {
    }

    public ExtendedPageFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @XmlElement
    @Override
    public PageSearch getFilter() {
        return this.filter;
    }

    @Override
    public void setFilter(PageSearch filter) {
        this.filter = filter;
    }
}
