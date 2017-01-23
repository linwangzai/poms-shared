package nl.vpro.domain.i18n;

import lombok.ToString;

import javax.persistence.Entity;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
@ToString(callSuper = true)
public class TwitterRefTranslation extends TextTranslation<MediaObjectTranslation> {
}
