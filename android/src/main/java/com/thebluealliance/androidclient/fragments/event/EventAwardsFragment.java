package com.thebluealliance.androidclient.fragments.event;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventAwards;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 * File created by phil on 4/22/14.
 */
public class EventAwardsFragment extends Fragment implements RefreshListener {

    private Activity parent;

    private String mEventKey;
    private static final String EVENT_KEY = "eventKey";

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;

    private PopulateEventAwards mTask;

    public static EventAwardsFragment newInstance(String eventKey) {
        EventAwardsFragment f = new EventAwardsFragment();
        Bundle data = new Bundle();
        data.putString(EVENT_KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventKey = getArguments().getString(EVENT_KEY, "");
        }
        parent = getActivity();
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).registerRefreshableActivityListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String teamKey = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
                if (!teamKey.equals("frc-1") && !teamKey.equals("frc")) {
                    startActivity(ViewTeamActivity.newInstance(getActivity(), teamKey));
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart() {
        mTask = new PopulateEventAwards(this, true);
        mTask.execute(mEventKey);
        View view = getView();
        if (view != null) {
            // Indicate loading; the task will hide the progressbar and show the content when loading is complete
            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            view.findViewById(R.id.list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((RefreshableHostActivity) parent).deregisterRefreshableActivityListener(this);
    }
}
