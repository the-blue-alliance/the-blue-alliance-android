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

import android.content.ContentValues;

import javax.annotation.Nullable;

public class Award extends com.thebluealliance.api.model.Award implements RenderableModel,
                                                                          TbaDatabaseModel{

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
