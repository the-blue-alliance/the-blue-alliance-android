package com.thebluealliance.androidclient.types;

public enum MediaType {
    NONE,
    YOUTUBE,
    CD_PHOTO_THREAD,
    IMGUR;

    public static MediaType fromString(String string) {
        switch (string) {
            case "cdphotothread":
                return CD_PHOTO_THREAD;
            case "youtube":
                return YOUTUBE;
            case "imgur":
                return IMGUR;
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
            case IMGUR:
                return "imgur";
        }
        return "";
    }

    public String getImageUrlPattern() {
        switch (this) {
            case CD_PHOTO_THREAD:
                return "http://www.chiefdelphi.com/media/img/%s";
            case YOUTUBE:
                return "http://img.youtube.com/vi/%s/hqdefault.jpg";
            case IMGUR:
                return "http://i.imgur.com/%s.jpg";
            default:
                return "";
        }
    }

    public String getLinkUrlPattern() {
        switch (this) {
            case CD_PHOTO_THREAD:
                return "http://www.chiefdelphi.com/media/photos/%s";
            case YOUTUBE:
                return "https://www.youtube.com/watch?v=%s";
            case IMGUR:
                return "http://imgur.com/%s";
            default:
                return "";
        }
    }

    public boolean isImage() {
        return this != NONE && this != YOUTUBE;
    }

    public boolean isVideo() {
        return this != NONE && this == YOUTUBE;
    }
}
