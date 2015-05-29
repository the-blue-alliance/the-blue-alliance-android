package com.thebluealliance.androidclient.fragments.gameday;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.gameday.PopulateGameDayWebcasts;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.ArrayList;

/**
 * Created by phil on 3/27/15.
 */
public class GamedayWebcastsFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private ListViewAdapter adapter;
    private Parcelable listState;
    private int firstVisiblePosition;

    public static GamedayWebcastsFragment newInstance() {
        return new GamedayWebcastsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_carded, null);
        listView = (ListView) v.findViewById(R.id.list);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (adapter != null) {
            listView.setAdapter(adapter);
            listView.onRestoreInstanceState(listState);
            listView.setSelection(firstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
        } else {
            adapter = new ListViewAdapter(getActivity(), new ArrayList<>());
            listView.setAdapter(adapter);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listView != null) {
            Log.d("onPause", "saving adapter");
            adapter = (ListViewAdapter) listView.getAdapter();
            listState = listView.onSaveInstanceState();
            firstVisiblePosition = listView.getFirstVisiblePosition();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new PopulateGameDayWebcasts(this, new RequestParams(true, false)).execute();
    }
}
