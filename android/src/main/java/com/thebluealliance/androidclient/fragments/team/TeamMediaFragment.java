package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.binders.ExpandableListBinder;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.views.ExpandableListView;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.schedulers.Schedulers;

public class TeamMediaFragment extends DatafeedFragment<MediaListSubscriber, ExpandableListBinder> {

    public static final String TEAM_KEY = "team", YEAR = "year";

    private String mTeamKey;
    private int mYear;

    public static Fragment newInstance(String teamKey, int year) {
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        args.putInt(YEAR, year);

        Fragment f = new TeamMediaFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComponent.inject(this);

        Bundle args = getArguments();
        if (args == null || !args.containsKey(TEAM_KEY) || !args.containsKey(YEAR)) {
            throw new IllegalArgumentException("TeamMediaFragment must be constructed with a team key and year");
        }
        mTeamKey = args.getString(TEAM_KEY);
        mYear = args.getInt(YEAR, -1);
        if (mYear == -1) {
            mYear = Utilities.getCurrentYear();
        }
        mSubscriber.setConsumer(mBinder);
        mBinder.setContext(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_media, container, false);
        mBinder.mExpandableList = (ExpandableListView) v.findViewById(R.id.team_media_list);
        mBinder.mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (mSubscriber != null) {
            mSubscriber.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<List<Media>> mTeamObservable = mDatafeed.fetchTeamMediaInYear(mTeamKey, mYear);
        mTeamObservable
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.computation())
          .subscribe(mSubscriber);
        EventBus.getDefault().register(this);
    }

    public void onEvent(YearChangedEvent event) {
        mYear = event.getYear();
    }
}