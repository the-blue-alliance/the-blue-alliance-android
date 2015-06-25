package com.thebluealliance.androidclient.modules;

import android.app.Activity;

import com.thebluealliance.androidclient.subscribers.EventInfoSubscriber;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.subscribers.MediaListSubscriber;
import com.thebluealliance.androidclient.subscribers.TeamInfoSubscriber;
import dagger.Module;
import dagger.Provides;

@Module
public class SubscriberModule {

    private Activity mActivity;

    public SubscriberModule(Activity activity) {
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

    @Provides
    public EventInfoSubscriber provideEventInfoSubscriber() {
            return new EventInfoSubscriber();
    }
}
