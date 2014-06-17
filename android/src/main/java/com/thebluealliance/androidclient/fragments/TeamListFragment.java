package com.thebluealliance.androidclient.fragments;

import android.content.Intent;
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
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateTeamList;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 * File created by phil on 4/20/14.
 */
public class TeamListFragment extends Fragment {

    private static final String START = "START";
    private static final String END = "END";

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;

    private int mTeamNumberStart, mTeamNumberEnd;

    private PopulateTeamList mTask;

    public static TeamListFragment newInstance(int startTeamNumber, int endTeamNumber) {
        TeamListFragment f = new TeamListFragment();
        Bundle args = new Bundle();
        args.putInt(START, startTeamNumber);
        args.putInt(END, endTeamNumber);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeamNumberStart = getArguments().getInt(START);
        mTeamNumberEnd = getArguments().getInt(END);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setFastScrollAlwaysVisible(true);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mTask = new PopulateTeamList(this);
            mTask.execute(mTeamNumberStart, mTeamNumberEnd);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String teamKey = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
                Intent i = new Intent(getActivity(), ViewTeamActivity.class);
                i.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTask.cancel(false);
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }
}
