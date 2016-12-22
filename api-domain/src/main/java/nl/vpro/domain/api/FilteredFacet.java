/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
public interface FilteredFacet<T extends AbstractSearch> {
    T getFilter();

    void setFilter(T filter);
}
