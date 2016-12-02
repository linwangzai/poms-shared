package nl.vpro.validation;

import java.util.Collection;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.junit.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintViolationsTest {

    @Test
    public void testHumanReadable() throws Exception {
        Title title = new Title("<h1>bla</h1", OwnerType.BROADCASTER, TextualType.MAIN);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Collection violations = factory.getValidator().validate(title);
        assertThat(ConstraintViolations.humanReadable(violations)).isEqualTo("\"title\" contains HTML");



    }
}
