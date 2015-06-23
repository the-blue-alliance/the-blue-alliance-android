package com.thebluealliance.androidclient.modules;

import android.app.Activity;

import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.fragments.team.TeamEventsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    TeamInfoFragment.class,
    TeamEventsFragment.class,
    TeamMediaFragment.class
  },
  includes = {
    DatafeedModule.class
  }
)
public class ViewTeamModule {

    private Activity mActivity;

    public ViewTeamModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber();
    }

    @Provides
    public EventListSubscriber provideEventListSubscriber() {
        return new EventListSubscriber(mActivity);
    }

    @Provides
    public MediaListSubscriber provideMediaListSubscriber() {
        return new MediaListSubscriber(mActivity);
    }
}
