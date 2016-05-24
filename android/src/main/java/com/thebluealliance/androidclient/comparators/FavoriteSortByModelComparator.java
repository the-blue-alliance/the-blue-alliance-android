package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.types.ModelType;

import java.util.Comparator;

public class FavoriteSortByModelComparator implements Comparator<Favorite> {
    @Override
    public int compare(Favorite lhs, Favorite rhs) {
        if (lhs.getModelEnum() == rhs.getModelEnum()) {
            if (lhs.getModelType() == ModelType.TEAM) {
                return Integer.compare(TeamHelper.getTeamNumber(lhs.getModelKey()),
                  TeamHelper.getTeamNumber(rhs.getModelKey()));
            } else if (lhs.getModelType() == ModelType.EVENT || lhs.getModelType() == ModelType.EVENTTEAM) {
                // Sort year newest first
                int yearCmp = compareEventYears(rhs.getModelKey(), lhs.getModelKey());
                if (yearCmp == 0) {
                    return EventHelper.getEventCode(lhs.getModelKey())
                      .compareTo(EventHelper.getEventCode(rhs.getModelKey()));
                }
                return yearCmp;
            } else {
                return lhs.getModelKey().compareTo(rhs.getModelKey());
            }
        } else {
            return ((Integer) lhs.getModelEnum()).compareTo(rhs.getModelEnum());
        }
    }

    private int compareEventYears(String key1, String key2) {
        return Integer.compare(EventHelper.getYear(key1), EventHelper.getYear(key2));
    }
}
