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

import android.content.ContentValues;


public class Media extends com.thebluealliance.api.model.Media implements TbaDatabaseModel,
                                                                          RenderableModel<Media>
{

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
