package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.modules.HasModule;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.views.ExpandableListView;

import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TeamMediaFragment extends DatafeedFragment<List<Media>, ExpandableListAdapter> {

    public static final String TEAM_KEY = "team", YEAR = "year";

    private String mTeamKey;
    private int mYear;
    private ExpandableListView mExpandableList;
    private ProgressBar mProgressBar;

    @Inject MediaListSubscriber mSubscriber;

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
        if (getActivity() instanceof HasModule) {
            ObjectGraph fragmentGraph = ObjectGraph.create(((HasModule) getActivity()).getModule());
            fragmentGraph.inject(this);
        }

        Bundle args = getArguments();
        if (args == null || !args.containsKey(TEAM_KEY) || !args.containsKey(YEAR)) {
            throw new IllegalArgumentException("TeamMediaFragment must be constructed with a team key and year");
        }
        mTeamKey = args.getString(TEAM_KEY);
        mYear = args.getInt(YEAR, -1);
        if (mYear == -1) {
            mYear = Utilities.getCurrentYear();
        }
        mSubscriber.setConsumer(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_media, container, false);
        mExpandableList = (ExpandableListView) v.findViewById(R.id.team_media_list);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<List<Media>> mTeamObservable = mDatafeed.fetchTeamMediaInYear(mTeamKey, mYear);
        mTeamObservable
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(mSubscriber);
        EventBus.getDefault().register(this);
    }

    public void onEvent(YearChangedEvent event) {
        mYear = event.getYear();
    }

    @Override
    public void updateData(@Nullable ExpandableListAdapter data)
      throws BasicModel.FieldNotDefinedException {
        if (data == null || mExpandableList == null) {
            return;
        }

        if (mExpandableList.getAdapter() == null) {
            mExpandableList.setAdapter(data);
        }
        mExpandableList.setVisibility(View.VISIBLE);
        data.notifyDataSetChanged();

        for (int i = 0; i < data.groups.size(); i++) {
            mExpandableList.expandGroup(i);
        }

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        // TODO no data text
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(Constants.LOG_TAG, Log.getStackTraceString(throwable));
    }
}