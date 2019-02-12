package com.thebluealliance.androidclient.types;

public enum MediaType {
    NONE,
    YOUTUBE,
    CD_PHOTO_THREAD,
    FACEBOOK_PROFILE,
    TWITTER_PROFILE,
    YOUTUBE_CHANNEL,
    GITHUB_PROFILE,
    INSTAGRAM_PROFILE,
    IMGUR;

    public static MediaType fromString(String string) {
        if (string == null) return NONE;
        switch (string) {
            case "cdphotothread":
                return CD_PHOTO_THREAD;
            case "youtube":
                return YOUTUBE;
            case "imgur":
                return IMGUR;
            case "facebook-profile":
                return FACEBOOK_PROFILE;
            case "youtube-channel":
                return YOUTUBE_CHANNEL;
            case "twitter-profile":
                return TWITTER_PROFILE;
            case "github-profile":
                return GITHUB_PROFILE;
            case "instagram-profile":
                return INSTAGRAM_PROFILE;
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

    public boolean isImage() {
        return this != NONE && this != YOUTUBE;
    }

    public boolean isVideo() {
        return this != NONE && this == YOUTUBE;
    }
}
