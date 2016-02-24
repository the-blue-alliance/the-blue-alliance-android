package com.thebluealliance.imgur;

import com.squareup.okhttp.RequestBody;

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
    void uploadImage(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            @Query("album") String albumId,
            @Query("account_url") String username,
            @Body RequestBody file
    );
}
