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
    IMGUR,
    AVATAR;

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
            case "avatar":
                return AVATAR;
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
                return "https://www.chiefdelphi.com/media/img/%s";
            case YOUTUBE:
                return "https://img.youtube.com/vi/%s/hqdefault.jpg";
            case IMGUR:
                return "https://i.imgur.com/%sl.jpg";
            default:
                return "";
        }
    }

    public String getLinkUrlPattern() {
        switch (this) {
            case CD_PHOTO_THREAD:
                return "https://www.chiefdelphi.com/media/photos/%s";
            case YOUTUBE:
                return "https://www.youtube.com/watch?v=%s";
            case IMGUR:
                return "https://imgur.com/%s";
            default:
                return "";
        }
    }

    public boolean isImage() {
        return this != NONE && this != YOUTUBE && this != AVATAR;
    }

    public boolean isVideo() {
        return this != NONE && this == YOUTUBE;
    }

    public boolean isAvatar() {
        return this == AVATAR;
    }
}
