package nl.vpro.domain.subtitles;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.LocaleAdapter;

import static nl.vpro.i18n.Locales.DUTCH;

/**
 * @author Michiel Meeuwissen
 * @since 4.7.7
 */
@XmlRootElement(name = "id")
@XmlAccessorType(XmlAccessType.NONE)
public class SubtitlesId implements Serializable {

    @XmlAttribute
    @Getter
    @Setter
    private String mid;

    @XmlAttribute
    @Getter
    @Setter
    private SubtitlesType type = SubtitlesType.CAPTION;

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @Getter
    @Setter
    private Locale language;

    public static SubtitlesId tt888Caption(String mid) {
        return new SubtitlesId(mid, DUTCH, SubtitlesType.CAPTION);
    }

    public SubtitlesId()  {

    }

    @lombok.Builder(builderClassName = "Builder")
    public SubtitlesId(String mid, Locale language, SubtitlesType type) {
        this.mid = mid;
        this.language = language;
        if (type != null) {
            this.type = type;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubtitlesId that = (SubtitlesId) o;

        if (!mid.equals(that.mid)) return false;
        if (type != that.type) return false;
        return language.equals(that.language);

    }

    @Override
    public int hashCode() {
        int result = mid.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + language.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return mid + "\t" + type + "\t" + language;
    }
}
