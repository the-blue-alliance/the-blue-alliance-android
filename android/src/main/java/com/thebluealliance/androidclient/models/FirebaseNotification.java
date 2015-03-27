package com.thebluealliance.androidclient.models;

import java.util.Map;

/**
 * Created by phil on 3/27/15.
 */
public class FirebaseNotification {

    private String time;
    private Map<String, Object> payload;

    public FirebaseNotification(){

    }

    public Map<String, Object> getPayload(){
        return payload;
    }

    public String getTime(){
        return time;
    }

}
