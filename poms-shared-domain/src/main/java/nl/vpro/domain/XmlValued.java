package nl.vpro.domain;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * An enum can be made to extend this, which indicates that an extra method will be present {@link #getXmlValue()} which
 * will be the {@link XmlEnumValue} of the enum value.
 *
 * Normally this would be {@link Enum#name()}}, but sometimes this is overriden, via the said annotation and you need programmatic access to it.
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface XmlValued {

    default String getXmlValue() {
        if (this instanceof Enum) {
            Class<?> enumClass = getClass();
            String name = ((Enum) this).name();
            try {
                XmlEnumValue xmlValue = enumClass.getField(name).getAnnotation(XmlEnumValue.class);
                return xmlValue.value();
            } catch (NoSuchFieldException | NullPointerException e) {
                return name;
            }
        }
        throw new UnsupportedOperationException();
    }
}
