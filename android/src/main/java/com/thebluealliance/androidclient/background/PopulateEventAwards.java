package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
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
    private String eventKey;
    private ArrayList<ListItem> awards;
    private ArrayList<String> keys;
    private ListViewAdapter adapter;

    public PopulateEventAwards(Fragment f) {
        mFragment = f;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<>();
        keys = new ArrayList<>();

        APIResponse<ArrayList<Award>> response;
        try {
            response = DataManager.getEventAwards(mFragment.getActivity(), eventKey);
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
            e.printStackTrace();
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null) {
            adapter = new ListViewAdapter(mFragment.getActivity(), awards, keys);
            ListView rankings = (ListView) view.findViewById(R.id.event_awards);
            rankings.setAdapter(adapter);
            rankings.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tag = view.getTag().toString();
        if(!tag.equals("frc0") && !tag.equals("frc-1")){
            mFragment.startActivity(ViewTeamActivity.newInstance(mFragment.getActivity(), tag));
        }
    }
}
