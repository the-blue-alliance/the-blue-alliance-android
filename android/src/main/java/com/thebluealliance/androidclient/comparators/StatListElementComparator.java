package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;

import java.util.Comparator;

public class StatListElementComparator implements Comparator<ListItem> {

    private String mStatToSortBy;

    public StatListElementComparator(String statToSortBy) {
        mStatToSortBy = statToSortBy;
    }

    @Override
    public int compare(ListItem lhs, ListItem rhs) {
        if (!(lhs instanceof StatsListElement) || !(rhs instanceof StatsListElement)) {
            return 0;
        }
        StatsListElement left = (StatsListElement) lhs;
        StatsListElement right = (StatsListElement) rhs;

        switch (mStatToSortBy) {
            case "team":
                return Integer.compare(left.getTeamNumber(), right.getTeamNumber());
            case "opr":
            default:
                return Double.compare(right.getOpr(), left.getOpr());
            case "dpr":
                return Double.compare(left.getDpr(), right.getDpr());
            case "ccwm":
                return Double.compare(right.getCcwm(), left.getCcwm());
        }
    }
}
