package com.thebluealliance.androidclient.helpers;

import java.util.ArrayList;

/**
 * Created by Nathan on 11/7/2014.
 */
public class ModelNotificationFavoriteSettings {
    public String modelKey = "";
    public ModelHelper.MODELS modelType = null;
    public boolean isFavorite = false;
    public ArrayList<String> enabledNotifications = new ArrayList<>();
}
