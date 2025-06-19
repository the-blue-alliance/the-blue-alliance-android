package com.thebluealliance.androidclient.database.model;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.models.District;

import javax.annotation.Nullable;

public class DistrictDbModel implements TbaDatabaseModel {

    private String key;
    private String abbreviation;
    private Integer year;
    private String displayName;
    private Long lastModified;

    public DistrictDbModel() {
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Nullable
    @Override
    public Long getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public ContentValues getParams(Gson gson) {
        ContentValues params = new ContentValues();
        params.put(DistrictsTable.KEY, getKey());
        params.put(DistrictsTable.ABBREV, getAbbreviation());
        params.put(DistrictsTable.YEAR, getYear());
        params.put(DistrictsTable.NAME, getDisplayName());
        params.put(DistrictsTable.LAST_MODIFIED, getLastModified());
        return params;
    }

    public static DistrictDbModel fromDistrict(District district) {
        DistrictDbModel model = new DistrictDbModel();
        model.setKey(district.getKey());
        model.setAbbreviation(district.getAbbreviation());
        model.setYear(district.getYear());
        model.setDisplayName(district.getDisplayName());
        model.setLastModified(district.getLastModified());
        return model;
    }

    public District toDistrict() {
        District district = new District();
        district.setKey(getKey());
        district.setAbbreviation(getAbbreviation());
        district.setYear(getYear());
        district.setDisplayName(getDisplayName());
        district.setLastModified(getLastModified());
        return district;
    }
}
