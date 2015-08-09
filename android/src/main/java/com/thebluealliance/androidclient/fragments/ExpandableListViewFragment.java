package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ExpandableListViewAdapter;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;
import com.thebluealliance.androidclient.views.ExpandableListView;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.List;

public abstract class ExpandableListViewFragment<T, S extends BaseAPISubscriber<T, List<ListGroup>>>
  extends DatafeedFragment<T, List<ListGroup>, S, ExpandableListViewBinder> {

    private Parcelable mListState;
    private ExpandableListViewAdapter mAdapter;
    private int mFirstVisiblePosition;

    protected ExpandableListView mExpandableListView;

    @Override
    public @Nullable View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.expandable_list_view_with_spinner, null);
        mExpandableListView = (ExpandableListView) v.findViewById(R.id.expandable_list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mExpandableListView.setAdapter(mAdapter);
            mExpandableListView.onRestoreInstanceState(mListState);
            mExpandableListView.setSelection(mFirstVisiblePosition);
            progressBar.setVisibility(View.GONE);
        }
        mBinder.mExpandableListView = mExpandableListView;
        mBinder.mProgressBar = progressBar;

        mBinder.setNoDataView((NoDataView) v.findViewById(R.id.no_data));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExpandableListView != null) {
            mAdapter = (ExpandableListViewAdapter) mExpandableListView.getExpandableListAdapter();
            mListState = mExpandableListView.onSaveInstanceState();
            mFirstVisiblePosition = mExpandableListView.getFirstVisiblePosition();
        }
    }
}
