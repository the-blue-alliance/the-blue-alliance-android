package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonElement;

public class KeyAndJson {
    public final String key;
    public final JsonElement json;

    public KeyAndJson(String key, JsonElement json) {
        this.key = key;
        this.json = json;
    }
}
