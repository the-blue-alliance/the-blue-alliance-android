package com.thebluealliance.androidclient.comparators;

import com.google.gson.JsonElement;

import java.util.Comparator;
import java.util.Map;

public class TeamSortByStatComparator implements Comparator<Map.Entry<String, JsonElement>> {
    @Override
    public int compare(Map.Entry<String, JsonElement> lhs, Map.Entry<String, JsonElement> rhs) {
        return Double.compare(rhs.getValue().getAsDouble(), lhs.getValue().getAsDouble());
    }
}
