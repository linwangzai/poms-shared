/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Location;

@XmlType(name = "locationSearchResultType")
public class LocationSearchResult extends SearchResult<Location> {

    public LocationSearchResult() {
        super();
    }
    public LocationSearchResult(final Integer count, final List<Location> result) {
        super(count, result);
    }

}
