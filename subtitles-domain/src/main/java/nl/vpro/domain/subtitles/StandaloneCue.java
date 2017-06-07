package nl.vpro.domain.subtitles;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.LocaleAdapter;

import static nl.vpro.i18n.Locales.DUTCH;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlRootElement(name = "standaloneCue")
@ToString(includeFieldNames = false, callSuper = true, of = {"language"})
@EqualsAndHashCode(callSuper = true)
@Getter
public class StandaloneCue extends Cue {

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    private Locale language;

    @XmlAttribute
    private SubtitlesType type = SubtitlesType.CAPTION;


    public static StandaloneCue translation(Cue cue, Locale locale) {
        return new StandaloneCue(cue, locale, SubtitlesType.TRANSLATION);
    }


    public static StandaloneCue tt888(Cue cue) {
        return new StandaloneCue(cue, DUTCH, SubtitlesType.CAPTION);
    }

    public static StandaloneCue of(Cue cue, SubtitlesId subtitles) {
        return new StandaloneCue(cue, subtitles.getLanguage(), subtitles.getType());
    }

    protected StandaloneCue() {

    }

    public StandaloneCue(Cue cue, Locale language, SubtitlesType type) {
        super(cue);
        this.language = language;
        this.type = type;
    }

    @lombok.Builder(builderClassName = "Builder", builderMethodName = "standaloneBuilder")
    StandaloneCue(String parent,
                  int sequence,
                  Duration start,
                  Duration end,
                  String content,
                  Locale language,
                  SubtitlesType type) {
        super(Cue.builder().parent(parent).sequence(sequence).start(start).end(end).content(content).build());
        this.language = language;
        this.type = type;

    }


    @XmlTransient
    public String getId() {
        return getParent() + "\t" + getType() + "\t" + getLanguage() + "\t" + getSequence();
        //return getParent() + "_" + getType() + "_" + getLocale() + "_" + getSequence();
    }
    @XmlTransient
    public SubtitlesId getSubtitlesId() {
        return SubtitlesId.builder().mid(parent).language(language).type(type).build();
    }

}
