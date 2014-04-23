package com.thebluealliance.androidclient.datatypes;

import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 4/22/14.
 */
public class MatchGroup extends ListGroup {

    public String string;
    public final List<Match> children = new ArrayList<Match>();
    public final List<String> children_keys = new ArrayList<String>();

    public MatchGroup(String string) {
        super(string);
    }
}
