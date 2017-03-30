package com.thebluealliance.androidclient.models;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.types.EventDetailType;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingResponseObject;
import com.thebluealliance.api.model.IRankingSortOrder;

import java.util.List;

import javax.annotation.Nullable;

public class RankingResponseObject implements IRankingResponseObject {

    private List<IRankingItem> rankings;
    private List<IRankingSortOrder> sortOrderInfo;
    private List<IRankingSortOrder> extraStatsInfo;
    private String eventKey;

    private @Nullable Long lastModified;

    @Override public List<IRankingItem> getRankings() {
        return rankings;
    }

    @Override public void setRankings(List<IRankingItem> rankings) {
        this.rankings = rankings;
    }

    @Override public List<IRankingSortOrder> getSortOrderInfo() {
        return sortOrderInfo;
    }

    @Override
    public void setSortOrderInfo(List<IRankingSortOrder> sortOrderInfo) {
        this.sortOrderInfo = sortOrderInfo;
    }

    @Nullable @Override
    public List<IRankingSortOrder> getExtraStatsInfo() {
        return extraStatsInfo;
    }

    @Override
    public void setExtraStatsInfo(List<IRankingSortOrder> extraStatsInfo) {
        this.extraStatsInfo = extraStatsInfo;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @Override @Nullable public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }

    public EventDetail toEventDetail(Gson gson) {
        EventDetail eventDetail = new EventDetail(eventKey, EventDetailType.RANKINGS);
        String rankingJson = gson.toJson(this, RankingResponseObject.class);
        eventDetail.setJsonData(rankingJson);
        eventDetail.setLastModified(lastModified);
        return eventDetail;
    }
}
