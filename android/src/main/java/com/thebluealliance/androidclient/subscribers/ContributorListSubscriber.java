package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.listitems.ContributorListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;

import java.util.ArrayList;
import java.util.List;

public class ContributorListSubscriber extends BaseAPISubscriber<JsonElement, List<ListItem>> {

    public ContributorListSubscriber() {
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();

        if (mAPIData == null || !mAPIData.isJsonArray()) {
            return;
        }

        JsonArray data = mAPIData.getAsJsonArray();
        for (JsonElement e : data) {
            JsonObject user = e.getAsJsonObject();
            String username = user.get("login").getAsString();
            int contributionCount = user.get("contributions").getAsInt();
            String avatarUrl = user.get("avatar_url").getAsString();
            mDataToBind.add(new ContributorListElement(username, contributionCount, avatarUrl));
        }
    }
}
