package nl.vpro.domain.api.media;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.api.NameableSearchableFacet;


/**
 * @author lies
 * @since 5.5
 */


@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "mediaTitleFacetType", propOrder = {"name", "subSearch"})
@JsonPropertyOrder({"name","subSearch"})
public class TitleFacet implements NameableSearchableFacet<TitleMatcher>  {

    private String name;

    @Valid
    private TitleMatcher subSearch;

    public TitleFacet() {
    }


    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @Override
    @XmlElement
    public TitleMatcher getSubSearch() {
        return subSearch;
    }

    @Override
    public void setSubSearch(TitleMatcher subSearch) {
        this.subSearch = subSearch;
    }

    @XmlAttribute
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }



}
