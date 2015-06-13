package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.TBAAndroidModule;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;

import dagger.Module;
import dagger.Provides;

@Module(
               injects = {
                                 ViewTeamActivity.class
               },
               addsTo = TBAAndroidModule.class,
               library = true
)
public class ViewTeamModule {

    @Provides
    public TeamInfoSubscriber provideTeamInfoSubscriber() {
        return new TeamInfoSubscriber();
    }
}
