package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;

import rx.functions.Func1;

public class JsonObjectAndKeyCombiner implements Func1<JsonObject, KeyAndJson> {

    private String mKey;

    public JsonObjectAndKeyCombiner(String key) {
        mKey = key;
    }

    @Override
    public KeyAndJson call(JsonObject jsonElement) {
        return new KeyAndJson(mKey, jsonElement);
    }
}
