package nl.vpro.domain.media;

import java.io.Serializable;
import java.util.function.Supplier;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.validation.URI;

@SuppressWarnings("serial")
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "websiteType")
public class Website implements UpdatableIdentifiable<Long, Website>, Serializable, Supplier<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @URI(message = "{nl.vpro.constraints.URI}")
    @XmlValue
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    private String url;

    public Website() {
    }

    public Website(String url) {
        this.url = url;
    }

    public Website(Website source) {
        this(source.getUrl());
    }

    public static Website copy(Website source) {
        if(source == null) {
            return null;
        }
        return new Website(source);
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Under normal operation this should not be used!
     * <p/>
     * While testing it sometimes comes in handy to be able to set an Id to simulate
     * a persisted object.
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void update(Website from) {
        url = from.getUrl();
    }

    /**
     * Checks for database identity or object identity if one side of the comparison can
     * not supply a database identity. It is advised to override this method with a more
     * accurate test which should not rely on database identity. You can rely on this
     * criterion when equality can not be deducted programmatic and a real and final
     * check is in need of human interaction. In essence this check then states that two
     * objects are supposed to be different if they can't supply the same database Id.
     *
     * @param object the object to compare with
     * @return true if both objects are equal
     */
    @Override
    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }

        if(this.getClass() != object.getClass()) {
            return false;
        }

        Website that = (Website)object;

        if(this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        return this.url == null ? that.url == null : this.url.equals(that.url);
    }

    @Override
    public int hashCode() {
        if(id != null) {
            return id.hashCode();
        }

        if(url != null) {
            return url.hashCode();
        }

        return System.identityHashCode(this);
    }

    /*
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
*/
    @Override
    public String get() {
        return url;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("url", url)
            .toString();
    }
}
