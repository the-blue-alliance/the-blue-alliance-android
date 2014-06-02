package com.thebluealliance.androidclient.datatypes;

import com.thebluealliance.androidclient.models.BasicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 4/22/14.
 */
public class ListGroup {
    public String string;
    public final List<BasicModel> children = new ArrayList<BasicModel>();
    public final List<String> childrenKeys = new ArrayList<String>();

    public ListGroup(String string) {
        this.string = string;
    }

    public void updateTitle(String string) {
        this.string = string;
    }

    public String getTitle() {
        return string;
    }

}