package com.thebluealliance.androidclient.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;

/**
 * Created by phil on 3/27/15.
 */
public class WebcastHelper {

    public enum TYPE {
        YOUTUBE,
        TWITCH,
        USTREAM,
        LIVESTREAM,
        IFRAME,
        HTML5,
        STEMTV,
        NONE;

        public String render(Context context) {
            switch (this) {
                case YOUTUBE:
                    return context.getString(R.string.webcast_type_youtube);
                case TWITCH:
                    return context.getString(R.string.webcast_type_twitch);
                case USTREAM:
                    return context.getString(R.string.webcast_type_ustream);
                case LIVESTREAM:
                    return context.getString(R.string.webcast_type_livestream);
                case IFRAME:
                    return context.getString(R.string.webcast_type_gameday); // watch on web GameDay
                case HTML5:
                    return context.getString(R.string.webcast_type_html5);
                case STEMTV:
                    return context.getString(R.string.webcast_type_stemtv);
                default:
                    return "";
            }
        }
    }

    public static TYPE getType(String typeString) {
        switch (typeString) {
            case "youtube":
                return TYPE.YOUTUBE;
            case "twitch":
                return TYPE.TWITCH;
            case "ustream":
                return TYPE.USTREAM;
            case "livestream":
                return TYPE.LIVESTREAM;
            case "iframe":
                return TYPE.IFRAME;
            case "html5":
                return TYPE.HTML5;
            case "stemtv":
                return TYPE.STEMTV;
            default:
                return TYPE.NONE;
        }
    }

    public static Intent getIntentForWebcast(Context context, String eventKey, TYPE type, JsonObject params, int number) {
        switch (type) {
            case YOUTUBE:
                return getWebIntentForUrl(context.getString(R.string.webcast_youtube_embed_pattern, params.get("channel").getAsString()));
            case TWITCH:
                return getWebIntentForUrl(context.getString(R.string.webcast_twitch_embed_pattern, params.get("channel").getAsString()));
            case USTREAM:
                return getWebIntentForUrl(context.getString(R.string.webcast_ustream_embed_pattern, params.get("channel").getAsString()));
            case LIVESTREAM:
                return getWebIntentForUrl(context.getString(R.string.webcast_livestream_embed_pattern, params.get("channel").getAsString(), params.get("file").getAsString()));
            case HTML5:
                return new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(params.get("channel").getAsString()), "video/*");
            case STEMTV:
                return new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(context.getString(R.string.webcast_stemtv_embed_pattern, params.get("channel").getAsString())), "video/*");
            default:
            case IFRAME:
                return getWebIntentForUrl(context.getString(R.string.webcast_gameday_pattern, eventKey, number));
        }
    }

    private static Intent getWebIntentForUrl(String url) {
        return new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
    }

}
