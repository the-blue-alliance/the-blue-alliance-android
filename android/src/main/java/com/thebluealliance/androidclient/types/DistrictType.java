package com.thebluealliance.androidclient.types;

/* DO NOT CHANGE ORDER. */
public enum DistrictType {
    NO_DISTRICT,
    MICHIGAN,
    MID_ATLANTIC,
    NEW_ENGLAND,
    PACIFIC_NORTHWEST,
    INDIANA,
    CHESAPEAKE,
    NORTH_CAROLINA,
    GEORGIA;

    public static DistrictType fromEnum(int districtEnum) {
        switch (districtEnum) {
            default:
            case 0:
                return NO_DISTRICT;
            case 1:
                return MICHIGAN;
            case 2:
                return MID_ATLANTIC;
            case 3:
                return NEW_ENGLAND;
            case 4:
                return PACIFIC_NORTHWEST;
            case 5:
                return INDIANA;
            case 6:
                return CHESAPEAKE;
            case 7:
                return NORTH_CAROLINA;
            case 8:
                return GEORGIA;
        }
    }

    public static DistrictType fromAbbreviation(String abbrev) {
        switch (abbrev) {
            case "fim":
                return MICHIGAN;
            case "mar":
                return MID_ATLANTIC;
            case "ne":
                return NEW_ENGLAND;
            case "pnw":
                return PACIFIC_NORTHWEST;
            case "in":
                return INDIANA;
            case "chs":
                return CHESAPEAKE;
            case "nc":
                return NORTH_CAROLINA;
            case "pch":
                return GEORGIA;
            default:
                return NO_DISTRICT;
        }
    }

    public String getName() {
        switch (this) {
            default:
            case NO_DISTRICT:
                return "";
            case MICHIGAN:
                return "Michigan";
            case MID_ATLANTIC:
                return "Mid Atlantic";
            case NEW_ENGLAND:
                return "New England";
            case PACIFIC_NORTHWEST:
                return "Pacific Northwest";
            case INDIANA:
                return "Indiana";
            case CHESAPEAKE:
                return "Chesapeake";
            case NORTH_CAROLINA:
                return "North Carolina";
            case GEORGIA:
                return "Georgia";
        }
    }

    public String getAbbreviation() {
        switch (this) {
            default:
            case NO_DISTRICT:
                return "";
            case MICHIGAN:
                return "fim";
            case MID_ATLANTIC:
                return "mar";
            case NEW_ENGLAND:
                return "ne";
            case PACIFIC_NORTHWEST:
                return "pnw";
            case INDIANA:
                return "in";
            case CHESAPEAKE:
                return "chs";
            case NORTH_CAROLINA:
                return "nc";
            case GEORGIA:
                return "pch";
        }
    }

}
