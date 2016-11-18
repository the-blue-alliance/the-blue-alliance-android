package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.activities.MoreSearchResultsActivity;
import com.thebluealliance.androidclient.activities.SearchResultsActivity;
import com.thebluealliance.androidclient.background.RecreateSearchIndexes;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.receivers.NotificationChangedReceiver;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TBAAndroidModule.class})
public interface DbComponent {

    void inject(MoreSearchResultsActivity activity);
    void inject(SearchResultsActivity activity);
    void inject(RecreateSearchIndexes service);
    void inject(NotificationChangedReceiver receiver);

}
