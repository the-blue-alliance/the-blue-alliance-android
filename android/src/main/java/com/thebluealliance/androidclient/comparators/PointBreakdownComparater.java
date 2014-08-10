package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.DistrictPointBreakdown;

import java.util.Comparator;

/**
 * File created by phil on 7/26/14.
 */
public class PointBreakdownComparater implements Comparator<DistrictPointBreakdown> {
    @Override
    public int compare(DistrictPointBreakdown lhs, DistrictPointBreakdown rhs) {
        return ((Integer) rhs.getTotalPoints()).compareTo(lhs.getTotalPoints());
    }
}
