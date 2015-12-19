package com.thebluealliance.androidclient.listeners;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ClickListenerModule {

    private Context mContext;

    public ClickListenerModule(Context context) {
        mContext = context;
    }

    @Provides
    public SocialClickListener provideSocialClickListener() {
        return new SocialClickListener(mContext);
    }

    @Provides
    public EventInfoContainerClickListener provideEventInfoContainerClickListener() {
        return new EventInfoContainerClickListener(mContext);
    }
}
