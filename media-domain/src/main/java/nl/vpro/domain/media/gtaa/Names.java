package nl.vpro.domain.media.gtaa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import nl.vpro.openarchives.oai.Label;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@AllArgsConstructor
@Data
@Builder
public class Names {

    protected String givenName;
    protected String familyName;

    // no args constructor for jaxb
    public Names() {

    }


    static Names of(Label label) {
        if (label == null) {
            return null;
        }
        return of(label.getValue());
    }

    static Names of(String label) {
        if (label == null) {
            return null;
        }
        NamesBuilder names = Names.builder();
        int splitIndex = label.indexOf(", ");

        if (splitIndex > 0) {
            names.givenName(label.substring(splitIndex + 2));
            names.familyName(label.substring(0, splitIndex));
        } else {
            names.familyName(label);
        }
        return names.build();
    }

}
