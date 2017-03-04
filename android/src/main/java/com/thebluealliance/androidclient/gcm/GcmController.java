package com.thebluealliance.androidclient.gcm;

import com.thebluealliance.androidclient.config.AppConfig;

import android.content.SharedPreferences;

import javax.inject.Singleton;

@Singleton
public class GcmController {

    public static final String OS_ANDROID = "android";
    public static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";
    public static final String PREF_SENDER_ID = "gcm_senderId";

    private final AppConfig mAppConfig;
    private final SharedPreferences mSharedPreferences;

    public GcmController(AppConfig appConfig, SharedPreferences sharedPreferences) {
        mAppConfig = appConfig;
        mSharedPreferences = sharedPreferences;
    }

    public String getSenderId() {
        return mAppConfig.getString(PREF_SENDER_ID);
    }

    public String getRegistrationId() {
        return mSharedPreferences.getString(PROPERTY_GCM_REG_ID, "");
    }

    public void storeRegistrationId(String id) {
        mSharedPreferences.edit().putString(PROPERTY_GCM_REG_ID, id).apply();
    }
}
