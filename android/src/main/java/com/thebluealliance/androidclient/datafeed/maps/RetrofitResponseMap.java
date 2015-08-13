package com.thebluealliance.androidclient.datafeed.maps;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A class that extracts the body from a Retrofit {@link Response} and calls an action (for use
 * in writing to the db), if desired
 */
@Singleton
public class RetrofitResponseMap {

    @Inject
    public RetrofitResponseMap() {
    }

    /**
     * Just returns the body from the response
     */
    public synchronized <T> Observable<T> getResponseBody(Observable<Response<T>> response) {
        return response.map(Response::body);
    }

    /**
     * If the response is successful (HTTP code between [200...300)), then call the provided
     * Action (which should write to the db) on the body
     */
    public synchronized <T> Observable<T> getAndWriteResponseBody(
      Observable<Response<T>> response,
      Action1<T> writer) {
        return response.map(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(data);
            }
            return data;
        });
    }

    /**
     * Same as {@link #getAndWriteResponseBody(Observable, Action1)} except this method allows for
     * a map to be passed before write. The map does not affect the return value
     */
    public synchronized <T, W> Observable<T> getAndWriteMappedResponseBody(
      Observable<Response<T>> response,
      Func1<T, W> writerMap,
      Action1<W> writer) {
        return response.map(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(writerMap.call(data));
            }
            return data;
        });
    }

    /**
     * Same as {@link #getAndWriteResponseBody(Observable, Action1)} except this method allows for
     * the response to be mapped, both for the return value and for the write
     */
    public synchronized <T, W> Observable<W> mapAndWriteResponseBody(
      Observable<Response<T>> response,
      Func1<T, W> map,
      Action1<W> writer) {
        return response.map(response1 -> {
            W data = map.call(response1.body());
            if (shouldWriteData(response1)) {
                writer.call(data);
            }
            return data;
        });
    }

    private static synchronized boolean shouldWriteData(Response response) {
        com.squareup.okhttp.Response cacheResponse = response.raw().cacheResponse();
        return cacheResponse == null || cacheResponse.code() == 504;
    }
}
