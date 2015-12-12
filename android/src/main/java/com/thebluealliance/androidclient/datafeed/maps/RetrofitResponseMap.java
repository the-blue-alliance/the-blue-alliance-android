package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.database.writers.BaseDbWriter;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Response;
import rx.Observable;
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
     * If the response is successful, call the provided db writer without cleaning previous info
     */
    public synchronized <T> Observable<T> getAndWriteResponseBody(
            Observable<Response<T>> response,
            BaseDbWriter<T> writer) {
        return getAndWriteResponseBody(response, writer, null, null, null);
    }

    /**
     * If the response is successful (HTTP code between [200...300)), then call the provided
     * db writer (which should write to the db) on the body
     */
    public synchronized <T> Observable<T> getAndWriteResponseBody(
            Observable<Response<T>> response,
            BaseDbWriter<T> writer,
            String dbTable,
            String sqlWhere,
            String[] whereArgs) {
        return response.map(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(dbTable, sqlWhere, whereArgs, data);
            }
            return data;
        });
    }

    /**
     * Map the response value before a write, does not clean db before
     */
    public synchronized <T, W> Observable<T> getAndWriteMappedResponseBody(
            Observable<Response<T>> response,
            Func1<T, W> writerMap,
            BaseDbWriter<W> writer) {
        return getAndWriteMappedResponseBody(response, writerMap, writer, null, null, null);
    }

    /**
     * Same as {@link #getAndWriteResponseBody(Observable, BaseDbWriter)} except this method allows
     * for
     * a map to be passed before write. The map does not affect the return value
     */
    public synchronized <T, W> Observable<T> getAndWriteMappedResponseBody(
            Observable<Response<T>> response,
            Func1<T, W> writerMap,
            BaseDbWriter<W> writer,
            String dbTable,
            String sqlWhere,
            String[] whereArgs) {
        return response.map(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(dbTable, sqlWhere, whereArgs, writerMap.call(data));
            }
            return data;
        });
    }

    /**
     * Return the mapped value also, without cleaning before
     */
    public synchronized <T, W> Observable<W> mapAndWriteResponseBody(
            Observable<Response<T>> response,
            Func1<T, W> map,
            BaseDbWriter<W> writer) {
        return mapAndWriteResponseBody(response, map, writer, null, null, null);
    }

    /**
     * Same as {@link #getAndWriteResponseBody(Observable, BaseDbWriter)} except this method allows
     * for
     * the response to be mapped, both for the return value and for the write
     */
    public synchronized <T, W> Observable<W> mapAndWriteResponseBody(
            Observable<Response<T>> response,
            Func1<T, W> map,
            BaseDbWriter<W> writer,
            String dbTable,
            String sqlWhere,
            String[] whereArgs) {
        return response.map(response1 -> {
            W data = map.call(response1.body());
            if (shouldWriteData(response1)) {
                writer.call(dbTable, sqlWhere, whereArgs, data);
            }
            return data;
        });
    }

    private static synchronized boolean shouldWriteData(Response response) {
        com.squareup.okhttp.Response cacheResponse = response.raw().cacheResponse();
        return cacheResponse == null || cacheResponse.code() == 504;
    }
}
