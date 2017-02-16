package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IDistrictRankingEventPoints;

public class DistrictRankingEventPoints implements IDistrictRankingEventPoints {

    private Integer alliancePoints;
    private Integer awardPoints;
    private Boolean districtCmp;
    private Integer elimPoints;
    private Integer qualPoints;
    private Integer total;
    private String eventKey;

    @Override public
    Integer getAlliancePoints() {
        return alliancePoints;
    }

    @Override
    public void setAlliancePoints(Integer alliancePoints) {
        this.alliancePoints = alliancePoints;
    }

    @Override
    public Integer getAwardPoints() {
        return awardPoints;
    }

    @Override
    public void setAwardPoints(Integer awardPoints) {
        this.awardPoints = awardPoints;
    }

    @Override
    public Boolean getDistrictCmp() {
        return districtCmp;
    }

    @Override
    public void setDistrictCmp(Boolean districtCmp) {
        this.districtCmp = districtCmp;
    }

    @Override
    public Integer getElimPoints() {
        return elimPoints;
    }

    @Override
    public void setElimPoints(Integer elimPoints) {
        this.elimPoints = elimPoints;
    }

    @Override
    public Integer getQualPoints() {
        return qualPoints;
    }

    @Override
    public void setQualPoints(Integer qualPoints) {
        this.qualPoints = qualPoints;
    }

    @Override
    public Integer getTotal() {
        return total;
    }

    @Override
    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String getEventKey() {
        return eventKey;
    }

    @Override
    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }
}
