package nl.vpro.domain.media;

import lombok.Getter;

import java.util.Optional;

import javax.xml.bind.annotation.XmlEnum;

import nl.vpro.domain.Displayable;
import nl.vpro.i18n.Locales;
import nl.vpro.i18n.LocalizedString;

@XmlEnum
public enum RoleType  implements Displayable {

    DIRECTOR("Regisseur", "Regie", "Regisseur"),
    CHIEF_EDITOR("Eindredactie", "EindRedactie", "Eindredacteur"),
    EDITOR("Redacteur" , "Redactie", "Redacteur"),
    PRESENTER("Presentator", "Presentatie", "Presentator"),
    INTERVIEWER("Interviewer", "Interview", "Interviewer"),
    PRODUCER("Productie", "Productie", "Producer"),
    RESEARCH("Research", "Research", "Researcher"),
    GUEST("Gast", "Te gast", "Gast"),
    REPORTER("Verslaggever", "Verslaggeving", "Verslaggever"),
    ACTOR("Acteur", "Acteerwerk", "Acteur"),
    COMMENTATOR("Commentaar", "Commentaar", "Commentator"),
    COMPOSER("Componist", "Compositie", "Componist"),
    SCRIPTWRITER("Scenario", "Scenario", "Scenarist"),
    /**
     * For radio shows this seems useful
     * @since 5.8
     */
    NEWS_PRESENTER("Nieuwslezer", "Nieuwslezing", "Nieuwslezer"),
     /**
     * For radio shows this seems useful
     * @since 5.9
     */
    SIDEKICK("Sidekick", "Sidekick", "Sidekick"),
    /**
     * https://jira.vpro.nl/browse/MSE-4371
     * @since 5.11
     */
    SUBJECT("Onderwerp", "Onderwerp", "Onderwerp"),
    /**
     * See https://jira.vpro.nl/browse/MSE-4371
     * @since 5.11
     */
    PARTICIPANT("Deelnemer", "Deelname", "Deelnemer"),
    UNDEFINED("Overig", "Overig", "Overig")
    ;

    @Getter
    private final String string;

    private final String personFunction;

    @Getter
    private final String role;

    RoleType(String string, String role, String personFunction) {
        this.string = string;
        this.role = role;
        this.personFunction = personFunction;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public String getDisplayName() {
        return personFunction;
    }
    @Override
    public Optional<LocalizedString> getPluralDisplayName() {
        return Optional.of(LocalizedString.of(role, Locales.NETHERLANDISH));
    }


    public static RoleType fromToString(String string) {
        for (RoleType role : RoleType.values()) {
            if (string.equals(role.toString())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
