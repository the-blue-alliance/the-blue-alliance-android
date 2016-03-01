package com.thebluealliance.androidclient.imgur;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;
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
        if (response.isSuccess()) {
            UploadResponse uploadResponse = response.body();
            mSuggestionController.suggest(
                    mTeamKey,
                    mYear,
                    uploadResponse.data.link,
                    uploadResponse.data.deletehash);
        } else {
            Log.e(Constants.LOG_TAG, "Error uploading imgur image\n"
                    +response.code() + " " + response.message());
        }
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
