package com.thebluealliance.androidclient.datafeed.gce;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.appspot.tbatv_prod_hrd.TeamMedia;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesMediaSuggestionMessage;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Class that interfaces Suggestions with GCE
 */
public class TbaSuggestionController {

    private static final String TEAM_LINK = "team";
    private static final String DELETEHASH_KEY = "deletehash";

    private final TeamMedia mTeamMediaApi;
    private final GceAuthController mGceAuthController;

    @Inject
    public TbaSuggestionController(TeamMedia teamMedia, GceAuthController gceAuthController) {
        mTeamMediaApi = teamMedia;
        mGceAuthController = gceAuthController;
    }

    /**
     * <b>MUST BE CALLED FROM BACKGROUND THREAD</b>
     * Uses {@link GceAuthController#getAuthHeader()}
     */
    @WorkerThread
    public void suggest(String teamKey, int year, String link, String deletehash) {
        String authHeader = mGceAuthController.getAuthHeader();
        ModelsMobileApiMessagesMediaSuggestionMessage message = buildSuggestionMessage(
                teamKey,
                year,
                link,
                deletehash);
        Call<ModelsMobileApiMessagesBaseResponse> request = mTeamMediaApi.suggestion(authHeader, message);
        request.enqueue(new Callback<ModelsMobileApiMessagesBaseResponse>() {
            @Override
            public void onResponse(Response<ModelsMobileApiMessagesBaseResponse> response,
                                   Retrofit retrofit) {
                //TODO stuff
            }

            @Override
            public void onFailure(Throwable t) {
                //TODO stuff
            }
        });
    }

    private static ModelsMobileApiMessagesMediaSuggestionMessage buildSuggestionMessage(
            String teamKey,
            int year,
            String imgurLink,
            String deleteHash) {
        ModelsMobileApiMessagesMediaSuggestionMessage message =
                new ModelsMobileApiMessagesMediaSuggestionMessage();
        message.media_url = imgurLink;
        message.reference_key = teamKey;
        message.year = year;
        message.reference_type = TEAM_LINK;
        message.details_json = getDetailsJson(deleteHash);
        return message;
    }

    private static String getDetailsJson(String deletehash) {
        JsonObject json = new JsonObject();
        json.add(DELETEHASH_KEY, new JsonPrimitive(deletehash));
        return json.toString();
    }
}
