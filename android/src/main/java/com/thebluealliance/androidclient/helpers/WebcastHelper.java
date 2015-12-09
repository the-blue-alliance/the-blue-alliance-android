package com.thebluealliance.androidclient.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.WebcastType;

public class WebcastHelper {

    public static WebcastType getType(String typeString) {
        switch (typeString) {
            case "youtube":
                return WebcastType.YOUTUBE;
            case "twitch":
                return WebcastType.TWITCH;
            case "ustream":
                return WebcastType.USTREAM;
            case "livestream":
                return WebcastType.LIVESTREAM;
            case "iframe":
                return WebcastType.IFRAME;
            case "html5":
                return WebcastType.HTML5;
            case "stemtv":
                return WebcastType.STEMTV;
            default:
                return WebcastType.NONE;
        }
    }

    public static Intent getIntentForWebcast(Context context, String eventKey, WebcastType type, JsonObject params, int number) {
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

    public static Intent getWebIntentForUrl(String url) {
        return new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
    }

}
