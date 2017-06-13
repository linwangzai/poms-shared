package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.media.MediaObject;

import static org.apache.commons.lang3.StringUtils.*;


/**
 * @author machiel
 * @since 5.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "genreConstraintType")
public class GenreConstraint extends TextConstraint<MediaObject> {

    public GenreConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    public GenreConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "genres.id";
    }

    @Override
    public boolean isExact() {
        return value != null && !endsWith(value, "*");
    }
    @Override
    public String getWildcardValue() {
        return removeEnd(removeEnd(value, "*"), ".");
    }

    @Override
    public boolean test(MediaObject t) {
        if (isExact()) {
            return t.getGenres().stream().anyMatch(g -> equalsIgnoreCase(value, g.getTermId()));
        } else {
            return t.getGenres().stream().anyMatch(g -> startsWith(g.getTermId(), getWildcardValue()));
        }
    }
}
