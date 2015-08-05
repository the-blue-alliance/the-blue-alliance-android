package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.subscribers.DistrictListSubscriber;

import java.util.List;

import rx.Observable;

public class DistrictListFragment
  extends DatafeedFragment<List<District>, List<ListItem>, DistrictListSubscriber, ListviewBinder> {

    public static final String YEAR = "year";

    private int mYear;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    public static DistrictListFragment newInstance(int year) {
        DistrictListFragment f = new DistrictListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listeners
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        mBinder.mListView = mListView;
        mBinder.mProgressBar = progressBar;

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onPause() {
        // Save the data if moving away from fragment.
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
    protected Observable<List<District>> getObservable() {
        return mDatafeed.fetchDistrictList(mYear);
    }
}
