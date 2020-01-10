package nl.vpro.domain;

public interface PersonInterface {

    String getGivenName();

    String getFamilyName();

    String getGtaaUri();

    /**
     * @since 5.12
     */
    default String getName() {
        String giveName = getGivenName();
        String familyName = getFamilyName();
        return stringValue(giveName, familyName);

    }

    /**
     * @since 5.11
     */
    static String stringValue(String givenName, String familyName) {
        if (familyName == null && givenName == null) {
            return null;
        }
        return (familyName == null ? "" : familyName) + (givenName == null ? "":  ", " + givenName);
    }

}
