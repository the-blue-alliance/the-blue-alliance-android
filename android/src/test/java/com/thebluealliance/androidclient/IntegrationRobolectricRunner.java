package com.thebluealliance.androidclient;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import android.support.annotation.NonNull;

import java.lang.reflect.Method;

/**
 * Custom Integration test runner that exposes our custom Application class
 * This lets us access mocks via the DI framework
 */
public class IntegrationRobolectricRunner extends RobolectricGradleTestRunner {

    // This value should be changed as soon as Robolectric will support newer api.
    private static final int SDK_EMULATE_LEVEL = 21;

    public IntegrationRobolectricRunner(@NonNull Class<?> clazz) throws Exception {
        super(clazz);
    }

    @Override
    public Config getConfig(@NonNull Method method) {
        final Config defaultConfig = super.getConfig(method);
        return new Config.Implementation(
                new int[]{SDK_EMULATE_LEVEL},
                "android/src/main/AndroidManifest.xml",
                defaultConfig.qualifiers(),
                "com.thebluealliance.androidclient",
                defaultConfig.resourceDir(),
                defaultConfig.assetDir(),
                defaultConfig.shadows(),
                TestTbaAndroid.class,
                defaultConfig.libraries(),
                defaultConfig.constants() == Void.class ? BuildConfig.class : defaultConfig.constants()
        );
    }
}
