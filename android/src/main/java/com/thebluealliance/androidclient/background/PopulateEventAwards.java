package com.thebluealliance.androidclient.background;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.AwardListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.models.Award;

import java.util.ArrayList;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventAwards extends AsyncTask<String, Void, Void> implements AdapterView.OnItemClickListener {

    private Context context;
    private View view;
    private String eventKey;
    private ArrayList<ListItem> awards;
    private ArrayList<String> keys;
    private ListViewAdapter adapter;

    public PopulateEventAwards(Context c, View view) {
        this.context = c;
        this.view = view;
    }

    @Override
    protected Void doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<ListItem>();
        keys = new ArrayList<String>();

        ArrayList<Award> awardList = null;
        try {
            awardList = DataManager.getEventAwards(context, eventKey);
            for(Award a:awardList){
                ArrayList<AwardListElement> allWinners = a.renderAll();
                awards.addAll(allWinners);
                for(int i=0;i<allWinners.size();i++){
                    keys.add(a.getEventKey()+"_"+a.getName());
                }
            }
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }


        adapter = new ListViewAdapter(context, awards, keys);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (view != null) {
            ListView rankings = (ListView) view.findViewById(R.id.event_awards);
            rankings.setAdapter(adapter);
            rankings.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tag = view.getTag().toString();
        if(!tag.equals("frc0") && !tag.equals("frc-1")){
            context.startActivity(ViewTeamActivity.newInstance(context, tag));
        }
    }
}
