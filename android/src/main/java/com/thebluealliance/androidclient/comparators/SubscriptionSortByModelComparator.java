package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Subscription;

import java.util.Comparator;

/**
 * Created by phil on 12/2/14.
 */
public class SubscriptionSortByModelComparator implements Comparator<Subscription> {
    @Override
    public int compare(Subscription lhs, Subscription rhs) {
        if (lhs.getModelEnum() == rhs.getModelEnum()) {
            return rhs.getModelKey().compareTo(lhs.getModelKey());
        } else {
            return ((Integer) lhs.getModelEnum()).compareTo(rhs.getModelEnum());
        }
    }
}
