package com.thebluealliance.imgur;

import okhttp3.RequestBody;
import com.thebluealliance.imgur.responses.UploadResponse;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
