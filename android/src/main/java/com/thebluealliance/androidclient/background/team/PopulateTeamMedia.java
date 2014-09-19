package com.thebluealliance.androidclient.background.team;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Media;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Retrieves media links (video/pictures) for an FRC team's robot.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 *         <p/>
 *         <p/>
 *         File created by phil on 5/31/14.
 */
public class PopulateTeamMedia extends AsyncTask<Object, Void, APIResponse.CODE> {

    private TeamMediaFragment fragment;
    private RefreshableHostActivity activity;
    private String team;
    private int year;
    ArrayList<ListGroup> groups;
    private boolean forceFromCache;

    public PopulateTeamMedia(TeamMediaFragment f, boolean forceFromCache) {
        fragment = f;
        activity = (RefreshableHostActivity) f.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected APIResponse.CODE doInBackground(Object... params) {
        if (params.length != 2 || !(params[0] instanceof String) || !(params[1] instanceof Integer)) {
            throw new IllegalArgumentException("PopulateTeamMedia must be called with the team key and year (String, int)");
        }

        Log.d(Constants.LOG_TAG, "Loading team media");

        team = (String) params[0];
        year = (Integer) params[1];

        if (year == -1) {
            //year has not been set.
            return APIResponse.CODE.NODATA;
        }

        APIResponse<ArrayList<Media>> response = null;
        try {
            response = DataManager.Teams.getTeamMedia(activity, team, year, forceFromCache);

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            groups = new ArrayList<>();
            ListGroup cdPhotos = new ListGroup(activity.getString(R.string.cd_header)),
                    ytVideos = new ListGroup(activity.getString(R.string.yt_header));

            ArrayList<Media> medias = response.getData();
            for (Media m : medias) {
                try {
                    switch (m.getMediaType()) {
                        case CD_PHOTO_THREAD:
                            cdPhotos.children.add(m);
                            break;
                        case YOUTUBE:
                            ytVideos.children.add(m);
                            break;
                        default:
                            break;
                    }
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.e(Constants.LOG_TAG, "Can't get media type. Missing fields..." +
                            Arrays.toString(e.getStackTrace()));
                }
            }

            if (!cdPhotos.children.isEmpty()) {
                groups.add(cdPhotos);
            }
            if (!ytVideos.children.isEmpty()) {
                groups.add(ytVideos);
            }

            Log.d(Constants.LOG_TAG, "Loading media finished");
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch media for " + team + " in " + year);
        }
        return APIResponse.CODE.NODATA;
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = fragment.getView();
        if (view != null && activity != null) {
            ExpandableListAdapter adapter = new ExpandableListAdapter(activity, groups);
            ExpandableListView media = (ExpandableListView) view.findViewById(R.id.team_media_list);

            TextView noDataText = (TextView) view.findViewById(R.id.no_media);

            // If there is no media, display a message.
            if (code == APIResponse.CODE.NODATA || adapter.groups.isEmpty()) {
                noDataText.setVisibility(View.VISIBLE);
            } else {
                noDataText.setVisibility(View.GONE);
                media.setAdapter(adapter);
                //expand all the groups
                for (int i = 0; i < groups.size(); i++) {
                    media.expandGroup(i);
                }
                Parcelable state = media.onSaveInstanceState();
                int firstVisiblePosition = media.getFirstVisiblePosition();
                media.setAdapter(adapter);
                media.onRestoreInstanceState(state);
                media.setSelection(firstVisiblePosition);

                adapter.notifyDataSetChanged();
            }

            // Display warning message if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress spinner and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.team_media_list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                PopulateTeamMedia secondLoad = new PopulateTeamMedia(fragment, false);
                fragment.updateTask(secondLoad);
                secondLoad.execute(team, year);
            } else {
                // Show notification if we've refreshed data.
                Log.i(Constants.REFRESH_LOG, "Team " + team + " " + year + " media refresh complete");
                if (fragment instanceof RefreshListener) {
                    activity.notifyRefreshComplete(fragment);
                }
            }
        }
    }
}
