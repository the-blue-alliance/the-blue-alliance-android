package com.thebluealliance.androidclient.types;

/** Do not insert any new entries above the existing enums!!!
 * Things depend on their ordinal values, so you can only to the bottom of the list
 */
public enum EventType {
    NONE,
    REGIONAL,
    DISTRICT,
    DISTRICT_CMP,
    CMP_DIVISION,
    CMP_FINALS,
    OFFSEASON,
    PRESEASON;

    public String toString() {
        switch (ordinal()) {
            default:
            case 0:
                return "Other Events";
            case 1:
                return "Regional Events";
            case 2:
                return "District Events";
            case 3:
                return "District Championships";
            case 4:
                return "Championship Divisions";
            case 5:
                return "Championship Finals";
            case 6:
                return "Offseason Events";
            case 7:
                return "Preseason Events";
        }
    }

    public static EventType fromInt(int num) {
        switch (num) {
            case 0:
                return REGIONAL;
            case 1:
                return DISTRICT;
            case 2:
                return DISTRICT_CMP;
            case 3:
                return CMP_DIVISION;
            case 4:
                return CMP_FINALS;
            case 99:
                return OFFSEASON;
            case 100:
                return PRESEASON;
            default:
                return NONE;
        }
    }

}
