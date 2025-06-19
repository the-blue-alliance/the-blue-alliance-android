package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.DistrictHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import thebluealliance.api.model.District;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AddDistrictKeysTest {

    private District mDistrict;
    private int mYear;
    private AddDistrictKeys mKeyAdder;

    @Before
    public void setUp() {
        mDistrict = ModelMaker.getModelList(District.class, "2015_districts").get(3);
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

        assertEquals(mDistrict.getYear().intValue(), mYear);
        assertEquals(mDistrict.getKey(), DistrictHelper.generateKey(mDistrict.getAbbreviation(), mYear));
    }
}