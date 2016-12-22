/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Builder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static nl.vpro.domain.Xmlns.SEARCH_NAMESPACE;
import static nl.vpro.domain.media.search.MediaSortField.lastModified;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "schedulePagerType", namespace = SEARCH_NAMESPACE, propOrder = {
        "offset",
        "max",
        "sort",
        "order"
        })
public class SchedulePager extends Pager<ScheduleSortField> {


    @Builder
    public SchedulePager(long offset, Integer max, ScheduleSortField sort, Direction order) {
        super(offset, max, sort, order);
    }

    public SchedulePager() {
        super(0, null, ScheduleSortField.guideDay, Direction.ASC);
    }

    @Override
    @XmlElement
    public ScheduleSortField getSort() {
        return super.getSort();
    }


    @Override
    public void setSort(ScheduleSortField sort) {
        super.setSort(sort);
    }
}
