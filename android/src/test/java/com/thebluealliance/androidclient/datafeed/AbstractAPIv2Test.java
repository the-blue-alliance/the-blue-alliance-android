package com.thebluealliance.androidclient.datafeed;

import com.squareup.okhttp.OkHttpClient;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.modules.DatafeedModule;

import org.junit.Before;

public abstract class AbstractAPIv2Test {

    APIv2 mApi;

    @Before
    public void setup() {
        mApi = DatafeedModule
          .getRetrofit(DatafeedModule.getGson(), new OkHttpClient())
          .create(APIv2.class);
    }
}
