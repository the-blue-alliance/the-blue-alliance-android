package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.datafeed.DataConsumer;
import com.thebluealliance.androidclient.datafeed.DatafeedModule;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import dagger.Module;
import dagger.Provides;

@Module(
    injects = {
        DatafeedFragment.class,
        TeamInfoFragment.class
    },
    includes = {
        DatafeedModule.class
    }
)
public class ViewTeamModule {

    private DataConsumer<Team> mTeamConsumer;

    public ViewTeamModule(DataConsumer<Team> teamConsumer) {
        mTeamConsumer = teamConsumer;
    }

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber(mTeamConsumer);
    }
}
