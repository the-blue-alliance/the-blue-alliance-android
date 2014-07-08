package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.thebluealliance.androidclient.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


public class HTTP {

    public static HttpResponse getResponse(String url) {
        return getResponse(url, null);
    }

    public static HttpResponse getResponse(String url, String lastUpdated) {
        // HTTP
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("X-TBA-App-Id", Constants.getApiHeader());
            if (lastUpdated != null) {
                httpget.addHeader("If-Modified-Since", lastUpdated);
            }
            return httpclient.execute(httpget);
        } catch (Exception e) {
            Log.w(Constants.LOG_TAG, "Exception while fetching " + url + ": \n" + Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public static String dataFromResponse(HttpResponse response) {
        InputStream is;
        String result = "";
        // Read response to string
        try {
            HttpEntity entity = response.getEntity();

            is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.w(Constants.LOG_TAG, "Exception while fetching data from " + response.toString() + ": \n" + Arrays.toString(e.getStackTrace()));
            return null;
        }
        return result;
    }

    public static String GET(String url) {

        HttpResponse response = getResponse(url);
        if (response == null) return null;

        return dataFromResponse(response);
    }
}