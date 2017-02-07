package nl.vpro.domain.subtitles;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@XmlEnum
@XmlType(name = "subtitlesFormatEnum")
public enum SubtitlesFormat {
    WEBVTT,
    EBU,
    SRT
}
