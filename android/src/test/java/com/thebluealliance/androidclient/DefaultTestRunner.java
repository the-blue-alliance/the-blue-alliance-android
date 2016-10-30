package com.thebluealliance.androidclient;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.support.annotation.NonNull;

import java.lang.reflect.Method;

/**
 * Custom Integration test runner that exposes our custom Application class
 * This lets us access mocks via the DI framework
 */
public class DefaultTestRunner extends RobolectricTestRunner {

    // This value should be changed as soon as Robolectric will support newer api.
    private static final int SDK_EMULATE_LEVEL = 21;

    public DefaultTestRunner(@NonNull Class<?> clazz) throws Exception {
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
                defaultConfig.abiSplit(),
                defaultConfig.resourceDir(),
                defaultConfig.assetDir(),
                defaultConfig.buildDir(),
                defaultConfig.shadows(),
                defaultConfig.instrumentedPackages(),
                TestTbaAndroid.class,
                defaultConfig.libraries(),
                getBuildConfig(defaultConfig.constants())
        );
    }

    private Class<?> getBuildConfig(Class<?> constants) {
        if (constants == Void.class) {
            return BuildConfig.class;
        }
        return constants;
    }
}
