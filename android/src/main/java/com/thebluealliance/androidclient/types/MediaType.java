package com.thebluealliance.androidclient.types;

public enum MediaType {
    NONE,
    YOUTUBE,
    CD_PHOTO_THREAD;

    public static MediaType fromString(String string) {
        switch (string) {
            case "cdphotothread":
                return CD_PHOTO_THREAD;
            case "youtube":
                return YOUTUBE;
            default:
                return NONE;
        }
    }

    public String toString() {
        switch (this) {
            case NONE:
                return "";
            case YOUTUBE:
                return "youtube";
            case CD_PHOTO_THREAD:
                return "cdphotothread";
        }
        return "";
    }
}
