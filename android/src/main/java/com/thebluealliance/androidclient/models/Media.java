package com.thebluealliance.androidclient.models;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.database.TbaDatabaseModel;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.MediaRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.api.model.IMedia;

import android.content.ContentValues;

import javax.annotation.Nullable;


public class Media implements IMedia, TbaDatabaseModel, RenderableModel<Media> {

    private String detailsJson = null;
    private String foreignKey = null;
    private Long lastModified = null;
    private String type = null;

    private JsonObject details;
    private String teamKey;
    private int year;

    public Media() {
    }

    @Override
    public String getKey() {
        return getForeignKey();
    }

    public JsonObject getDetailsJson() {
        if (details == null) {
            details = JSONHelper.getasJsonObject(getDetails());
        }
        return details;
    }

    public void setDetails(String detailsJson) {
        this.detailsJson = detailsJson;
    }

    @Nullable @Override public String getDetails() {
        return detailsJson;
    }

    @Override public String getForeignKey() {
        return foreignKey;
    }

    @Override public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    @Nullable @Override public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override public String getType() {
        return type;
    }

    @Override public void setType(String type) {
        this.type = type;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public ContentValues getParams() {
        ContentValues data = new ContentValues();
        data.put(MediasTable.TYPE, getType());
        data.put(MediasTable.FOREIGNKEY, getForeignKey());
        data.put(MediasTable.TEAMKEY, getTeamKey());
        data.put(MediasTable.DETAILS, getDetails());
        data.put(MediasTable.YEAR, getYear());
        data.put(MediasTable.LAST_MODIFIED, getLastModified());
        return data;
    }

    @Override
    public ListElement render(ModelRendererSupplier rendererSupplier) {
        MediaRenderer renderer = (MediaRenderer) rendererSupplier.getRendererForType(ModelType.MEDIA);
        if (renderer == null) {
            return null;
        }
        return renderer.renderFromModel(this, null);
    }
}
