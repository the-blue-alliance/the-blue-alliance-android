package com.thebluealliance.androidclient.imgur;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.gce.TbaSuggestionController;
import com.thebluealliance.imgur.responses.UploadResponse;

import android.support.annotation.UiThread;
import android.util.Log;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import rx.schedulers.Schedulers;

/**
 * Class that takes a successful upload from the imgur API and suggests it TBA for a given team/year
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
    @UiThread
    public void onResponse(Response<UploadResponse> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            UploadResponse uploadResponse = response.body();
            Log.d(Constants.LOG_TAG, "Uploaded imgur image: " + uploadResponse.data.link);

            //noinspection WrongThread - we schedule on io thread, linter isn't smart enough to tell
            Schedulers.io().createWorker().schedule(() -> mSuggestionController.suggest(
                    mTeamKey,
                    mYear,
                    uploadResponse.data.link,
                    uploadResponse.data.deletehash));
        } else {
            Log.e(Constants.LOG_TAG, "Error uploading imgur image\n"
                    +response.code() + " " + response.message());
        }
    }

    @Override
    @UiThread
    public void onFailure(Throwable t) {
        Log.e(Constants.LOG_TAG, "Failed to upload image to imgur");
        t.printStackTrace();
    }
}
