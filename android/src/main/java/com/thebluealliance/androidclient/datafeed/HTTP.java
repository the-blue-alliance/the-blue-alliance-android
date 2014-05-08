package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.BuildConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class HTTP {
    public static String GET(String url) {

        InputStream is = null;
        String result = "";

        // HTTP
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("X-TBA-App-Id", "the-blue-alliance:android:v"+BuildConfig.VERSION_NAME);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
