package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Favorite;

import java.util.Comparator;

/**
 * Created by phil on 12/2/14.
 */
public class FavoriteSortByModelComparator implements Comparator<Favorite> {
    @Override
    public int compare(Favorite lhs, Favorite rhs) {
        if(lhs.getModelEnum() == rhs.getModelEnum()){
            return rhs.getModelKey().compareTo(lhs.getModelKey());
        }else {
            return ((Integer) lhs.getModelEnum()).compareTo(rhs.getModelEnum());
        }
    }
}
