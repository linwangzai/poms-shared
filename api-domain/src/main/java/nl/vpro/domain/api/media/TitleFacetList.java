package nl.vpro.domain.api.media;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.SearchableFacet;

/**
 * @author lies
 * @since 5.5
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaTitleFacetListType", propOrder = {"filter", "subSearch",   "facets"})

public class TitleFacetList
    extends MediaFacet /* extending MediaFacet is mainly done for backwards compatibility */
    implements SearchableFacet<TitleSearch>, Iterable<TitleFacet> {

    @Valid
    private MediaSearch filter;

    @Valid
    private TitleSearch subSearch;


    @Valid
    @XmlElement(name = "title")
    protected List<TitleFacet> facets;

    public TitleFacetList() {
        super(null, null, null);
    }

    public TitleFacetList(List<TitleFacet> facets) {
        this.facets = facets;
    }

    @Override
    public MediaSearch getFilter() {
        return filter;
    }

    @Override
    public void setFilter(MediaSearch filter) {
        this.filter = filter;
    }

    @Override
    public TitleSearch getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(TitleSearch subSearch) {
        this.subSearch = subSearch;
    }

    /**
     * Use iterator if you want to initialise the facet names. Clients may supply there own custom name, but
     * this is optional
     */
    public List<TitleFacet> getFacets() {
        return facets;
    }

    public void setFacets(List<TitleFacet> facets) {
        this.facets = facets;
    }

    public boolean isEmpty() {
        return facets == null || facets.isEmpty();
    }

    public int size() {
        return facets == null ? 0 : facets.size();
    }



    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    public Iterator<TitleFacet> iterator() {
        return new Iterator<TitleFacet>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return facets != null && index < facets.size();
            }

            @Override
            public TitleFacet next() {
                TitleFacet titleFacet = facets.get(index++);
                if(titleFacet.getName() == null) {
                    titleFacet.setName("titles" + index);
                }
                return titleFacet;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Unsupported");
            }
        };
    }
}
