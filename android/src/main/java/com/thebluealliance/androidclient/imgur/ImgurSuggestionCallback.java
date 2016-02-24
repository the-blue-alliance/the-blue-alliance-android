package com.thebluealliance.androidclient.imgur;

import com.thebluealliance.androidclient.datafeed.gce.TbaSuggestionController;
import com.thebluealliance.imgur.responses.UploadResponse;

import retrofit.Callback;
import retrofit.Response;

/**
 * Class that takes a sucessful upload from the imgur API and suggests it TBA
 */
public class ImgurSuggestionCallback implements Callback<UploadResponse> {

    private final TbaSuggestionController mSuggestionController;
    private final String mTeamKey;
    private final int mYear;

    public ImgurSuggestionCallback(
            TbaSuggestionController suggestionController,
            String teamKey,
            int year) {
        mSuggestionController = suggestionController;
        mTeamKey = teamKey;
        mYear = year;
    }


    @Override
    public void onResponse(Response<UploadResponse> response) {
        UploadResponse uploadResponse = response.body();
        mSuggestionController.suggest(
                mTeamKey,
                mYear,
                uploadResponse.data.id,
                uploadResponse.data.link,
                uploadResponse.data.deletehash);
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
