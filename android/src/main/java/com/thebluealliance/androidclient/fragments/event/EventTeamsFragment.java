package com.thebluealliance.androidclient.fragments.event;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventTeams;

/**
 * File created by phil on 4/22/14.
 */
public class EventTeamsFragment extends Fragment {

    private String mEventKey;
    private static final String KEY = "event_key";

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    private PopulateEventTeams mTask;

    public static EventTeamsFragment newInstance(String eventKey){
        EventTeamsFragment f = new EventTeamsFragment();
        Bundle data = new Bundle();
        data.putString(KEY,eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mEventKey = getArguments().getString(KEY,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_teams, null);
        mListView = (ListView) view.findViewById(R.id.event_team_list);
        if(mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            mTask = new PopulateEventTeams(this);
            mTask.execute(mEventKey);
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String teamKey = ((ListViewAdapter) adapterView.getAdapter()).getKey(position);
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
        if(mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }
}
