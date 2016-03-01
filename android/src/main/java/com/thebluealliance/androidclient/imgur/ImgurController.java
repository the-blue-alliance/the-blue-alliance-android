package com.thebluealliance.androidclient.imgur;

import android.content.Context;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.imgur.ImgurApi;
import com.thebluealliance.imgur.responses.UploadResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
            Context context,
            String filepath,
            String title,
            String description,
            ImgurSuggestionCallback callback) throws IOException {
        String authToken = getAuthHeader(context);
        File file = new File(filepath);
        FileInputStream stream = new FileInputStream(file);
        byte[] buf = new byte[stream.available()];
        while(stream.read(buf) != -1);
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), buf);
        Call<UploadResponse> apiCall = mImgurApi.uploadImage(authToken, title, description, body);
        apiCall.enqueue(callback);
    }

    private static String getAuthHeader(Context context) {
        String clientId = Utilities.readLocalProperty(context, "imgur.clientId");
        return String.format("Client-ID %1$s", clientId);
    }
}
