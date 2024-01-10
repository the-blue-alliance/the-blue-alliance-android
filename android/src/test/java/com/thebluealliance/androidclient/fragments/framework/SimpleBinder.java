package com.thebluealliance.androidclient.fragments.framework;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.databinding.ListViewWithSpinnerBinding;

public class SimpleBinder extends AbstractDataBinder<String, ListViewWithSpinnerBinding> {

    @Override
    public void updateData(@Nullable String data) {
        if (data == null || data.isEmpty()) {
            setDataBound(false);
            return;
        }

        setDataBound(true);
    }

    @Override
    public void onComplete() {
        if (!isDataBound()) {
            mNoDataBinder.bindData(mNoDataParams);
        }
    }

    @Override
    public void bindViews() {

    }

    @Override
    public void onError(Throwable throwable) {
        if (!isDataBound()) {
            mNoDataBinder.bindData(mNoDataParams);
        }
    }
}
