package com.thebluealliance.androidclient.types;

import android.support.annotation.StringRes;

import com.thebluealliance.androidclient.R;

/** Do not insert any new entries above the existing enums!!!
 * Things depend on their ordinal values, so you can only to the bottom of the list
 */
public enum EventType {
    NONE(999, R.string.event_type_other),
    REGIONAL(0, R.string.event_type_regional),
    DISTRICT(1, R.string.event_type_district),
    DISTRICT_CMP(3, R.string.event_type_dcmp),
    CMP_DIVISION(4, R.string.event_type_cmp_division),
    CMP_FINALS(5, R.string.event_type_cmp),
    OFFSEASON(6, R.string.event_type_offseason),
    PRESEASON(7, R.string.event_type_preseason),
    DCMP_DIVISION(2, R.string.event_type_dcmp_division);

    private final @StringRes int categoryName;
    private final int sortOrder;

    EventType(int sortOrder, @StringRes int categoryName) {
        this.sortOrder = sortOrder;
        this.categoryName = categoryName;
    }

    public @StringRes int getCategoryName() {
        return categoryName;
    }

    public int getSortOrder() {
        return sortOrder;
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
            case 5:
                return DCMP_DIVISION;
            case 99:
                return OFFSEASON;
            case 100:
                return PRESEASON;
            default:
                return NONE;
        }
    }

}
