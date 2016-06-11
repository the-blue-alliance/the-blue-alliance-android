package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonElement;

import com.thebluealliance.androidclient.datafeed.KeyAndJson;

import rx.functions.Func1;

public class JsonAndKeyCombiner implements Func1<JsonElement, KeyAndJson> {

    private String mKey;

    public JsonAndKeyCombiner(String key) {
        mKey = key;
    }

    @Override
    public KeyAndJson call(JsonElement jsonElement) {
        return new KeyAndJson(mKey, jsonElement);
    }
}
