package com.thebluealliance.androidclient.helpers;

import android.os.Bundle;

import com.thebluealliance.androidclient.types.ModelType;

import java.util.ArrayList;

/**
 * Created by Nathan on 11/7/2014.
 */
public class ModelNotificationFavoriteSettings {
    public String modelKey = "";
    public ModelType modelType = null;
    public boolean isFavorite = false;
    public ArrayList<String> enabledNotifications = new ArrayList<>();

    public static ModelNotificationFavoriteSettings readFromBundle(Bundle bundle) {
        ModelNotificationFavoriteSettings model = new ModelNotificationFavoriteSettings();

        if (bundle != null) {
            model.modelKey = bundle.getString("modelKey");
            model.modelType = ModelHelper.getModelFromEnum(bundle.getInt("modelType"));
            model.isFavorite = bundle.getBoolean("isFavorite");
            model.enabledNotifications = bundle.getStringArrayList("enabledNotifications");
        }

        return model;
    }

    public void writeToBundle(Bundle bundle) {
        bundle.putString("modelKey", modelKey);
        bundle.putInt("modelType", modelType.getEnum());
        bundle.putBoolean("isFavorite", isFavorite);
        bundle.putStringArrayList("enabledNotifications", enabledNotifications);
    }
}
