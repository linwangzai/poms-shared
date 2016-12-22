/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import nl.vpro.domain.api.media.MediaSearch;
import nl.vpro.domain.api.media.MemberRefSearch;
import nl.vpro.domain.api.page.AssociationSearch;
import nl.vpro.domain.api.page.PageSearch;

/**
 * A Search interface but JAXB won't handle interfaces
 *
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({MediaSearch.class, PageSearch.class, MemberRefSearch.class, AssociationSearch.class})
@XmlTransient
public abstract class AbstractSearch extends AbstractMatcher {

    public abstract boolean hasSearches();

}
