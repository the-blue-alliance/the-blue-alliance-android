package com.thebluealliance.androidclient.interfaces;

public interface BindableAdapter {

    void bindFragmentAtPosition(int position);

    void setAutoBindOnceAtPosition(int position, boolean autoBind);

    void setFragmentVisibleAtPosition(int position, boolean visible);

    boolean isFragmentAtPositionBound(int position);

    int getCount();
}
