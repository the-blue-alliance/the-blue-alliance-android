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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.FirebaseNotification;

import java.util.ArrayList;

/**
 * Created by phil on 3/26/15.
 */
public class GamedayTickerFragment extends Fragment implements ChildEventListener {

    private ListView listView;
    private ProgressBar progressBar;
    private ListViewAdapter adapter;
    private Parcelable listState;
    Firebase ticker;
    private int firstVisiblePosition;

    public static GamedayTickerFragment newInstance(){
        return new GamedayTickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }else{
            adapter = new ListViewAdapter(getActivity(), new ArrayList<ListItem>());
            listView.setAdapter(adapter);
        }
        ticker = new Firebase("https://thebluealliance.firebaseio.com/notifications/"); //TODO move to config file
        ticker.limitToLast(25).addChildEventListener(this);
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
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        FirebaseNotification notification = dataSnapshot.getValue(FirebaseNotification.class);
        Log.d(Constants.LOG_TAG, "Json: "+notification.convertToJson());
        adapter.values.add(0, notification.getNotification());
        adapter.updateListData();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
