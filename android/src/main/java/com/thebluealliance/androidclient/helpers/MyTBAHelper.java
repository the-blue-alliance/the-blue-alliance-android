package com.thebluealliance.androidclient.helpers;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.JSONManager;

import java.util.Map;

/**
 * File created by phil on 8/13/14.
 */
public class MyTBAHelper {

    private static final String INTENT_PACKAGE = "package", INTENT_CLASS = "class", INTENT_EXTRAS = "extras";
    
    public static String createKey(String userName, String modelKey){
        return userName+":"+modelKey;
    }

    public static String getFavoritePreferenceKey() {
        return "favorite";
    }
    
    public static String serializeIntent(Intent intent){
        JsonObject data = new JsonObject(), extras = new JsonObject();
        ComponentName activity = intent.getComponent();
        data.addProperty(INTENT_PACKAGE, activity.getPackageName());
        data.addProperty(INTENT_CLASS, activity.getClassName());
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            for (String key : bundle.keySet()) {
                extras.addProperty(key, bundle.getString(key));
            }
        }
        data.add(INTENT_EXTRAS, extras);
        Log.d(Constants.LOG_TAG, "Serialized: "+data.toString());
        return data.toString();
    }
    
    public static Intent deserializeIntent(String input){
        JsonObject data = JSONManager.getasJsonObject(input);
        Intent intent = new Intent();
        String pack = data.get(INTENT_PACKAGE).getAsString();
        String cls = data.get(INTENT_CLASS).getAsString();
        Log.d(Constants.LOG_TAG, pack+"/"+cls);
        intent.setClassName(pack, cls);
        JsonObject extras = data.get(INTENT_EXTRAS).getAsJsonObject();
        for(Map.Entry<String, JsonElement> extra: extras.entrySet()){
            intent.putExtra(extra.getKey(), extra.getValue().getAsString());
        }
        return intent;
    }

}
