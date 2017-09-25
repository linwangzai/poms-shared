package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "multiplePageEntryType", propOrder = {})
@JsonPropertyOrder({})
public class MultiplePageEntry extends  MultipleEntry<Page> {

    public MultiplePageEntry() {

    }
    public MultiplePageEntry(String id, Page page) {
        super(id, page);
    }
}
