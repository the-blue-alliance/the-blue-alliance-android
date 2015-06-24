package com.thebluealliance.androidclient.modules;

import com.thebluealliance.androidclient.fragments.team.TeamEventsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {SubscriberModule.class, DatafeedModule.class})
public interface FragmentComponent {
    void inject(TeamInfoFragment fragment);
    void inject(TeamEventsFragment fragment);
    void inject(TeamMediaFragment fragment);
}
