package com.thebluealliance.androidclient.modules;

/**
 * Interface to get DI modules from an object
 * So we can share one module instance across an activity's fragments
 */
public interface HasModule {

    Object getModule();
}
