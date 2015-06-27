package com.thebluealliance.androidclient.binders;

import android.content.Context;

import com.thebluealliance.androidclient.datafeed.DataConsumer;

public abstract class AbstractDataBinder<T> implements DataConsumer<T> {
    Context mContext;

    public void setContext(Context context) {
        mContext = context;
    }
}
