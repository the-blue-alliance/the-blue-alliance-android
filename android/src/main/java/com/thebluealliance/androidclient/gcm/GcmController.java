package com.thebluealliance.androidclient.gcm;

import com.thebluealliance.androidclient.LocalProperties;

import android.content.SharedPreferences;

import javax.inject.Singleton;

@Singleton
public class GcmController {

    public static final String OS_ANDROID = "android";
    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    public static final String PREF_SENDER_ID = "gcm.senderId";

    private final LocalProperties mLocalProperties;
    private final SharedPreferences mSharedPreferences;

    public GcmController(LocalProperties localProperties, SharedPreferences sharedPreferences) {
        mLocalProperties = localProperties;
        mSharedPreferences = sharedPreferences;
    }

    public String getSenderId() {
        return mLocalProperties.readLocalProperty(PREF_SENDER_ID);
    }

    public String getRegistrationId() {
        return mSharedPreferences.getString(PROPERTY_GCM_REG_ID, "");
    }

    public void storeRegistrationId(String id) {
        mSharedPreferences.edit().putString(PROPERTY_GCM_REG_ID, id).apply();
    }
}
