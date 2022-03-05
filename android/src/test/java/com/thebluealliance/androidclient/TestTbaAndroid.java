package com.thebluealliance.androidclient;

public class TestTbaAndroid extends TbaAndroid {

    @Override
    public void onCreate() {
        disableStetho();
        super.onCreate();
    }

}
