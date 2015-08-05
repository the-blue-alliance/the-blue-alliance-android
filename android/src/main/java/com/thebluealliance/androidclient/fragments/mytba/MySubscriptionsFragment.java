package com.thebluealliance.androidclient.fragments.mytba;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.fragments.ListviewFragment;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.subscribers.SubscriptionListSubscriber;

import java.util.List;

import rx.Observable;

/**
 * File created by phil on 8/2/14.
 */
public class MySubscriptionsFragment
  extends ListviewFragment<List<Subscription>, SubscriptionListSubscriber> {

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    public static MySubscriptionsFragment newInstance() {
        return new MySubscriptionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        mBinder.mListView = mListView;
        mBinder.mProgressBar = progressBar;
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Subscription>> getObservable() {
        return mDatafeed.getCache().fetchUserSubscriptions(getActivity());
    }
}
