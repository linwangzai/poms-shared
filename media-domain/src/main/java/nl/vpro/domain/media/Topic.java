package nl.vpro.domain.media;

import lombok.*;

import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.*;
import nl.vpro.domain.media.support.MediaObjectOwnableListItem;

/**
 * A Topic is a wrapper around a GTAARecord linking it to a Topics record.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(of = { "gtaaRecord" })
@EqualsAndHashCode(of = { "gtaaRecord" }, callSuper = false)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "topicType", propOrder = {"name", "scopeNotes", "gtaaUri", "gtaaStatus"})
public class Topic extends DomainObject implements MediaObjectOwnableListItem<Topic, Topics>, GTAARecordManaged {

    @ManyToOne(fetch = FetchType.LAZY)
    @XmlTransient
    private Topics parent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gtaa_uri", nullable = false)
    @XmlTransient
    private GTAARecord gtaaRecord;

    public Topic() {
        gtaaRecord = new GTAARecord();
    }

    @lombok.Builder
    private Topic(Long id,
                  String name,
                  @Singular List<String> scopeNotes,
                  @NonNull String uri,
                  GTAAStatus gtaaStatus) {

        this.id = id;
        this.gtaaRecord = GTAARecord.builder()
                .name(name)
                .scopeNotes(scopeNotes)
                .uri(uri)
                .status(gtaaStatus)
                .build();
    }

    @XmlElement
    @Override
    public String getName() {
        return GTAARecordManaged.super.getName();
    }

    @XmlElement(name = "scopeNote")
    @JsonProperty("scopeNotes")
    @Override
    public List<String> getScopeNotes() {
        return GTAARecordManaged.super.getScopeNotes();
    }

    @XmlAttribute
    @Override
    public GTAAStatus getGtaaStatus() {
        return GTAARecordManaged.super.getGtaaStatus();
    }

    @XmlAttribute
    @Override
    public String getGtaaUri() {
        return GTAARecordManaged.super.getGtaaUri();
    }

    @Override
    public int compareTo(Topic topic) {
        if (getGtaaUri() != null) {
            return getGtaaUri().compareTo(topic.getGtaaUri());
        }
        return 0;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Topic clone() {
        Topic clone = new Topic();
        clone.setParent(parent);
        clone.setGtaaRecord(gtaaRecord);
        return clone;
    }
}
