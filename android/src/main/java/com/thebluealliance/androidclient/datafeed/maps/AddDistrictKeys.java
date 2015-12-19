package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;

import java.util.List;

import rx.functions.Func1;

/**
 * Because apiv2 doesn't provide year within the response, add it in later
 */
public class AddDistrictKeys implements Func1<List<District>, List<District>> {

    private int mYear;

    public AddDistrictKeys(int year) {
        mYear = year;
    }

    @Override
    public List<District> call(List<District> districts) {
        if (districts == null) {
            return null;
        }
        for (int i = 0; i < districts.size(); i++) {
            District district = districts.get(i);
            try {
                String key = DistrictHelper.generateKey(district.getAbbreviation(), mYear);
                district.setKey(key);
                district.setYear(mYear);
            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
            }
        }
        return districts;
    }
}
