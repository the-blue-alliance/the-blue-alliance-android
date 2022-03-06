package com.thebluealliance.androidclient.helpers;

import androidx.work.Data;

import com.thebluealliance.androidclient.types.ModelType;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nullable;

public class ModelNotificationFavoriteSettings {
    public String modelKey = "";
    public ModelType modelType = null;
    public boolean isFavorite = false;
    public ArrayList<String> enabledNotifications = new ArrayList<>();

    public Data toWorkData() {
        String[] notifications = new String[enabledNotifications.size()];
        enabledNotifications.toArray(notifications);

        return new Data.Builder()
                .putString("modelKey", modelKey)
                .putInt("modelType", modelType.getEnum())
                .putBoolean("isFavorite", isFavorite)
                .putStringArray("enabledNotifications", notifications)
                .build();
    }

    public static ModelNotificationFavoriteSettings fromWorkData(Data data) {
        ModelNotificationFavoriteSettings model = new ModelNotificationFavoriteSettings();
        model.modelKey = data.getString("modelKey");
        model.modelType = ModelHelper.getModelFromEnum(data.getInt("modelType", -1));
        model.isFavorite = data.getBoolean("isFavorite", false);
        @Nullable String[] notifications = data.getStringArray("enabledNotifications");
        if (notifications != null) {
            model.enabledNotifications = new ArrayList<>(Arrays.asList(notifications));
        }
        return model;
    }
}
