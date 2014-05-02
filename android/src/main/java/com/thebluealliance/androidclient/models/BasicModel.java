package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.datatypes.ListElement;

/**
 * File created by phil on 4/28/14.
 */
public interface BasicModel {

    public ListElement render();

    /*
     * When we're ready for it, I can foresee wanting easy inflating/deflating with json. Uncomment whenever that is...
    public JsonObject toJson();
    public static BasicModel fromJson(Json Object in);
     */

}
