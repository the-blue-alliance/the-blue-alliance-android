package com.thebluealliance.imgur;

import com.squareup.okhttp.RequestBody;
import com.thebluealliance.imgur.responses.UploadResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Retrofit service for the Imgur v3 API
 * See docs at https://api.imgur.com/
 */
public interface ImgurApi {
    String SERVER_URL = "https://api.imgur.com";

    @POST("/3/image")
    Call<UploadResponse> uploadImage(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            @Body RequestBody file
    );
}
