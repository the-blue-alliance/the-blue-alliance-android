package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.Subscription;

import java.util.Comparator;

public class SubscriptionSortByModelComparator implements Comparator<Subscription> {
    @Override
    public int compare(Subscription lhs, Subscription rhs) {
        if (lhs.getModelEnum() == rhs.getModelEnum()) {
            if (lhs.getModelType() == ModelType.TEAM) {
                return Integer.compare(TeamHelper.getTeamNumber(lhs.getModelKey()),
                  TeamHelper.getTeamNumber(rhs.getModelKey()));
            }
            return lhs.getModelKey().compareTo(rhs.getModelKey());
        } else {
            return ((Integer) lhs.getModelEnum()).compareTo(rhs.getModelEnum());
        }
    }
}
