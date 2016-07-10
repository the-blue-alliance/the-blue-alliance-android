package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.writers.BaseDbWriter;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

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

    public synchronized <T> void writeResponseBody(
            Observable<Response<T>> response,
            BaseDbWriter<T> writer) {
        writeResponseBody(response, writer, null, null, null);
    }

    public synchronized <T> Single<Void> writeResponseBodyWithRequestStatusObservable(
            Observable<Response<T>> response,
            BaseDbWriter<T> writer) {
        return writeResponseBodyWithRequestStatusObservable(response, writer, null, null, null);
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

    public synchronized <T> void writeResponseBody(
            Observable<Response<T>> response,
            BaseDbWriter<T> writer,
            String dbTable,
            String sqlWhere,
            String[] whereArgs) {
        response.subscribe(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(dbTable, sqlWhere, whereArgs, data);
            }
        }, error -> {
            Log.d(Constants.LOG_TAG, "writeResponseBody error received");
            error.printStackTrace();
        });
    }

    public synchronized <T> Single<Void> writeResponseBodyWithRequestStatusObservable(
            Observable<Response<T>> response,
            BaseDbWriter<T> writer,
            String dbTable,
            String sqlWhere,
            String[] whereArgs) {
        BehaviorSubject<Void> subject = BehaviorSubject.create();
        response.subscribe(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(dbTable, sqlWhere, whereArgs, data);
            }
            subject.onNext(null);
            subject.onCompleted();
        }, error -> {
            Log.d(Constants.LOG_TAG, "writeResponseBody error received");
            error.printStackTrace();
            subject.onError(error);
        });
        return subject.toSingle();
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

    public synchronized <T, W> void writeMappedResponseBody(
            Observable<Response<T>> response,
            Func1<T, W> writerMap,
            BaseDbWriter<W> writer) {
        writeMappedResponseBody(response, writerMap, writer, null, null, null);
    }

    public synchronized <T, W> Single<Void> writeMappedResponseBodyWithRequestStatusObservable(
            Observable<Response<T>> response,
            Func1<T, W> writerMap,
            BaseDbWriter<W> writer) {
        return writeMappedResponseBodyWithRequestStatusObservable(response, writerMap, writer, null, null, null);
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

    public synchronized <T, W> void writeMappedResponseBody(
            Observable<Response<T>> response,
            Func1<T, W> writerMap,
            BaseDbWriter<W> writer,
            String dbTable,
            String sqlWhere,
            String[] whereArgs) {
        response.subscribe(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(dbTable, sqlWhere, whereArgs, writerMap.call(data));
            }
        }, error -> {
            Log.d(Constants.LOG_TAG, "writeMappedResponseBody error received");
            error.printStackTrace();
        });
    }

    public synchronized <T, W> Single<Void> writeMappedResponseBodyWithRequestStatusObservable(
            Observable<Response<T>> response,
            Func1<T, W> writerMap,
            BaseDbWriter<W> writer,
            String dbTable,
            String sqlWhere,
            String[] whereArgs) {
        BehaviorSubject<Void> subject = BehaviorSubject.create();
        response.subscribeOn(Schedulers.io()).subscribe(response1 -> {
            T data = response1.body();
            if (shouldWriteData(response1)) {
                writer.call(dbTable, sqlWhere, whereArgs, writerMap.call(data));
            }
            subject.onNext(null);
            subject.onCompleted();
        }, error -> {
            Log.d(Constants.LOG_TAG, "writeMappedResponseBody error received");
            error.printStackTrace();
            subject.onError(error);
        });
        return subject.toSingle();
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
        okhttp3.Response cacheResponse = response.raw().cacheResponse();
        return cacheResponse == null || cacheResponse.code() == 504;
    }
}
