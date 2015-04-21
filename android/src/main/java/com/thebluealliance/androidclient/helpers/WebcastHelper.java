package com.thebluealliance.androidclient.helpers;

import android.content.Context;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;

/**
 * Created by phil on 3/27/15.
 */
public class WebcastHelper {

    public static enum TYPE {
        YOUTUBE,
        TWITCH,
        USTREAM,
        LIVESTREAM,
        IFRAME,
        NONE;

        public String render(Context context){
            switch(this){
                case YOUTUBE: return context.getString(R.string.webcast_type_youtube);
                case TWITCH: return context.getString(R.string.webcast_type_twitch);
                case USTREAM: return context.getString(R.string.webcast_type_ustream);
                case LIVESTREAM: return context.getString(R.string.webcast_type_livestream);
                case IFRAME: return context.getString(R.string.webcast_type_gameday); // watch on web GameDay
                default: return "";
            }
        }
    }

    public static TYPE getType(String typeString){
        switch(typeString){
            case "youtube":     return TYPE.YOUTUBE;
            case "twitch":      return TYPE.TWITCH;
            case "ustream":     return TYPE.USTREAM;
            case "livestream":  return TYPE.LIVESTREAM;
            case "iframe":      return TYPE.IFRAME;
            default:            return TYPE.NONE;
        }
    }

    public static String getUrlForWebcast(Context context, String eventKey, TYPE type, JsonObject params, int number){
        switch(type){
            case YOUTUBE:
                return context.getString(R.string.webcast_youtube_embed_pattern, params.get("channel").getAsString());
            case TWITCH:
                return context.getString(R.string.webcast_twitch_embed_pattern, params.get("channel").getAsString());
            case USTREAM:
                return context.getString(R.string.webcast_ustream_embed_pattern, params.get("channel").getAsString());
            case LIVESTREAM:
                return context.getString(R.string.webcast_livestream_embed_pattern, params.get("channel").getAsString(), params.get("file").getAsString());
            case IFRAME:
                return context.getString(R.string.webcast_gameday_pattern, eventKey, number);
            default:
                return "";
        }
    }

}
