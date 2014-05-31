package com.thebluealliance.androidclient.background.team;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListGroup;
import com.thebluealliance.androidclient.models.Media;

import java.util.ArrayList;

/**
 * File created by phil on 5/31/14.
 */
public class PopulateTeamMedia extends AsyncTask<Object, Void, APIResponse.CODE> {

    private Fragment fragment;
    private RefreshableHostActivity activity;
    SparseArray<ListGroup> groups;

    public PopulateTeamMedia(Fragment f){
        fragment = f;
        activity = (RefreshableHostActivity) f.getActivity();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected APIResponse.CODE doInBackground(Object... params) {
        if(params.length != 2 || !(params[0] instanceof String) || !(params[1] instanceof Integer)){
            throw new IllegalArgumentException("PopulateTeamMedia must be called with the team key and year (String, int)");
        }

        String team = (String) params[0];
        int year = (Integer) params[1];

        /**
         * This isn't in the API yet, so here's some temp data
         */
        JsonObject testCDPhoto = new JsonObject();
        testCDPhoto.addProperty("type", "cdphotothread");
        testCDPhoto.addProperty("foreign_key","39894");
        JsonObject details = new JsonObject();
        details.addProperty("image_partial","fe3/fe38d320428adf4f51ac969efb3db32c_l.jpg");
        testCDPhoto.add("details", details);

        JsonObject testVideo = new JsonObject();
        testVideo.addProperty("type", "youtube");
        testVideo.addProperty("foreign_key", "aFZy8iibMD0");
        testVideo.add("details", new JsonObject());


        ArrayList<Media> testMedia = new ArrayList<>();
        testMedia.add(JSONManager.getGson().fromJson(testCDPhoto, Media.class));
        testMedia.add(JSONManager.getGson().fromJson(testVideo, Media.class));
        APIResponse<ArrayList<Media>> response = new APIResponse<>(testMedia, APIResponse.CODE.OFFLINECACHE);
        /**
         * End test data creation
         */

        groups = new SparseArray<>();
        ListGroup cdPhotos = new ListGroup(activity.getString(R.string.cd_header)),
                  ytVideos = new ListGroup(activity.getString(R.string.yt_header));

        ArrayList<Media> medias = response.getData();
        for(Media m: medias){
            switch(m.getMediaType()){
                case CD_PHOTO_THREAD:
                    cdPhotos.children.add(m);
                    break;
                case YOUTUBE:
                    ytVideos.children.add(m);
                    break;
                default:
                    break;
            }
        }

        int numGroups = 0;
        if(cdPhotos.children.size() > 0){
            groups.append(numGroups, cdPhotos);
            numGroups ++;
        }
        if(ytVideos.children.size() > 0){
            groups.append(numGroups, ytVideos);
        }

        return response.getCode();
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = fragment.getView();
        if (view != null && activity != null) {
            ExpandableListAdapter adapter = new ExpandableListAdapter(activity, groups);
            ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.team_media_list);
            listView.setAdapter(adapter);

            //expand all the groups
            for(int i=0;i<groups.size(); i++){
                listView.expandGroup(i);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
