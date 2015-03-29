package com.thebluealliance.androidclient.test.datafeed;

import com.thebluealliance.androidclient.datafeed.APIHelper;
import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;

/**
 * Created by phil on 3/28/15.
 */
@RunWith(RobolectricTestRunner.class)
public class APIv2TeamTest {

    APIv2 api;

    @Before
    public void setupAPIClient(){
        APIv2.APIv2RequestInterceptor.isFromJunit = true;
        api = APIHelper.getAPI();
    }

    @Test
    public void testFetchTeam() throws BasicModel.FieldNotDefinedException {
        Team team = api.fetchTeam("frc1124", null);
        assertEquals(team.getTeamKey(), "frc1124");
    }

    @Test
    public void testFetchTeamObservable(){
        api.fetchTeamObservable("frc1124", null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Team>() {
                    @Override
                    public void call(Team team) {
                        try {
                            assertEquals(team.getTeamKey(), "frc1124");
                        } catch (BasicModel.FieldNotDefinedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
