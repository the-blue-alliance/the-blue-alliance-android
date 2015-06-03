package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.thebluealliance.androidclient.Constants;

import java.io.IOException;
import java.util.Map;


public class HTTP {

    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
            client.networkInterceptors().add(new StethoInterceptor());
        }
        return client;
    }

    public static Response getRequest(String url) {
        return getRequest(url, null, null);
    }

    public static Response getRequest(String url, String lastUpdated) {
        return getRequest(url, lastUpdated, null);
    }

    public static Response getRequest(String url, String lastUpdated, Map<String, String> headers) {

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("X-TBA-App-Id", Constants.getApiHeader());
        if (lastUpdated != null) {
            requestBuilder.addHeader("If-Modified-Since", lastUpdated);
        }
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }

        Request request = requestBuilder.build();
        try {
            return getClient().newCall(request).execute();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Exception while fetching " + url);
            e.printStackTrace();
            return null;
        }
    }

    public static String GET(String url) {

        return GET(url, null);
    }

    public static String GET(String url, Map<String, String> headers) {

        Response response = getRequest(url, null, headers);
        if (response == null) return null;

        return dataFromResponse(response);
    }

    public static String dataFromResponse(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            Log.w(Constants.LOG_TAG, "Exception while fetching data from " + response.toString());
            e.printStackTrace();
            return null;
        }
    }

}