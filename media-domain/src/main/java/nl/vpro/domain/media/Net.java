package nl.vpro.domain.media;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlValue;

import nl.vpro.domain.Displayable;

@Entity
@SuppressWarnings("serial")
public class Net implements Displayable, Serializable {

    @Id
    @NotNull(message = "type is required")
    @javax.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,12}", message = "type should conform to: [A-Z0-9_-]{2,12}")
    @XmlValue
    private String id;

    @Column
    private String displayName;

    protected Net() {
        // hibernate needs default constructor (somewhy)
    }

    public Net(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Net(String id) {
        this.id = id;
        this.displayName = null;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.id + (this.displayName == null ? "" : " (" + this.displayName + ")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Net)) return false;

        Net net = (Net) o;

        return id.equals(net.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
