package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.ModelRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.api.model.IAward;

import android.content.ContentValues;

import javax.annotation.Nullable;

public class Award implements IAward, RenderableModel, TbaDatabaseModel {

    private Integer awardType = null;
    private String eventKey = null;
    private String key = null;
    private Long lastModified = null;
    private String name = null;
    private String recipientList = null;
    private Integer year = null;

    private JsonArray winners;

    public Award() {
        winners = null;
    }

    public Award(String eventKey, String name, int year, JsonArray winners) {
        this();
        setEventKey(eventKey);
        setName(name);
        setYear(year);
        setWinners(winners);
    }

    @Override public Integer getAwardType() {
        return awardType;
    }

    @Override public void setAwardType(Integer awardType) {
        this.awardType = awardType;
    }

    @Override public String getEventKey() {
        return eventKey;
    }

    @Override public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @Nullable @Override public String getKey() {
        return key;
    }

    @Override public void setKey(String key) {
        this.key = key;
    }

    @Nullable @Override public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Nullable @Override public String getRecipientList() {
        return recipientList;
    }

    @Override public void setRecipientList(String recipientList) {
        this.recipientList = recipientList;
    }

    @Override public Integer getYear() {
        return year;
    }

    @Override public void setYear(Integer year) {
        this.year = year;
    }

    @Nullable
    public JsonArray getWinners() {
        if (winners == null) {
            String recipients = getRecipientList();
            if (recipients == null) {
                return null;
            }
            winners = JSONHelper.getasJsonArray(recipients);
        }
        return winners;
    }

    public void setWinners(JsonArray winners) {
        setRecipientList(winners.toString());
        this.winners = winners;
    }

    public void setWinners(String winnersJson) {
        setRecipientList(winnersJson);
    }

    @Nullable
    public int getEnum() {
        return getAwardType();
    }

    public void setEnum(int awardType) {
        setAwardType(awardType);
    }

    @Override
    public ContentValues getParams() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AwardsTable.KEY, getKey());
        contentValues.put(AwardsTable.ENUM, getEnum());
        contentValues.put(AwardsTable.EVENTKEY, getEventKey());
        contentValues.put(AwardsTable.NAME, getName());
        contentValues.put(AwardsTable.YEAR, getYear());
        contentValues.put(AwardsTable.WINNERS, getRecipientList());
        return contentValues;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListElement render(ModelRendererSupplier supplier) {
        ModelRenderer<Award, ?> renderer = supplier.getRendererForType(ModelType.AWARD);
        return renderer != null ? renderer.renderFromModel(this, null) : null;
    }
}
