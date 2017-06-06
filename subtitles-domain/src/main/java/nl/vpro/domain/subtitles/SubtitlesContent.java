package nl.vpro.domain.subtitles;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;

/**
 * The subtitles as a String (to parse this use {@link SubtitlesUtil#parse(nl.vpro.domain.subtitles.Subtitles, boolean)}
 * @author Michiel Meeuwissen
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtitlesContentType")
@Slf4j
@Data
public class SubtitlesContent implements Serializable {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private SubtitlesFormat format = SubtitlesFormat.WEBVTT;


    @Column(nullable = false)
    @XmlValue
    private byte[] value;

    @Column
    @XmlAttribute
    private String charset;

    public SubtitlesContent() {
    }

    public SubtitlesContent(SubtitlesFormat format, String content) {
        this.format = format;
        this.value = content.getBytes(Charset.forName("UTF-8"));
        this.charset = "UTF-8";
    }


    @lombok.Builder
    private SubtitlesContent(SubtitlesFormat format, byte[] content, Charset charset) {
        this.format = format;
        this.value = content;
        this.charset = charset.name();
    }

    public InputStream asStream() {
        return new ByteArrayInputStream(value == null ? new byte[0] : value);
    }

}
