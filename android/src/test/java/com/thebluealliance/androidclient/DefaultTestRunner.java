package com.thebluealliance.androidclient;

import androidx.annotation.NonNull;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;

/**
 * Custom Integration test runner that exposes our custom Application class
 * This lets us access mocks via the DI framework
 */
public class DefaultTestRunner extends RobolectricTestRunner {

    // This value should be changed as soon as Robolectric will support newer api.
    public static final int SDK_EMULATE_LEVEL = 21;

    public DefaultTestRunner(@NonNull Class<?> clazz) throws Exception {
        super(clazz);
    }

    @Override
    public Config getConfig(@NonNull Method method) {
        final Config defaultConfig = super.getConfig(method);
        return new Config.Implementation(
                new int[]{SDK_EMULATE_LEVEL},
                defaultConfig.minSdk(),
                defaultConfig.maxSdk(),
                defaultConfig.manifest(),
                defaultConfig.qualifiers(),
                "com.thebluealliance.androidclient",
                defaultConfig.resourceDir(),
                defaultConfig.assetDir(),
                defaultConfig.shadows(),
                defaultConfig.instrumentedPackages(),
                TestTbaAndroid.class,
                defaultConfig.libraries()
        );
    }
}
