/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.AbstractSearch;
import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.TextMatcherList;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberRefSearchType")
public class MemberRefSearch extends AbstractSearch {

    @Valid
    @Getter
    @Setter
    private TextMatcherList mediaIds;

    @Valid
    @Getter
    @Setter
    private TextMatcherList types;

    public MemberRefSearch() {

    }
    @lombok.Builder
    private MemberRefSearch(Match match, TextMatcherList mediaIds, TextMatcherList types) {
        super(match);
        this.mediaIds = mediaIds;
        this.types = types;

    }



    @Override
    public boolean hasSearches() {
        return atLeastOneHasSearches(mediaIds, types);
    }
}
