package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnumValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.XmlValued;
import nl.vpro.domain.media.bind.AspectRatioToString;

@JsonSerialize(using = AspectRatioToString.Serializer.class)
@JsonDeserialize(using = AspectRatioToString.Deserializer.class)
public enum AspectRatio implements XmlValued {

    @XmlEnumValue("4:3")
    _4x3(4, 3),

    @XmlEnumValue("16:9")
    _16x9(16, 9),

    @XmlEnumValue("xCIF")
    _xCIF(352, 288) {
        @Override
        public String toString() {
            return "xCIF";
        }
    };

    private final int w;
    private final int h;

    AspectRatio(int w, int h) {
        int gcd = gcd(w, h);
        this.w = w / gcd;
        this.h = h / gcd;
    }


    public static AspectRatio fromDimension(Integer w, Integer h) {
        if (w == null || h == null) {
            return null;
        }
        int gcd = gcd(w, h);
        if (gcd == 0) return null;
        int aw = w / gcd;
        int ah = h / gcd;
        for (AspectRatio a : AspectRatio.values()) {
            if (aw == a.w && ah == a.h) {
                return a;
            }
        }

        return null;
    }

    /**
     * @since 1.8
     */
    public static AspectRatio fromString(String s) {
        if (s == null || s.length() == 0) return null;
        String[] split = s.split("\\s*[^\\d]\\s*", 2);
        return fromDimension(
            Integer.parseInt(split[0].trim()),
            Integer.parseInt(split[1].trim()));
    }

    @Override
    public String toString() {
        return w + ":" + h;
    }

    /**
     * @return the greatest common divisor of a and b.
     */
    private static int gcd(int a, int b) {
        // Euclid's algorithm
        return b == 0 ? (a == 0 ? 1 : a) : gcd(b, a % b);
    }

}
