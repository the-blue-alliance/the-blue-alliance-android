package com.thebluealliance.androidclient.test.datafeed;

import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.datafeed.APIv2ErrorHandler;
import com.thebluealliance.androidclient.datafeed.APIv2RequestInterceptor;
import com.thebluealliance.androidclient.datafeed.RetrofitConverter;

import org.junit.Before;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public abstract class AbstractAPIv2Test {

    APIv2 mApi;

    @Before
    public void setup() {
        mApi = new RestAdapter.Builder()
                .setEndpoint(APIv2.TBA_APIv2_URL)
                .setConverter(new RetrofitConverter())
                .setRequestInterceptor(new APIv2RequestInterceptor())
                .setErrorHandler(new APIv2ErrorHandler())
                .setClient(new OkClient())
                .build()
                .create(APIv2.class);
    }
}
