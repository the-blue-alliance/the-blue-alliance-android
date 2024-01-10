package com.thebluealliance.androidclient.renderers;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.types.MediaType;
import com.thebluealliance.androidclient.types.ModelType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MediaRenderer implements ModelRenderer<Media, Void> {

    private static final Pattern YOUTUBE_KEY_PATTERN = Pattern.compile("^([a-zA-Z0-9_-]*)");

    private final Picasso mPicasso;

    @Inject
    public MediaRenderer(Picasso picasso) {
        mPicasso = picasso;
    }

    @Override
    public @Nullable ListElement renderFromKey(String key, ModelType type, Void args) {
        /* Not implemented yet */
        return null;
    }

    @Override
    public @Nullable ImageListElement renderFromModel(Media media, Void aVoid) {
        String imageUrl;
        MediaType mediaType = MediaType.fromString(media.getType());
        String foreignKey = media.getForeignKey();
        String keyForUrl = foreignKey;

            /* Build the link of the remote image based on foreign key */
        switch (mediaType) {
            case CD_PHOTO_THREAD:
                JsonObject details = media.getDetailsJson();
                imageUrl = String.format(mediaType.getImageUrlPattern(),
                                         details.get("image_partial").getAsString()
                                                .replace("_l.jpg", "_m.jpg"));
                break;
            case YOUTUBE:
                    /* Need to account for timestamps in youtube foreign key
                     * Can be like <key>?start=1h15m3s or <key>?t=time or <key>#t=time
                     * Since foreign key is first param in yt.com/watch?v=blah, others need to be &
                     */
                keyForUrl = foreignKey.replace('?', '&').replace('#', '&');
                Matcher m = YOUTUBE_KEY_PATTERN.matcher(foreignKey);
                String cleanKey = m.find() ? m.group(1) : foreignKey;
                imageUrl = String.format(mediaType.getImageUrlPattern(), cleanKey);
                break;
            case IMGUR:
                imageUrl = String.format(mediaType.getImageUrlPattern(), foreignKey);
                break;
            default:
                imageUrl = "";
        }
        Boolean isVideo = mediaType == MediaType.YOUTUBE;
        String linkUrl = String.format(mediaType.getLinkUrlPattern(), keyForUrl);
        return new ImageListElement(mPicasso, imageUrl, linkUrl, isVideo);
    }
}
