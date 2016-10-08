package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.District;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class AddDistrictKeysTest {

    private District mDistrict;
    private int mYear;
    private AddDistrictKeys mKeyAdder;

    @Before
    public void setUp() {
        mDistrict = ModelMaker.getModel(District.class, "district_ne");
        mYear = 2015;
        mKeyAdder = new AddDistrictKeys(mYear);
    }

    @Test
    public void testAddDistrictKeys()  {
        List<District> districtList = new ArrayList<>();
        districtList.add(mDistrict);

        districtList = mKeyAdder.call(districtList);
        assertNotNull(districtList);
        assertEquals(districtList.size(), 1);
        assertEquals(districtList.get(0), mDistrict);

        assertEquals(mDistrict.getYear(), mYear);
        assertEquals(mDistrict.getKey(), DistrictHelper.generateKey(mDistrict.getAbbreviation(), mYear));
    }
}