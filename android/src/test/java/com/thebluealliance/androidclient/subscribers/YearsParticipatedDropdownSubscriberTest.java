package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class YearsParticipatedDropdownSubscriberTest {
    @Mock YearsParticipatedUpdate mCallback;

    private YearsParticipatedDropdownSubscriber mSubscriber;
    private List<Integer> mYearsParticipated;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mSubscriber = new YearsParticipatedDropdownSubscriber(mCallback);
        JsonArray yearsJson = ModelMaker.getModel(JsonArray.class, "frc1124_years_participated");
        mYearsParticipated = new ArrayList<>();
        for (int i = 0; i < yearsJson.size(); i++ ) {
            mYearsParticipated.add(yearsJson.get(i).getAsInt());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testParseNullData() {
        mSubscriber.onNext(null);
    }

    @Test
    public void testParsedData() {
        int[] expected = {2015, 2014, 2013, 2012};
        mSubscriber.onNext(mYearsParticipated);
        verify(mCallback).updateYearsParticipated(expected);
    }
}