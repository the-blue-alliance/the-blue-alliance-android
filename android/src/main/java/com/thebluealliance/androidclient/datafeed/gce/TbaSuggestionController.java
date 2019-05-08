package com.thebluealliance.androidclient.datafeed.gce;

import androidx.annotation.WorkerThread;

import com.appspot.tbatv_prod_hrd.TeamMedia;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesMediaSuggestionMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            public void onResponse(Call<ModelsMobileApiMessagesBaseResponse> call, Response<ModelsMobileApiMessagesBaseResponse> response) {
                //TODO stuff
            }

            @Override
            public void onFailure(Call<ModelsMobileApiMessagesBaseResponse> call, Throwable t) {
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
