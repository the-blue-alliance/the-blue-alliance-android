package com.thebluealliance.androidclient.test.datafeed;

import com.thebluealliance.androidclient.datafeed.APIHelper;
import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.datafeed.DatafeedAdapter;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.interfaces.RefreshableHost;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertTrue;

/**
 * Created by phil on 4/7/15.
 */
@RunWith(RobolectricTestRunner.class)
public class DatafeedAdapterTest extends TestSubscriber<String> implements RefreshableHost {

    private DatafeedAdapter adapter;

    @Before
    public void setupDatafeedAdapter(){
        // tell retrofit we're working from junit
        APIv2.APIv2RequestInterceptor.isFromJunit = true;
        adapter = new DatafeedAdapter(this);
    }

    @Test
    public void testDatafeedAdapter(){
        String endpoint = String.format(APIHelper.getTBAApiUrl(null, APIHelper.QUERY.TEAM), "frc1124");
        adapter.registerEndpoint(this, endpoint, APIHelper.QUERY.TEAM);
        adapter.fetchData();
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(String s) {
        System.out.println(s);
        assertTrue(false);
    }

    @Override
    public void registerRefreshListener(RefreshListener listener) {

    }

    @Override
    public void unregisterRefreshListener(RefreshListener listener) {

    }

    @Override
    public void notifyRefreshComplete(RefreshListener completedListener) {

    }

    @Override
    public void startRefresh(RefreshListener listener) {

    }
}
