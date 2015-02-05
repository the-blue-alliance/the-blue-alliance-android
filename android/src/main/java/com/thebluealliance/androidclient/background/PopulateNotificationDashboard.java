package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.fragments.NotificationDashboardFragment;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.ArrayList;

/**
 * Created by phil on 2/3/15.
 */
public class PopulateNotificationDashboard extends AsyncTask<Void, Void, Void> {
    
    private NotificationDashboardFragment fragment;
    private RefreshableHostActivity activity;
    private ArrayList<ListItem> items;
    private ListViewAdapter adapter;
    
    public PopulateNotificationDashboard(NotificationDashboardFragment fragment){
        super();
        this.fragment = fragment;
        this.activity = (RefreshableHostActivity) fragment.getActivity();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d(Constants.LOG_TAG, "Starting to fetch notifications");
        Database.Notifications table = Database.getInstance(activity).getNotificationsTable();
        table.dismissAll();
        ArrayList<StoredNotification> notifications = table.get();
        items = new ArrayList<>();
        for(StoredNotification notification: notifications){
            items.add(new LabelValueListItem(notification.getTitle(), notification.getBody(), notification.getIntent()));
        }
        adapter = new ListViewAdapter(activity, items);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        View view = fragment.getView();
        if (view != null) {
            //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no awards in the adapter or if we can't download info
            // off the web, display a message.
            if (adapter.values.isEmpty()) {
                noDataText.setText(R.string.no_team_data); //TODO proper string
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView teamList = (ListView) view.findViewById(R.id.list);
                Parcelable state = teamList.onSaveInstanceState();
                teamList.setAdapter(adapter);
                noDataText.setVisibility(View.GONE);
                teamList.onRestoreInstanceState(state);
            }
            
            // give the parent a reference to the adapter
            fragment.setAdapter(adapter);

            // Remove progress spinner and show content, since we're done loading the data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);


            // Show notification if we've refreshed data.
            if (fragment instanceof RefreshListener) {
                Log.d(Constants.REFRESH_LOG, "Notification dashboard refresh complete");
                activity.notifyRefreshComplete(fragment);
            }
        }
    }
}
