package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.RankingListElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * File created by phil on 4/23/14.
 */
public class PopulateEventRankings extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private BaseActivity activity;
    private String eventKey;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventRankings(Fragment f) {
        mFragment = f;
        activity = (BaseActivity) mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teams = new ArrayList<>();

        try {
            APIResponse<ArrayList<JsonArray>> response = DataManager.getEventRankings(activity, eventKey);
            ArrayList<JsonArray> rankList = response.getData();
            JsonArray headerRow = rankList.remove(0);
            for (JsonArray row : rankList) {
                /* Assume that the list of lists has rank first
                 * and team # second, always
                 */
                String teamKey = "frc" + row.get(1).getAsString();
                String rankingString = "";
                CaseInsensitiveMap<String> rankingElements = new CaseInsensitiveMap<>();
                for (int i = 2; i < row.size(); i++) {
                    rankingElements.put(headerRow.get(i).getAsString(), row.get(i).getAsString());
                }
                String record = null;
                // Find if the rankings contain a record; remove it if it does
                Iterator it = rankingElements.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry) it.next();
                    if(entry.getKey().toLowerCase().contains("record".toLowerCase())) {
                        record = "(" + rankingElements.get(entry.getKey()) + ")";
                        it.remove();
                        break;
                    }
                }
                if(record == null) {
                    Set<String> keys = rankingElements.keySet();
                    if(keys.contains("wins") && keys.contains("losses") && keys.contains("ties")) {
                        record = "(" + rankingElements.get("wins") + "-" + rankingElements.get("losses") + "-" + rankingElements.get("ties") + ")";
                        rankingElements.remove("wins");
                        rankingElements.remove("losses");
                        rankingElements.remove("ties");
                    }
                }
                if(record == null) {
                    record = "";
                }
                // Construct rankings string
                it = rankingElements.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    rankingString += entry.getKey() + ": " + entry.getValue();
                    if (it.hasNext()) {
                        rankingString += ", ";
                    }
                }
                teams.add(new RankingListElement(teamKey, row.get(1).getAsInt(), "", row.get(0).getAsInt(), record, rankingString));
                //the two columns set to "" above are 'team name' and 'record' as those are not consistently in the data
                //TODO get team name for given number
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event rankings");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && mFragment.getActivity() != null) {
            ListView rankings = (ListView) view.findViewById(R.id.list);
            adapter = new ListViewAdapter(mFragment.getActivity(), teams);
            rankings.setAdapter(adapter);

            if (code == APIResponse.CODE.OFFLINECACHE /* && event is current */) {
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    private class CaseInsensitiveMap<K> extends HashMap<String, K> {

        @Override
        public K put(String key, K value) {
            return super.put(key.toLowerCase(), value);
        }

        public K get(String key) {
            return super.get(key.toLowerCase());
        }

        public boolean contains(String key) {
            return get(key) != null;
        }
    }
}
