package com.thebluealliance.androidclient;

import android.content.Context;
import com.thebluealliance.androidclient.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Wrapper for the local tba.properties config file
 * This reads the file at creation time, so we don't have to keep a {@link Context} reference
 * Therefore, you should {@link dagger.Lazy} inject this class
 */
@Singleton
public class LocalProperties {

    private Properties mProperties;

    @Inject
    public LocalProperties(Context context) {
        loadPropertyFile(context);
    }

    public String readLocalProperty(String property) {
        return readLocalProperty(property, "");
    }

    public String readLocalProperty(String property, String defaultValue) {
        String debugKey = property + ".debug";
        if (BuildConfig.DEBUG && mProperties.containsKey(debugKey)) {
            return mProperties.getProperty(debugKey);
        }
        return mProperties.getProperty(property, defaultValue);
    }

    private void loadPropertyFile(Context context) {
        mProperties = new Properties();
        InputStream fileStream;
        try {
            fileStream = context.getAssets().open("tba.properties");
            mProperties.load(fileStream);
            fileStream.close();
        } catch (IOException e) {
            Log.e("Unable to load property file");
            e.printStackTrace();
        }
    }
}
