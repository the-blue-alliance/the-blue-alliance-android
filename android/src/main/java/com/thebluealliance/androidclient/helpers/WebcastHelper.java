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
        NONE;

        public String render(Context context){
            switch(this){
                case YOUTUBE: return context.getString(R.string.webcast_type_youtube);
                case TWITCH: return context.getString(R.string.webcast_type_twitch);
                case USTREAM: return context.getString(R.string.webcast_type_ustream);
                case LIVESTREAM: return context.getString(R.string.webcast_type_livestream);
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
            default:            return TYPE.NONE;
        }
    }

    public static String getUrlForWebcast(Context context, TYPE type, JsonObject params){
        switch(type){
            case YOUTUBE:
                return String.format(context.getString(R.string.webcast_youtube_embed_pattern), params.get("channel").getAsString());
            case TWITCH:
                return String.format(context.getString(R.string.webcast_twitch_embed_pattern), params.get("channel").getAsString());
            case USTREAM:
                return String.format(context.getString(R.string.webcast_ustream_embed_pattern), params.get("channel").getAsString());
            case LIVESTREAM:
                return String.format(context.getString(R.string.webcast_livestream_embed_pattern), params.get("channel").getAsString(), params.get("file").getAsString());
            default:
                return "";
        }
    }

}
