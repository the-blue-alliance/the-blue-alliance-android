package com.thebluealliance.androidclient.imgur;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.thebluealliance.imgur.ImgurApi;
import com.thebluealliance.imgur.responses.UploadResponse;

import java.io.File;

import javax.inject.Inject;

import retrofit.Call;

/**
 * Class than handles interactions with the imgur API
 */
public class ImgurController {

    private final ImgurApi mImgurApi;

    @Inject
    public ImgurController(ImgurApi imgurApi) {
        mImgurApi = imgurApi;
    }

    public void uploadImage(
            String filepath,
            String title,
            String description,
            ImgurSuggestionCallback callback) {
        String authToken = "meow"; //TODO handle imgur auth
        File file = new File(filepath);
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        Call<UploadResponse> apiCall = mImgurApi.uploadImage(authToken, title, description, body);
        apiCall.enqueue(callback);
    }
}
