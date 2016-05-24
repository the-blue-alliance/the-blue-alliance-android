package com.thebluealliance.androidclient.types;

import com.thebluealliance.androidclient.R;

import android.content.Context;

public enum WebcastType {
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
