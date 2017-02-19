package com.thebluealliance.androidclient.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.ModelRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.api.model.IAward;
import com.thebluealliance.api.model.IAwardRecipient;

import android.content.ContentValues;

import java.util.List;

import javax.annotation.Nullable;

public class Award implements IAward, RenderableModel, TbaDatabaseModel {

    private Integer awardType = null;
    private String eventKey = null;
    private String key = null;
    private Long lastModified = null;
    private String name = null;
    private List<IAwardRecipient> recipientList = null;
    private Integer year = null;

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

    @Nullable @Override public List<IAwardRecipient> getRecipientList() {
        return recipientList;
    }

    @Override public void setRecipientList(List<IAwardRecipient> recipientList) {
        this.recipientList = recipientList;
    }

    @Override public Integer getYear() {
        return year;
    }

    @Override public void setYear(Integer year) {
        this.year = year;
    }

    @Nullable
    public Integer getEnum() {
        return getAwardType();
    }

    public void setEnum(int awardType) {
        setAwardType(awardType);
    }

    @Override
    public ContentValues getParams(Gson gson) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AwardsTable.KEY, getKey());
        contentValues.put(AwardsTable.ENUM, getEnum());
        contentValues.put(AwardsTable.EVENTKEY, getEventKey());
        contentValues.put(AwardsTable.NAME, getName());
        contentValues.put(AwardsTable.YEAR, getYear());
        contentValues.put(AwardsTable.WINNERS, gson.toJson(getRecipientList(), new TypeToken<List<AwardRecipient>>(){}.getType()));
        contentValues.put(AwardsTable.LAST_MODIFIED, getLastModified());
        return contentValues;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListElement render(ModelRendererSupplier supplier) {
        ModelRenderer<Award, ?> renderer = supplier.getRendererForType(ModelType.AWARD);
        return renderer != null ? renderer.renderFromModel(this, null) : null;
    }

    public static class AwardRecipient implements IAwardRecipient {
        private @Nullable String awardee;
        private @Nullable String teamKey;
        private @Nullable Long lastModified;

        @Override @Nullable public String getAwardee() {
            return awardee;
        }

        @Override public void setAwardee(@Nullable String awardee) {
            this.awardee = awardee;
        }

        @Override @Nullable public String getTeamKey() {
            return teamKey;
        }

        @Override public void setTeamKey(@Nullable String teamKey) {
            this.teamKey = teamKey;
        }

        @Nullable public Long getLastModified() {
            return lastModified;
        }

        public void setLastModified(@Nullable Long lastModified) {
            this.lastModified = lastModified;
        }
    }
}
