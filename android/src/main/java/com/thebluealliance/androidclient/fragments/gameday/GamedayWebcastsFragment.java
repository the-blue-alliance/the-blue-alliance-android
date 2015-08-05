package com.thebluealliance.androidclient.fragments.gameday;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.fragments.ListvVewFragment;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.WebcastListSubscriber;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class GamedayWebcastsFragment extends ListvVewFragment<List<Event>, WebcastListSubscriber> {

    private ListView mListView;
    private ListViewAdapter mAdapter;
    private Parcelable mListState;
    private int mFirstVisiblePosition;
    private int mYear;
    private int mWeek;

    public static GamedayWebcastsFragment newInstance() {
        return new GamedayWebcastsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mYear = Utilities.getCurrentYear();
        mWeek = Utilities.getCurrentCompWeek();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_carded, null);
        mListView = (ListView) v.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        mBinder.mListView = mListView;
        mBinder.mProgressBar = progressBar;
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mListView.setSelection(mFirstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
        } else {
            mAdapter = new ListViewAdapter(getActivity(), new ArrayList<>());
            mListView.setAdapter(mAdapter);
        }
        return v;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable() {
        return mDatafeed.fetchEventsInWeek(mYear, mWeek);
    }
}
