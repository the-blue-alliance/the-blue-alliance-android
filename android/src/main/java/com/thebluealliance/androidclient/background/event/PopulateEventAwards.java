package com.thebluealliance.androidclient.background.event;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.AwardListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.models.Award;

import java.util.ArrayList;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventAwards extends AsyncTask<String, Void, APIResponse.CODE> implements AdapterView.OnItemClickListener {

    private Fragment mFragment;
    private BaseActivity activity;
    private String eventKey;
    private ArrayList<ListItem> awards;
    private ArrayList<String> keys;
    private ListViewAdapter adapter;

    public PopulateEventAwards(Fragment f) {
        mFragment = f;
        activity = (BaseActivity)mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<>();
        keys = new ArrayList<>();

        APIResponse<ArrayList<Award>> response;
        try {
            response = DataManager.getEventAwards(activity, eventKey);
            ArrayList<Award> awardList = response.getData();
            for(Award a:awardList){
                ArrayList<AwardListElement> allWinners = a.renderAll();
                awards.addAll(allWinners);
                for(int i=0;i<allWinners.size();i++){
                    keys.add(a.getEventKey()+"_"+a.getName());
                }
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event awards");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null) {
            adapter = new ListViewAdapter(activity, awards, keys);
            ListView rankings = (ListView) view.findViewById(R.id.event_awards);
            rankings.setAdapter(adapter);
            rankings.setOnItemClickListener(this);

            if(code == APIResponse.CODE.OFFLINECACHE /* && event is current */){
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tag = view.getTag().toString();
        if(!tag.equals("frc0") && !tag.equals("frc-1")){
            mFragment.startActivity(ViewTeamActivity.newInstance(activity, tag));
        }
    }
}
