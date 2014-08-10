package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.thebluealliance.androidclient.Constants;

import java.io.IOException;
import java.util.UUID;

/**
 * File created by phil on 7/28/14.
 */
public class AccountHelper {

    private static final String PREF_ACTIVE_ACCOUNT = "chosen_account";
    private static final String PREF_PLUS_ID = "plus_profile_id_%s";
    private static final String PREF_PLUS_NAME = "plus_name_%s";
    private static final String PREF_PLUS_PIC = "plus_image_url_%s";
    private static final String PREF_PLUS_COVER_URL = "plus_cover_url_%s";
    private static final String PREF_GCM_KEY = "gcm_key_%s";

    public static void storeAccountId(Context context, GoogleApiClient client) {
        Person current = Plus.PeopleApi.getCurrentPerson(client);
        String id = current.getId(),
                userName = current.getDisplayName(),
                accountName = Plus.AccountApi.getAccountName(client),
                userPicUrl = current.getImage().getUrl();
        //coverUrl = current.getCover().getCoverPhoto().getUrl();
        Log.d(Constants.LOG_TAG, "Got user id: " + id + "\n" + userName + "\n" + accountName + "\n" + userPicUrl);
        setCurrentUser(context, accountName);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(String.format(PREF_PLUS_ID, accountName), id)
                .putString(String.format(PREF_PLUS_NAME, accountName), userName)
                .putString(String.format(PREF_PLUS_PIC, accountName), userPicUrl)
                        //.putString(String.format(PREF_PLUS_COVER_URL, accountName), coverUrl)
                .commit();
    }

    public static String getCurrentAccountId(Context context) {
        String currentUser = getCurrentUser(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(String.format(PREF_PLUS_ID, currentUser), null);
    }

    public static void setCurrentUser(Context context, String accountName) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_ACTIVE_ACCOUNT, accountName).commit();
    }

    public static String getCurrentUser(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_ACTIVE_ACCOUNT, "");
    }

    public static String getGCMKey(Context context, GoogleApiClient driveClient) {
        String currentUser = getCurrentUser(context);
        if (TextUtils.isEmpty(currentUser)) {
            Log.e(Constants.LOG_TAG, "No current user: can't get GCM Key");
            return null;
        }
        return getGCMKey(context, currentUser, driveClient);
    }

    public static String getGCMKey(Context context, String accountName, GoogleApiClient driveClient) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String gcmKey = prefs.getString(String.format(PREF_GCM_KEY, accountName), null);

        //if we don't have a key stored in sharedPreferences, check the user's drive
        if (gcmKey == null || gcmKey.isEmpty()) {
            try {
                gcmKey = DriveHelper.getUserSecret(driveClient);
                if (gcmKey != null && !gcmKey.isEmpty()) {
                    Log.d(Constants.LOG_TAG, "Read key from Drive: " + gcmKey);
                    prefs.edit().putString(String.format(PREF_GCM_KEY, accountName), gcmKey).commit();
                }
            } catch (IOException e) {
                Log.w(Constants.LOG_TAG, "Couldn't read user secret from drive");
            }
        }

        // if there is no current GCM key, generate a new random one
        if (gcmKey == null || gcmKey.isEmpty()) {
            gcmKey = UUID.randomUUID().toString();
            Log.d(Constants.LOG_TAG, "No GCM key on account " + accountName + ". Generating random one.");
            setGcmKey(context, accountName, gcmKey, driveClient);
        }

        return gcmKey;
    }

    public static void setGcmKey(Context context, String accountName, String gcmKey, GoogleApiClient driveClient) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(String.format(PREF_GCM_KEY, accountName), gcmKey).commit();
        DriveHelper.writeUserSecretToDrive(gcmKey, driveClient);
        Log.d(Constants.LOG_TAG, "Created secret GCM key for account " + accountName);
    }
}
