package com.thebluealliance.androidclient;

import android.support.annotation.NonNull;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Custom Integration test runner that exposes our custom Application class
 * This lets us access mocks via the DI framework
 */
public class DefaultTestRunner extends RobolectricTestRunner {

    public DefaultTestRunner(@NonNull Class<?> clazz) throws Exception {
        super(clazz);
    }

    @Override
    protected Config buildGlobalConfig() {
        return new Config.Builder()
                .setManifest("android/src/main/AndroidManifest.xml")
                .setPackageName("com.thebluealliance.androidclient")
                .setApplication(TestTbaAndroid.class)
                //? .setSdk(xxx)
                .build();
    }

    private Class<?> getBuildConfig(Class<?> constants) {
        if (constants == Void.class) {
            return BuildConfig.class;
        }
        return constants;
    }
}
