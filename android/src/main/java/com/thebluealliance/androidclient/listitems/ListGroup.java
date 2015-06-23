package com.thebluealliance.androidclient.listitems;

import com.thebluealliance.androidclient.interfaces.RenderableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 4/22/14.
 */
public class ListGroup {
    public String string;
    public final List<RenderableModel> children = new ArrayList<RenderableModel>();

    public ListGroup(String string) {
        this.string = string;
    }

    public void updateTitle(String string) {
        this.string = string;
    }

    public String getTitle() {
        return string;
    }

    public void clear() {
        children.clear();
    }
}
