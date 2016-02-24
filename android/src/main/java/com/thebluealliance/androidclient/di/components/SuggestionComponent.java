package com.thebluealliance.androidclient.di.components;

import com.thebluealliance.androidclient.datafeed.gce.GceModule;
import com.thebluealliance.androidclient.imgur.ImgurModule;
import com.thebluealliance.androidclient.imgur.ImgurSuggestionService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {ImgurModule.class, GceModule.class},
        dependencies = {ApplicationComponent.class})
public interface SuggestionComponent {

    void inject(ImgurSuggestionService service);
}
