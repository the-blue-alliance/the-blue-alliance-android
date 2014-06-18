package com.thebluealliance.androidclient.fragments.team;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.background.team.PopulateTeamInfo;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

import java.util.List;

public class TeamInfoFragment extends Fragment implements View.OnClickListener, RefreshListener {

    private Activity parent;

    private String mTeamKey;

    private PopulateTeamInfo task;

    public TeamInfoFragment() {
        // Empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeamKey = getArguments().getString(ViewTeamActivity.TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("TeamInfoFragment must be created with a team key!");
        }
        parent = getActivity();
        if(parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity)parent).registerRefreshableActivityListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_info, container, false);
        // Register this fragment as the callback for all clickable views
        v.findViewById(R.id.team_location_container).setOnClickListener(this);
        v.findViewById(R.id.team_twitter_button).setOnClickListener(this);
        v.findViewById(R.id.team_cd_button).setOnClickListener(this);
        v.findViewById(R.id.team_youtube_button).setOnClickListener(this);
        v.findViewById(R.id.team_website_button).setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(parent instanceof RefreshableHostActivity){
            ((RefreshableHostActivity) parent).startRefresh();
        }
    }

    @Override
    public void onClick(View view) {
        PackageManager manager = getActivity().getPackageManager();
        if (view.getTag() != null) {

            String uri = view.getTag().toString();
            Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            List<ResolveInfo> handlers = manager.queryIntentActivities(i, 0);
            if (!handlers.isEmpty()) {
                // There is an application to handle this intent intent
                startActivity(i);
            } else {
                // No application can handle this intent
                Toast.makeText(getActivity(), "No app can handle that request", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRefreshStart() {
        task = new PopulateTeamInfo(this, true);
        task.execute(mTeamKey);
        View view = getView();
        if (view != null) {
            // Indicate loading; the task will hide the progressbar and show the content when loading is complete
            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            view.findViewById(R.id.team_info_container).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefreshStop() {
        task.cancel(false);
    }
}
