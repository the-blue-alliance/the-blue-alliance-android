package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.Favorite;

import java.util.Comparator;

/**
 * Created by phil on 12/2/14.
 */
public class FavoriteSortByModelComparator implements Comparator<Favorite> {
    @Override
    public int compare(Favorite lhs, Favorite rhs) {
        if (lhs.getModelEnum() == rhs.getModelEnum()) {
            if (lhs.getModelType() == ModelHelper.MODELS.TEAM) {
                return Integer.compare(TeamHelper.getTeamNumber(lhs.getModelKey()),
                  TeamHelper.getTeamNumber(rhs.getModelKey()));
            }
            return lhs.getModelKey().compareTo(rhs.getModelKey());
        } else {
            return ((Integer) lhs.getModelEnum()).compareTo(rhs.getModelEnum());
        }
    }
}
