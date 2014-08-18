package com.thebluealliance.androidclient.background.mytba;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.fragments.mytba.MySubscriptionsFragment;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Subscription;

import java.util.ArrayList;

/**
 * File created by phil on 8/2/14.
 */
public class PopulateUserSubscriptions extends AsyncTask<Void, Void, Void> {

    public static final String USER_KEY = "user_key";

    private MySubscriptionsFragment fragment;
    private RefreshableHostActivity activity;
    private boolean forceFromCache;
    private ArrayList<ListItem> subscriptions;

    public PopulateUserSubscriptions(MySubscriptionsFragment fragment, boolean forceFromCache) {
        super();
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (activity != null) {
            activity.showMenuProgressBar();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        subscriptions = new ArrayList<>();
        ArrayList<Subscription> collection = Database.getInstance(activity).getSubscriptionsTable().getForUser(AccountHelper.getSelectedAccount(activity));
        int lastModel = -1;
        if (collection != null) {
            for (Subscription subscription : collection) {
                if(lastModel != subscription.getModelEnum()){
                    subscriptions.add(new EventTypeHeader(subscription.getModelType().getTitle()));
                }
                lastModel = subscription.getModelEnum();
                subscriptions.add(ModelHelper.renderModelFromKey(activity, subscription.getModelKey()));
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        View view = fragment.getView();
        if(activity != null && fragment != null && view != null) {
            TextView noDataText = (TextView)view.findViewById(R.id.no_data);
            if (subscriptions == null || subscriptions.isEmpty()) {
                noDataText.setText(activity.getString(R.string.no_subscription_data));
                noDataText.setVisibility(View.VISIBLE);
            } else {
                noDataText.setVisibility(View.GONE);
                ListViewAdapter adapter = new ListViewAdapter(activity, subscriptions);
                ListView listView = (ListView) fragment.getView().findViewById(R.id.list);
                listView.setAdapter(adapter);
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);

            activity.notifyRefreshComplete(fragment);
        }
    }
}
