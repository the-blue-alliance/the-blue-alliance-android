package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Match;

import java.util.Comparator;

public class MatchSortByDisplayOrderComparator implements Comparator<Match> {
    @Override
    public int compare(Match match, Match match2) {
        return match.getDisplayOrder().compareTo(match2.getDisplayOrder());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
