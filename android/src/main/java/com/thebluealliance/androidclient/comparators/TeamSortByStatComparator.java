package com.thebluealliance.androidclient.comparators;

import com.google.gson.JsonElement;

import java.util.Comparator;
import java.util.Map;

/**
 * File created by phil on 5/14/14.
 */
public class TeamSortByStatComparator implements Comparator<Map.Entry<String, JsonElement>> {
    @Override
    public int compare(Map.Entry<String, JsonElement> lhs, Map.Entry<String, JsonElement> rhs) {
        return Double.compare(lhs.getValue().getAsDouble(), rhs.getValue().getAsDouble());
    }
}
