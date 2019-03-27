package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Award;

import java.util.Comparator;

public class AwardSortComparator implements Comparator<Award> {

    private static final int ORDER_UNDEFINED = Integer.MAX_VALUE;

    @Override
    public int compare(Award award1, Award award2) {
        int priority1 = getAwardOrder(award1);
        int priority2 = getAwardOrder(award2);

        if (priority1 == ORDER_UNDEFINED && priority2 == ORDER_UNDEFINED) {
            return award1.getName().compareTo(award2.getName());
        } else {
            return priority1 - priority2;
        }
    }

    /**
     * Get the priority order for a given award.
     * If there is not particular priority for the award, returns {@link #ORDER_UNDEFINED}.
     */
    private int getAwardOrder(Award award) {
        switch (award.getAwardType()) {
            case 0: return 0; // Chairman's
            case 6: return 1; // Founder's
            case 9: return 2; // Engineering Inspiration
            case 10: return 3; // Rookie All Star
            case 3: return 4; // Woodie Flowers
            case 5: return 5; // Volunteer of the Year
            case 4: return 6; // Dean's List
            case 1: return 7; // Winner
            case 2: return 8; // Finalist
            default: return ORDER_UNDEFINED;
        }
    }
}
