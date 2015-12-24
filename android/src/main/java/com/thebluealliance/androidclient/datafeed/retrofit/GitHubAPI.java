package com.thebluealliance.androidclient.datafeed.retrofit;

import com.google.gson.JsonArray;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Interface to access the GitHub API in relation to the app repo
 * Used in {@link com.thebluealliance.androidclient.fragments.ContributorsFragment}
 */
public interface GitHubAPI {

    @GET("/repos/{user}/{repo}/contributors")
    Observable<JsonArray> fetchRepoContributors(
            @Path("user") String user,
            @Path("repo") String repo);
}
