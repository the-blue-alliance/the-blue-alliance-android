package com.thebluealliance.androidclient.interfaces;

public interface BindableFragmentPagerAdapter {

    void bindFragmentAtPosition(int position);
    void setAutoBindOnceAtPosition(int position, boolean autoBind);
    int getCount();
}
