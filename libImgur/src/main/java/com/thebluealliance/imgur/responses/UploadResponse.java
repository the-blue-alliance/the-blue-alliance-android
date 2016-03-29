package com.thebluealliance.imgur.responses;

/**
 * The model that imgur returns after uploading an image
 */
public class UploadResponse {

    public boolean success;
    public int status;
    public ImageData data;

    public static class ImageData {
        public String id;
        public String title;
        public String description;
        public int datetime;
        public String type;
        public boolean animated;
        public int width;
        public int height;
        public int size;
        public int views;
        public int bandwidth;
        public String vote;
        public boolean favorite;
        public String nsfw;
        public String section;
        public String account_url;
        public int account_id;
        public String comment_preview;
        public String deletehash;
        public String name;
        public String link;
    }
}
