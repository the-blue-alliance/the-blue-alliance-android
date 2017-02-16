package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRankingResponseObjectSortOrderInfo;

public class RankingResponseObjectSortOrderInfo implements IRankingResponseObjectSortOrderInfo {
    private String name;
    private Integer precision;

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Override public Integer getPrecision() {
        return precision;
    }

    @Override public void setPrecision(Integer precision) {
        this.precision = precision;
    }
}
