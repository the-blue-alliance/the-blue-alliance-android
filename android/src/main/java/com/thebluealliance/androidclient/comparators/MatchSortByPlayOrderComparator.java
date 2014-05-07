package com.thebluealliance.androidclient.comparators;

import com.thebluealliance.androidclient.models.Match;

import java.util.Comparator;

/**
 * File created by phil on 5/4/14.
 */
public class MatchSortByPlayOrderComparator implements Comparator<Match> {
    @Override
    public int compare(Match match, Match match2) {
        return match.getPlayOrder().compareTo(match2.getPlayOrder());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
