package com.thebluealliance.androidclient.background.district;

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
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.district.DistrictListFragment;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;

import java.util.ArrayList;

/**
 * Created by phil on 7/24/14.
 */
public class PopulateDistrictList extends AsyncTask<Integer, Void, APIResponse.CODE> {

    private boolean forceFromCache;
    private DistrictListFragment fragment;
    private RefreshableHostActivity activity;
    private int year;
    private ArrayList<ListItem> districts;

    public PopulateDistrictList(DistrictListFragment fragment, boolean forceFromCache) {
        this.forceFromCache = forceFromCache;
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(Integer... params) {
        year = params[0];

        try {
            APIResponse<ArrayList<District>> response = DataManager.Districts.getDistrictsInYear(activity, year, forceFromCache);
            districts = new ArrayList<>();
            for (District district : response.getData()) {
                int numEvents = DataManager.Districts.getNumEventsForDistrict(activity, district.getKey());
                district.setNumEvents(numEvents);
                districts.add(district.render());
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to get district list for " + year);
            return APIResponse.CODE.NODATA;
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.wtf(Constants.LOG_TAG, "District has no key?");
            e.printStackTrace();
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        View view = fragment.getView();
        if (view != null && activity != null) {
            ListViewAdapter adapter = new ListViewAdapter(activity, districts);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no data in the adapter or if we can't download info
            // off the web, display a message.
            if ((code == APIResponse.CODE.NODATA && !ConnectionDetector.isConnectedToInternet(activity)) || (!forceFromCache && adapter.values.isEmpty())) {
                noDataText.setText(R.string.no_district_list);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView eventList = (ListView) view.findViewById(R.id.list);
                Parcelable state = eventList.onSaveInstanceState();
                eventList.setAdapter(adapter);
                noDataText.setVisibility(View.GONE);
                eventList.onRestoreInstanceState(state);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(fragment.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                PopulateDistrictList second = new PopulateDistrictList(fragment, false);
                fragment.updateTask(second);
                second.execute(year);
            } else {
                // Show notification if we've refreshed data.
                Log.d(Constants.REFRESH_LOG, "Event list refresh complete");
                activity.notifyRefreshComplete((RefreshListener) fragment);
            }

        }
    }
}
