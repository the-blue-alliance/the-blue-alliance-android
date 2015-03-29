package com.thebluealliance.androidclient.test.datafeed;

import com.thebluealliance.androidclient.datafeed.APIHelper;
import com.thebluealliance.androidclient.datafeed.APIv2;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assume.assumeTrue;


/**
 * Class that hits TBA endpoints and tests that we can get data back
 * This class is ignored by default, so we aren't always hitting the server from automated tests
 * Change the 'shouldRun' variable to true if you'd like to run it.
 * Created by phil on 3/28/15.
 */
@RunWith(RobolectricTestRunner.class)
public abstract class AbstractAPIv2Test {
    APIv2 api;

    @Before
    public void setupAPIClient(){
        boolean shouldRun = true; //TODO move to configuration file

        APIv2.APIv2RequestInterceptor.isFromJunit = true;
        api = APIHelper.getAPI();
        assumeTrue(shouldRun);
    }

}
