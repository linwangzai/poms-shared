package nl.vpro.validation;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.com.neovisionaries.i18n.LanguageAlpha3Code;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class LanguageValidatorTest {

    LanguageValidator validator = new LanguageValidator();

    @Test
    public void testIsValid() throws Exception {
        assertTrue(validator.isValid(new Locale("nl"), null));

    }

    @Test
    public void testIsValidCz() throws Exception {
        assertFalse(validator.isValid(new Locale("cz"), null));

    }

    @Test
    public void testIsValidZxx() throws Exception {
        assertTrue(validator.isValid(new Locale("zxx"), null));

    }

    @Test
    public void testIsValidJw() throws Exception {
        assertTrue(validator.isValid(new Locale("jw"), null));

    }

    @Test
    public void iso3() {
        assertTrue(validator.isValid(new Locale("dut"), null));

    }

    @Test
    @Ignore("fails")
    public void achterhoeks() {
        assertTrue(validator.isValid(new Locale("act"), null));

    }

    @Test
    @Ignore
    public void wiki() {
        Map<String, String> result = new TreeMap<>();
        for (String s : Locale.getISOLanguages()) {
            result.put(s, new Locale(s).getDisplayLanguage(new Locale("en")));
        }
        for (LanguageAlpha3Code s : LanguageAlpha3Code.values()) {
            result.put(s.toString(), s.getName());
        }
        // output sorted
        System.out.println("||code||name in english||name in dutch||name in language itself||");
        for (Map.Entry<String, String> e : result.entrySet()) {
            assertTrue(validator.isValid(new Locale(e.getKey()), null));
            String en = e.getValue();
            String nl = new Locale(e.getKey()).getDisplayLanguage(new Locale("nl"));
            if (nl.equals(en) || nl.equals(e.getKey())) {
                nl = " ";
            }
            String self = new Locale(e.getKey()).getDisplayLanguage(new Locale(e.getKey()));
            if (self.equals(en) || self.equals(e.getKey())) {
                self = " ";
            }
            System.out.println("|" + e.getKey() + "|" + en + "|"
                + nl + "|" + self + "|");
        }
    }
}
