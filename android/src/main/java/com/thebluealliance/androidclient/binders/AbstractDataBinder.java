package com.thebluealliance.androidclient.binders;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.datafeed.DataConsumer;

public abstract class AbstractDataBinder<T> implements DataConsumer<T> {
    Context mContext;
    View mView;

    public void setContext(Context context) {
        mContext = context;
    }

    public void setView(View view) {
        mView = view;
    }
}
