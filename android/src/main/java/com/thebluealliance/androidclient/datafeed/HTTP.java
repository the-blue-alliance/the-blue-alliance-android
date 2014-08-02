package com.thebluealliance.androidclient.datafeed;

import android.util.Log;

import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


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
            Log.w(Constants.LOG_TAG, "Exception while fetching " + url);
            e.printStackTrace();
            return null;
        }
    }

    public static HttpResponse postResponse(String uri, Map<String, String> headers, JsonElement data){
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(data.toString()));
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");
            for(Map.Entry<String, String> header: headers.entrySet()){
                httpPost.setHeader(header.getKey(), header.getValue());
            }
            return new DefaultHttpClient().execute(httpPost);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error making POST request");
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(String uri, Map<String, String> headers, JsonElement data){
        HttpResponse response = postResponse(uri, headers, data);
        if (response == null) return null;

        return dataFromResponse(response);
    }

    public static String GET(String url) {

        HttpResponse response = getResponse(url);
        if (response == null) return null;

        return dataFromResponse(response);
    }

    public static String GET(String url, Map<String, String> headers){

        // HTTP
        HttpResponse response = null;
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpGet httpget = new HttpGet(url);
            if(headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpget.addHeader(header.getKey(), header.getValue());
                }
            }
            response = httpclient.execute(httpget);
        } catch (Exception e) {
            Log.w(Constants.LOG_TAG, "Exception while fetching " + url);
            e.printStackTrace();
            return null;
        }

        InputStream is;
        String result = "";
        // Read response to string
        if(response != null) {
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
                return result;
            } catch (Exception e) {
                Log.w(Constants.LOG_TAG, "Exception while fetching data from " + response.toString());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static String dataFromResponse(HttpResponse response) {
        InputStream is;
        String result = "";
        // Read response to string
        if(response != null) {
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
                return result;
            } catch (Exception e) {
                Log.w(Constants.LOG_TAG, "Exception while fetching data from " + response.toString());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}