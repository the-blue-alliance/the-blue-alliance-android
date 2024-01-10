package com.thebluealliance.androidclient.binders;

import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.thebluealliance.androidclient.databinding.RecyclerViewWithSpinnerBinding;

public class RecyclerViewBinder extends AbstractRecyclerViewBinder<RecyclerViewWithSpinnerBinding> {

    @Override
    protected RecyclerView getList() {
        return mBinding.list;
    }

    @Override
    protected ProgressBar getProgress() {
        return mBinding.progress;
    }

    @Override
    public void bindViews() {
        mBinding = RecyclerViewWithSpinnerBinding.bind(mRootView);
    }
}
