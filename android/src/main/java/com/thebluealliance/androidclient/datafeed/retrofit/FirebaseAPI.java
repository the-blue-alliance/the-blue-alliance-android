package com.thebluealliance.androidclient.datafeed.retrofit;

import com.google.gson.JsonElement;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface FirebaseAPI {

    @GET("/{nodePath}.json?limitToLast=1&orderBy=\"$key\"")
    Observable<JsonElement> getOneItemFromNode(@Path("nodePath") String node);
}
