package com.thebluealliance.androidclient.modules.components;

import android.support.v4.app.Fragment;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.modules.DatafeedModule;
import com.thebluealliance.androidclient.datafeed.RetrofitConverter;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DatafeedModule.class})
public interface DatafeedComponent {
    void inject(CacheableDatafeed datafeed);
    void inject(DatafeedModule module);
    void inject(RetrofitConverter converter);
    void inject(Fragment fragment);
    void inject(APICache cache);
}
