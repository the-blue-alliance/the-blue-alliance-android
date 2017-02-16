package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingResponseObject;
import com.thebluealliance.api.model.IRankingResponseObjectSortOrderInfo;

import java.util.List;

import javax.annotation.Nullable;

public class RankingResponseObject implements IRankingResponseObject {

    private IRankingItem rankings;
    private List<IRankingResponseObjectSortOrderInfo> sortOrderInfo;
    private @Nullable Long lastModified;

    @Override public IRankingItem getRankings() {
        return rankings;
    }

    @Override public void setRankings(IRankingItem rankings) {
        this.rankings = rankings;
    }

    @Override public List<IRankingResponseObjectSortOrderInfo> getSortOrderInfo() {
        return sortOrderInfo;
    }

    @Override
    public void setSortOrderInfo(List<IRankingResponseObjectSortOrderInfo> sortOrderInfo) {
        this.sortOrderInfo = sortOrderInfo;
    }

    @Override @Nullable public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }
}
