package com.thebluealliance.androidclient.fragments;

import com.thebluealliance.androidclient.DividerItemDecoration;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;
import com.thebluealliance.androidclient.views.NoDataView;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

public abstract class BriteRecyclerViewFragment<T, S extends BaseAPISubscriber<T, List<Object>>, B extends RecyclerViewBinder>
        extends BriteDatafeedFragment<T, List<Object>, S, B> implements RecyclerViewBinder.RecyclerViewAdapterCreatorInitializer {

    private Parcelable mListState;
    private RecyclerView.Adapter mAdapter;
    protected RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    public
    @Nullable
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;

        // If a subclass doesn't want to show the default scrollbars, we have to inflate a
        // different layout with scrollbars turned off in XML, as unfortunately there's no way
        // to do it programmatically.
        if (shouldShowScrollbars()) {
            v = inflater.inflate(R.layout.recycler_view_with_spinner, null);
        } else {
            v = inflater.inflate(R.layout.recycler_view_with_spinner_no_scrollbar, null);
        }

        mBinder.setRootView(v);
        mBinder.setRecyclerViewBinderMapper(this);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        if (shouldShowDividers()) {
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        }
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }

        mBinder.setNoDataView((NoDataView) v.findViewById(R.id.no_data));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecyclerView != null) {
            mAdapter = mRecyclerView.getAdapter();
            mListState = mLayoutManager.onSaveInstanceState();
        }
    }

    protected boolean shouldShowDividers() {
        return true;
    }

    protected boolean shouldShowScrollbars() {
        return true;
    }
}
