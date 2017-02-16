package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;

import android.content.ContentValues;

import javax.annotation.Nullable;

public class DistrictTeam implements TbaDatabaseModel {

    private String key;
    private String districtKey;
    private String teamKey;
    private Integer districtEnum;
    private Integer year;
    private Integer rank;
    private String event1Key;
    private String event2Key;
    private Integer event1Points;
    private Integer event2Points;
    private String cmpKey;
    private Integer cmpPoints;
    private Integer rookiePoints;
    private Integer totalPoints;
    private String json;
    private Long lastModified;

    public DistrictTeam() {
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Nullable @Override public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getDistrictKey() {
        return districtKey;
    }

    public void setDistrictKey(String districtKey) {
        this.districtKey = districtKey;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public Integer getDistrictEnum() {
        return districtEnum;
    }

    public void setDistrictEnum(Integer districtEnum) {
        this.districtEnum = districtEnum;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getEvent1Key() {
        return event1Key;
    }

    public void setEvent1Key(String event1Key) {
        this.event1Key = event1Key;
    }

    public String getEvent2Key() {
        return event2Key;
    }

    public void setEvent2Key(String event2Key) {
        this.event2Key = event2Key;
    }

    public Integer getEvent1Points() {
        return event1Points;
    }

    public void setEvent1Points(Integer event1Points) {
        this.event1Points = event1Points;
    }

    public Integer getEvent2Points() {
        return event2Points;
    }

    public void setEvent2Points(Integer event2Points) {
        this.event2Points = event2Points;
    }

    public String getCmpKey() {
        return cmpKey;
    }

    public void setCmpKey(String cmpKey) {
        this.cmpKey = cmpKey;
    }

    public Integer getCmpPoints() {
        return cmpPoints;
    }

    public void setCmpPoints(Integer cmpPoints) {
        this.cmpPoints = cmpPoints;
    }

    public Integer getRookiePoints() {
        return rookiePoints;
    }

    public void setRookiePoints(Integer rookiePoints) {
        this.rookiePoints = rookiePoints;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public ContentValues getParams() {
        ContentValues params = new ContentValues();
        params.put(DistrictTeamsTable.KEY, getKey());
        params.put(DistrictTeamsTable.TEAM_KEY, getTeamKey());
        params.put(DistrictTeamsTable.DISTRICT_KEY, getDistrictKey());
        params.put(DistrictTeamsTable.DISTRICT_ENUM, getDistrictEnum());
        params.put(DistrictTeamsTable.YEAR, getYear());
        params.put(DistrictTeamsTable.RANK, getRank());
        params.put(DistrictTeamsTable.EVENT1_KEY, getEvent1Key());
        params.put(DistrictTeamsTable.EVENT1_POINTS, getEvent1Points());
        params.put(DistrictTeamsTable.EVENT2_KEY, getEvent2Key());
        params.put(DistrictTeamsTable.EVENT2_POINTS, getEvent2Points());
        params.put(DistrictTeamsTable.CMP_KEY, getCmpKey());
        params.put(DistrictTeamsTable.CMP_POINTS, getCmpPoints());
        params.put(DistrictTeamsTable.ROOKIE_POINTS, getRookiePoints());
        params.put(DistrictTeamsTable.TOTAL_POINTS, getTotalPoints());
        params.put(DistrictTeamsTable.JSON, getJson());
        params.put(DistrictTeamsTable.LAST_MODIFIED, getLastModified());
        return params;
    }
}
