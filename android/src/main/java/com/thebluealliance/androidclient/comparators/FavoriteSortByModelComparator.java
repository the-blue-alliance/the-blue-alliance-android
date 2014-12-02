package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Favorite;

import java.util.Comparator;

/**
 * Created by phil on 12/2/14.
 */
public class FavoriteSortByModelComparator implements Comparator<Favorite> {
    @Override
    public int compare(Favorite lhs, Favorite rhs) {
        return Integer.compare(lhs.getModelEnum(), rhs.getModelEnum());
    }
}
