package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.listitems.ContributorListElement;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ContributorListSubscriber extends BaseAPISubscriber<JsonElement, List<ListItem>> {

    private final Picasso mPicasso;

    @Inject
    public ContributorListSubscriber(Picasso picasso) {
        mDataToBind = new ArrayList<>();
        mPicasso = picasso;
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        JsonArray data = mAPIData.getAsJsonArray();
        for (JsonElement e : data) {
            JsonObject user = e.getAsJsonObject();
            String username = user.get("login").getAsString();
            int contributionCount = user.get("contributions").getAsInt();
            String avatarUrl = user.get("avatar_url").getAsString();
            mDataToBind.add(new ContributorListElement(mPicasso, username, contributionCount, avatarUrl));
        }
    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && mAPIData.isJsonArray();
    }
}
