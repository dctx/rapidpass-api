package ph.devcon.rapidpass.enums;

/**
 * Enum representing a type in the LookupTable
 */
public enum LookupType {


    APOR("APOR"),
    ID_TYPE_INDIVIDUAL("IDTYPE-I"),
    ID_TYPE_VEHICLE("IDTYPE-V");

    private final String databaseValue;

    LookupType(final String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public final String toDBType() {
        return databaseValue;
    }

}
