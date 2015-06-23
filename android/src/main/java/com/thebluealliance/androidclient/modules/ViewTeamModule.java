package com.thebluealliance.androidclient.modules;

import android.content.Context;

import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.fragments.team.TeamEventsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import dagger.Module;
import dagger.Provides;

@Module(
  injects = {
    TeamInfoFragment.class,
    TeamEventsFragment.class
  },
  includes = {
    DatafeedModule.class
  }
)
public class ViewTeamModule {

    private Context mContext;

    public ViewTeamModule(Context context) {
        mContext = context;
    }

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber();
    }

    @Provides
    public EventListSubscriber provideEventListSubscriber() {
        return new EventListSubscriber(mContext);
    }
}
