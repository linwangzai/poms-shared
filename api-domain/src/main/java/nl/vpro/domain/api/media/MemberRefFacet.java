/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.SearchableFacet;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "memberRefFacetType")
public class MemberRefFacet extends MediaFacet implements SearchableFacet<MediaSearch, MemberRefSearch> {

    @Valid
    MemberRefSearch subSearch;

    public MemberRefFacet() {
    }

    public MemberRefFacet(Integer threshold, FacetOrder sort, Integer max) {
        super(threshold, sort, max);
    }

    @lombok.Builder(builderMethodName = "refBuilder")
    private MemberRefFacet(Integer threshold, FacetOrder sort, Integer max, MemberRefSearch subSearch) {
        super(threshold, sort, max);
        this.subSearch = subSearch;
    }


    @Override
    @XmlElement
    public MemberRefSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(MemberRefSearch subSearch) {
        this.subSearch = subSearch;
    }
}
