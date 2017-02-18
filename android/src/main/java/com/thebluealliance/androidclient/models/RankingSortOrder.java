package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRankingSortOrder;

public class RankingSortOrder implements IRankingSortOrder {
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
