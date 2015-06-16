package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
        TeamInfoFragment.class
    },
    includes = {
        DatafeedModule.class
    }
)
public class ViewTeamModule {

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber();
    }
}
