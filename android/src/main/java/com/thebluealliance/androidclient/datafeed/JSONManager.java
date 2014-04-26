package com.thebluealliance.androidclient.datafeed;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * File created by phil on 4/22/14.
 */
public class JSONManager {
    private static Gson gson;
    private static JsonParser parser;

    public static JsonArray getasJsonArray(String input){
        if(parser == null)
            parser = new JsonParser();
        if(input == null || input.equals(""))
            return new JsonArray();
        return parser.parse(input).getAsJsonArray();
    }
}
