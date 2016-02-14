package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.DistrictPointBreakdown;

import java.util.Comparator;

public class PointBreakdownComparater implements Comparator<DistrictPointBreakdown> {
    @Override
    public int compare(DistrictPointBreakdown lhs, DistrictPointBreakdown rhs) {
        return ((Integer) rhs.getTotalPoints()).compareTo(lhs.getTotalPoints());
    }
}
