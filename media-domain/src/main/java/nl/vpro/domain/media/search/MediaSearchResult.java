/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.MediaObject;

@XmlType(name = "mediaSearchResultType")
public class MediaSearchResult extends AbstractSearchResult<MediaObject> {

    public MediaSearchResult() {
        super();
    }

    public MediaSearchResult(final Long count, final List<MediaObject> result) {
        super(count, result);
    }
}
