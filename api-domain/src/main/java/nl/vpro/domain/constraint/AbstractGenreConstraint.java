package nl.vpro.domain.constraint;

import java.util.stream.Stream;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractGenreConstraint<T> extends AbstractTextConstraint<T> {

    {
        caseHandling = CaseHandling.ASIS;
    }
    public AbstractGenreConstraint() {

    }

    public AbstractGenreConstraint(String value) {
        super(value);
    }

    @Override
    @Pattern(regexp = "3\\.[0-9\\.]+")
    public void setValue(String s) {
        super.setValue(s);
    }

    @Override
    public String getESPath() {
        return "genres.id";
    }

    @Override
    public boolean isExact() {
        return value == null || !endsWith(value, "*");
    }

    @Override
    public String getWildcardValue() {
        return removeEnd(value, "*");
    }

    @Override
    public boolean test(T  t) {
        if (isExact()) {
            return getTermIds(t).anyMatch(g -> StringUtils.equals(value, g));
        } else {
            return getTermIds(t).anyMatch(g -> startsWith(g, getWildcardValue()));
        }
    }

    protected abstract Stream<String> getTermIds(T t);
}
