package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.views.ExpandableListView;
import com.thebluealliance.androidclient.views.NoDataView;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;

public class TeamMediaFragment extends DatafeedFragment<
        List<Media>,
        List<ListGroup>,
        MediaListSubscriber,
        ExpandableListViewBinder> {

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
        Bundle args = getArguments();
        if (args == null || !args.containsKey(TEAM_KEY) || !args.containsKey(YEAR)) {
            throw new IllegalArgumentException("TeamMediaFragment must be constructed with a team key and year");
        }
        mTeamKey = args.getString(TEAM_KEY);
        mYear = args.getInt(YEAR, -1);
        if (mYear == -1) {
            mYear = Utilities.getCurrentYear();
        }
        super.onCreate(savedInstanceState);

        mBinder.setExpandMode(ExpandableListViewBinder.MODE_EXPAND_ALL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.expandable_list_view_with_spinner, container, false);
        mBinder.mExpandableListView = (ExpandableListView) v.findViewById(R.id.list);
        mBinder.mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mBinder.setNoDataView((NoDataView) v.findViewById(R.id.no_data));
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
        EventBus.getDefault().register(this);
    }

    public void onEvent(YearChangedEvent event) {
        mYear = event.getYear();
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Media>> getObservable() {
        return mDatafeed.fetchTeamMediaInYear(mTeamKey, mYear);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_photo_camera_black_48dp, R.string.no_media_data);
    }
}