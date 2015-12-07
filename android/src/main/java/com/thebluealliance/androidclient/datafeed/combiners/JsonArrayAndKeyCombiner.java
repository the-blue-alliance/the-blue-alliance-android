package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;

import javax.inject.Singleton;

import rx.functions.Func1;

@Singleton
public class JsonArrayAndKeyCombiner implements Func1<JsonArray, KeyAndJson> {

    private String mKey;

    public JsonArrayAndKeyCombiner(String key) {
        mKey = key;
    }

    @Override
    public KeyAndJson call(JsonArray jsonElement) {
        return new KeyAndJson(mKey, jsonElement);
    }

}
