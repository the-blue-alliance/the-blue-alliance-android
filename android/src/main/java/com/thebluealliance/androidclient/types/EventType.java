package com.thebluealliance.androidclient.types;

import com.thebluealliance.androidclient.helpers.EventHelper;

/**
 * Do not insert any new entries above the existing enums!!!
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
                return "";
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

    public static EventType fromString(String str) {
        switch (str) {
            case "Regional":
                return REGIONAL;
            case "District":
                return DISTRICT;
            case "District Championship":
                return DISTRICT_CMP;
            case "Championship Division":
                return CMP_DIVISION;
            case "Championship Finals":
                return CMP_FINALS;
            case "Offseason":
                return OFFSEASON;
            case "Preseason":
                return PRESEASON;
            default:
                return NONE;
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

    public static EventType fromLabel(String label) {
        switch (label) {
            case EventHelper.OFFSEASON_LABEL:
                return OFFSEASON;
            case EventHelper.PRESEASON_LABEL:
                return PRESEASON;
            case EventHelper.CHAMPIONSHIP_LABEL:
                return CMP_DIVISION;
            case EventHelper.WEEKLESS_LABEL:
                return NONE;
            default:
                return REGIONAL;
        }
    }
}
