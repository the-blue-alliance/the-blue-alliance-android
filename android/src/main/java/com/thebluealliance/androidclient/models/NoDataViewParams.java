package com.thebluealliance.androidclient.models;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Everything you need to display a pretty no data screen
 */
public class NoDataViewParams {
    @DrawableRes
    private int mImageResId;
    @StringRes
    private int mTextResId;

    public NoDataViewParams(@DrawableRes int imageResId, @StringRes int textRes) {
        this.mTextResId = textRes;
        this.mImageResId = imageResId;
    }

    public
    @DrawableRes
    int getImageResId() {
        return mImageResId;
    }

    public
    @StringRes
    int getTextResId() {
        return mTextResId;
    }
}
