package com.thebluealliance.androidclient.gcm;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.LocalProperties;
import com.thebluealliance.androidclient.mytba.MyTbaRegistrationService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import javax.inject.Singleton;

@Singleton
public class GcmController {

    public static final String OS_ANDROID = "android";
    private static final String PROPERTY_GCM_REG_ID = "gcm_registration_id";

    private final LocalProperties mLocalProperties;
    private final SharedPreferences mSharedPreferences;

    public GcmController(LocalProperties localProperties, SharedPreferences sharedPreferences) {
        mLocalProperties = localProperties;
        mSharedPreferences = sharedPreferences;
    }

    public String getSenderId() {
        return mLocalProperties.readLocalProperty("gcm.senderId");
    }

    public void registerIfNeeded(Context context) {
        final String registrationId = getRegistrationId();
        if (TextUtils.isEmpty(registrationId)) {
            // GCM has not yet been registered on this device
            Log.d(Constants.LOG_TAG, "GCM is not currently registered. Registering....");
            context.startService(new Intent(context, MyTbaRegistrationService.class));
        }
    }

    public String getRegistrationId() {
        return mSharedPreferences.getString(PROPERTY_GCM_REG_ID, "");
    }

    public void storeRegistrationId(String id) {
        mSharedPreferences.edit().putString(PROPERTY_GCM_REG_ID, id).apply();
    }
}
