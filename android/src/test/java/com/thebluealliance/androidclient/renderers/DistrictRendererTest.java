package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.DistrictListElement;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.types.ModelType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class DistrictRendererTest {
    private static final String DISTRICT_KEY = "2015ne";

    @Mock APICache mDatafeed;
    private District mDistrict;
    private DistrictRenderer mRenderer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mDistrict = ModelMaker.getModelList(District.class, "2015_districts").get(3);
        mDistrict.setYear(2015);
        mDistrict.setKey(DISTRICT_KEY);
        mRenderer = new DistrictRenderer(mDatafeed);
    }

    @Test
    public void testRenderFromKey() {
        when(mDatafeed.fetchDistrict(DISTRICT_KEY)).thenReturn(Observable.just(mDistrict));
        when(mDatafeed.fetchDistrictEvents("2015ne")).thenReturn(Observable.just(new ArrayList<>()));
        DistrictListElement element = mRenderer.renderFromKey(DISTRICT_KEY, ModelType.DISTRICT, null);
        assertDistrictItem(element, 0, 2015, false);
    }

    @Test
    public void testNullRenderFromKey(){
        when(mDatafeed.fetchDistrict(DISTRICT_KEY)).thenReturn(Observable.just(null));
        when(mDatafeed.fetchDistrictEvents("2015ne")).thenReturn(Observable.just(new ArrayList<>()));
        DistrictListElement element = mRenderer.renderFromKey(DISTRICT_KEY, ModelType.DISTRICT, null);
        assertNull(element);
    }

    @Test
    public void testRenderFromKeyNullEvents() {
        when(mDatafeed.fetchDistrict(DISTRICT_KEY)).thenReturn(Observable.just(mDistrict));
        when(mDatafeed.fetchDistrictEvents("2015ne")).thenReturn(Observable.just(null));
        DistrictListElement element = mRenderer.renderFromKey(DISTRICT_KEY, ModelType.DISTRICT, null);
        assertDistrictItem(element, 0, 2015, false);
    }

    @Test
    public void testRenderFromModel() {
        DistrictListElement element = mRenderer.renderFromModel(mDistrict, new DistrictRenderer.RenderArgs(12, true));
        assertDistrictItem(element, 12, 2015, true);
    }

    private void assertDistrictItem(DistrictListElement element, int numEvents, int year, boolean showMyTba) {
        assertNotNull(element);
        assertEquals(element.key, DISTRICT_KEY);
        assertEquals(element.numEvents, numEvents);
        assertEquals(element.showMyTba, showMyTba);
        assertEquals(element.year, year);
    }
}