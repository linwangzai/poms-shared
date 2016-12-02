package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum RoleType {

    DIRECTOR {

        @Override
        public String toString() {
            return "Regisseur";
        }
    },
    CHIEF_EDITOR {

        @Override
        public String toString() {
            return "Eindredactie";
        }
    },
    EDITOR {

        @Override
        public String toString() {
            return "Redacteur";
        }
    },
    PRESENTER {

        @Override
        public String toString() {
            return "Presentator";
        }
    },
    INTERVIEWER {

        @Override
        public String toString() {
            return "Interviewer";
        }
    },
    PRODUCER {

        @Override
        public String toString() {
            return "Productie";
        }
    },
    RESEARCH {

        @Override
        public String toString() {
            return "Research";
        }
    },
    GUEST {

        @Override
        public String toString() {
            return "Gast";
        }
    },
    REPORTER {

        @Override
        public String toString() {
            return "Verslaggever";
        }
    },
    ACTOR {

        @Override
        public String toString() {
            return "Acteur";
        }
    },
    COMMENTATOR {

        @Override
        public String toString() {
            return "Commentaar";
        }
    },
    COMPOSER {

        @Override
        public String toString() {
            return "Componist";
        }
    },
    SCRIPTWRITER {

        @Override
        public String toString() {
            return "Scenario";
        }
    },
    UNDEFINED {
        @Override
        public String toString() {
            return "Overig";
        }
    };

    public static RoleType fromToString(String string) {
        for (RoleType role : RoleType.values()) {
            if (string.equals(role.toString())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
