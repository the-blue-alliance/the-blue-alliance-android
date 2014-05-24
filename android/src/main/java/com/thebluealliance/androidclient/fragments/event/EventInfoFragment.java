package com.thebluealliance.androidclient.fragments.event;

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
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.background.event.PopulateEventInfo;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

import java.util.List;

/**
 * File created by phil on 4/22/14.
 */
public class EventInfoFragment extends Fragment implements RefreshListener, View.OnClickListener {

    private String eventKey;
    private static final String KEY = "eventKey";
    private PopulateEventInfo task;

    public static EventInfoFragment newInstance(String eventKey) {
        EventInfoFragment f = new EventInfoFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventKey = getArguments().getString(KEY, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View info = inflater.inflate(R.layout.fragment_event_info, null);
        info.findViewById(R.id.event_location_container).setOnClickListener(this);
        info.findViewById(R.id.event_website_button).setOnClickListener(this);
        info.findViewById(R.id.event_twitter_button).setOnClickListener(this);
        info.findViewById(R.id.event_youtube_button).setOnClickListener(this);
        info.findViewById(R.id.event_cd_button).setOnClickListener(this);
        info.findViewById(R.id.event_top_teams_container).setOnClickListener(this);
        info.findViewById(R.id.top_opr_container).setOnClickListener(this);
        return info;
    }

    @Override
    public void onResume() {
        super.onResume();
        task = new PopulateEventInfo(this);
        task.execute(eventKey);
    }

    @Override
    public void onRefreshStart() {
        task = new PopulateEventInfo(this);
        task.execute(eventKey);
    }

    @Override
    public void onRefreshStop() {
        task.cancel(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            if (v.getTag().equals("top_teams")) {
                ((ViewEventActivity) getActivity()).getPager().setCurrentItem(2);
            } else if (v.getTag().equals("top_opr")) {
                ((ViewEventActivity) getActivity()).getPager().setCurrentItem(4);
            } else {
                PackageManager manager = getActivity().getPackageManager();
                String uri = v.getTag().toString();
                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                List<ResolveInfo> handlers = manager.queryIntentActivities(i, 0);
                if (handlers.size() > 0) {
                // There is an application to handle this intent intent
                    startActivity(i);
                } else
                {
                // No application can handle this intent
                Toast.makeText(getActivity(), "No app can handle that request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
