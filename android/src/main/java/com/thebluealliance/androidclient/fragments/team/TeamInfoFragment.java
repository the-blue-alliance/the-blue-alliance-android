package com.thebluealliance.androidclient.fragments.team;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.eventbus.LiveEventEventUpdateEvent;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.modules.ViewTeamModule;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TeamInfoFragment extends Fragment implements View.OnClickListener, DataConsumer<Team> {

    private static final String TEAM_KEY = "team_key";

    private ViewTeamActivity mParent;
    private String mTeamKey;
    private ObjectGraph mFragmentGraph;

    //TODO bring out to super class
    @Inject CacheableDatafeed mDatafeed;
    @Inject TeamInfoSubscriber mSubscriber;

    public static TeamInfoFragment newInstance(String teamKey) {
        TeamInfoFragment fragment = new TeamInfoFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeamKey = getArguments().getString(TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("TeamInfoFragment must be created with a team key!");
        }
        if (!(getActivity() instanceof ViewTeamActivity)) {
            throw new IllegalArgumentException("TeamMediaFragment must be hosted by a " +
                "ViewTeamActivity!");
        } else {
            mParent = (ViewTeamActivity) getActivity();
        }

        mFragmentGraph = ObjectGraph.create(new ViewTeamModule(this));
        mFragmentGraph.inject(this);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
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
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mSubscriber.unsubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<Team> mTeamObservable = mDatafeed.fetchTeam(mTeamKey, null);
        mTeamObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mSubscriber);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View view) {
        PackageManager manager = getActivity().getPackageManager();
        if (view.getTag() != null) {

            String uri = view.getTag().toString();

            //social button was clicked. Track the call
            AnalyticsHelper.sendSocialUpdate(getActivity(), uri, mTeamKey);

            Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            List<ResolveInfo> handlers = manager.queryIntentActivities(i, 0);
            if (!handlers.isEmpty()) {
                // There is an application to handle this intent intent
                startActivity(i);
            } else {
                // No application can handle this intent
                Toast.makeText(getActivity(), "No app can handle that request", Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }

    public void showCurrentEvent(final EventListElement event) {

        final LinearLayout eventLayout = (LinearLayout) getView()
            .findViewById(R.id.team_current_event);
        final RelativeLayout container = (RelativeLayout) getView()
            .findViewById(R.id.team_current_event_container);

        getActivity().runOnUiThread(() -> {
            eventLayout.removeAllViews();
            eventLayout.addView(event.getView(getActivity(),
                getActivity().getLayoutInflater(), null));

            container.setVisibility(View.VISIBLE);
            container.setTag(mTeamKey + "@" + event.getEventKey());
            container.setOnClickListener(new TeamAtEventClickListener(getActivity()));
        });
    }

    public void onEvent(YearChangedEvent event) {

    }

    public void onEvent(LiveEventEventUpdateEvent event) {
        if (event.getEvent() != null) {
            showCurrentEvent(event.getEvent().render());
        }
    }

    @Override
    public void updateData(@Nullable Team team) throws BasicModel.FieldNotDefinedException {
        //TODO set up Android M data binding here
        // https://developer.android.com/tools/data-binding/guide.html

        View view = getView();
        if (view != null) {
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);
            View infoContainer = view.findViewById(R.id.team_info_container);
            if (team == null) {
                noDataText.setText(R.string.no_team_info);
                noDataText.setVisibility(View.VISIBLE);
                infoContainer.setVisibility(View.GONE);
            } else {
                noDataText.setVisibility(View.GONE);
                TextView teamName = ((TextView) view.findViewById(R.id.team_name));
                if (team.getNickname().isEmpty()) {
                    teamName.setText("Team " + team.getTeamNumber());
                } else {
                    teamName.setText(team.getNickname());
                }

                View teamLocationContainer = view.findViewById(R.id.team_location_container);
                if (team.getLocation().isEmpty()) {
                    // No location; hide the location view
                    teamLocationContainer.setVisibility(View.GONE);
                } else {
                    // Show and populate the location view
                    ((TextView) view.findViewById(R.id.team_location))
                        .setText(team.getLocation());

                    // Tag is used to create an ACTION_VIEW intent for a maps application
                    view.findViewById(R.id.team_location_container)
                        .setTag("geo:0,0?q=" + team.getLocation().replace(" ", "+"));
                }

                view.findViewById(R.id.team_twitter_button)
                    .setTag("https://twitter" + ".com/search?q=%23" + mTeamKey);
                view.findViewById(R.id.team_youtube_button)
                    .setTag("https://www.youtube" + ".com/results?search_query=" + mTeamKey);
                view.findViewById(R.id.team_cd_button)
                    .setTag("http://www.chiefdelphi" + ".com/media/photos/tags/" + mTeamKey);
                view.findViewById(R.id.team_website_button)
                    .setTag(!team.getWebsite().isEmpty() ? team.getWebsite() :
                        "https://www.google" + ".com/search?q=" + mTeamKey);
                if (team.getFullName().isEmpty()) {
                    // No full name specified, hide the view
                    view.findViewById(R.id.team_full_name_container).setVisibility(View.GONE);
                } else {
                    // This string needs to be specially formatted
                    SpannableString string = new SpannableString("aka " + team.getFullName());
                    string.setSpan(new TextAppearanceSpan(getActivity(),
                            R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    ((TextView) view.findViewById(R.id.team_full_name)).setText(string);
                }

                view.findViewById(R.id.team_next_match_label).setVisibility(View.GONE);
                view.findViewById(R.id.team_next_match_details).setVisibility(View.GONE);
                view.findViewById(R.id.team_info_container).setVisibility(View.VISIBLE);
            }
            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(Constants.LOG_TAG, throwable.toString());
    }
}
