package com.thebluealliance.androidclient.binders;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.datafeed.DataConsumer;

/**
 * A class that takes in input data model and updates views accordingly
 * For now, declare views as public members and set them elsewhere where the view is created
 * (like {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}).
 * @param <T>
 */
public abstract class AbstractDataBinder<T> implements DataConsumer<T> {
    Activity mActivity;

    public void setActivity(Activity activity) {
        mActivity = activity;
    }
}
