package com.thebluealliance.androidclient.fragments.framework;

import org.junit.After;
import org.robolectric.util.ReflectionHelpers;

import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class BaseFragmentTest {

    /**
     * Try and avoid Robolectric OOM errors
     * Probably this: https://github.com/robolectric/robolectric/issues/2068
     * Based on code from https://github.com/robolectric/robolectric/issues/1700#issuecomment-163943815
     */
    @After
    public void globalTearDown() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // https://github.com/robolectric/robolectric/pull/1741
        final Class<?> btclass = Class.forName("com.android.internal.os.BackgroundThread");
        Object backgroundThreadSingleton = ReflectionHelpers.getStaticField(btclass,"sInstance");
        if (backgroundThreadSingleton!=null) {
            btclass.getMethod("quit").invoke(backgroundThreadSingleton);
            ReflectionHelpers.setStaticField(btclass, "sInstance", null);
            ReflectionHelpers.setStaticField(btclass, "sHandler", null);
        }

        // https://github.com/robolectric/robolectric/issues/2068
        Class clazz = ReflectionHelpers.loadClass(getClass().getClassLoader(), "android.view.WindowManagerGlobal");
        Object instance = ReflectionHelpers.callStaticMethod(clazz, "getInstance");

        // We essentially duplicate what's in {@link WindowManagerGlobal#closeAll} with what's below.
        // The closeAll method has a bit of a bug where it's iterating through the "roots" but
        // bases the number of objects to iterate through by the number of "views." This can result in
        // an {@link java.lang.IndexOutOfBoundsException} being thrown.
        Object lock = ReflectionHelpers.getField(instance, "mLock");

        ArrayList<Object> roots = ReflectionHelpers.getField(instance, "mRoots");
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock) {
            for (int i = 0; i < roots.size(); i++) {
                ReflectionHelpers.callInstanceMethod(instance, "removeViewLocked",
                        ReflectionHelpers.ClassParameter.from(int.class, i),
                        ReflectionHelpers.ClassParameter.from(boolean.class, false));
            }
        }

        // Views will still be held by this array. We need to clear it out to ensure
        // everything is released.
        Collection<View> dyingViews = ReflectionHelpers.getField(instance, "mDyingViews");
        dyingViews.clear();
    }
}
