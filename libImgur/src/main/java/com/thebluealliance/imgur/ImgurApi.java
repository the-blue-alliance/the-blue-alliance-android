package com.thebluealliance.imgur;

import com.squareup.okhttp.RequestBody;
import com.thebluealliance.imgur.responses.UploadResponse;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Retrofit service for the Imgur v3 API
 * See docs at https://api.imgur.com/
 */
public interface ImgurApi {
    String SERVER_URL = "https://api.imgur.com";

    @Multipart
    @POST("/3/image")
    Call<UploadResponse> uploadImage(
            @Header("Authorization") String auth,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("image\"; filename=\"picture.jpg\" ") RequestBody file
    );
}
