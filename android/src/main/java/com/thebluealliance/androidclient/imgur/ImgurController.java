package com.thebluealliance.androidclient.imgur;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.imgur.ImgurApi;
import com.thebluealliance.imgur.responses.UploadResponse;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import retrofit.Call;

/**
 * Class than handles interactions with {@link ImgurApi}
 */
public class ImgurController {

    private final ImgurApi mImgurApi;

    @Inject
    public ImgurController(ImgurApi imgurApi) {
        mImgurApi = imgurApi;
    }

    /**
     * Upload an image to Imgur
     * @param context Android context
     * @param filepath Path to the image on the local device
     * @param title Imgur title for the image
     * @param description Imgur description for the image
     * @param callback Callback to execute after the upload request completes.
     * @throws IOException
     */
    public void uploadImage(
            Context context,
            String filepath,
            String title,
            String description,
            ImgurSuggestionCallback callback) throws IOException {
        String authToken = getAuthHeader(context);
        File file = new File(filepath);
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);
        Call<UploadResponse> apiCall = mImgurApi.uploadImage(authToken, titlePart, descPart, body);
        apiCall.enqueue(callback);
    }

    private static String getAuthHeader(Context context) {
        String clientId = Utilities.readLocalProperty(context, "imgur.clientId");
        return String.format("Client-ID %1$s", clientId);
    }
}
