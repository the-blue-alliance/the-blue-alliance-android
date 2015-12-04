package com.thebluealliance.androidclient.renderers;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.helpers.MediaType;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Media;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MediaRenderer implements ModelRenderer<Media, Void> {

    @Inject
    public MediaRenderer() {

    }

    @Override
    public @Nullable ListElement renderFromKey(String key, ModelType type, Void args) {
        /* Not implemented yet */
        return null;
    }

    @Override
    public @Nullable ImageListElement renderFromModel(Media media, Void aVoid) {
        String imageUrl;
        try {
            MediaType mediaType = media.getMediaType();
            String foreignKey = media.getForeignKey();
            if (mediaType == MediaType.CD_PHOTO_THREAD) {
                JsonObject details = media.getDetails();
                imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType),
                  details.get("image_partial").getAsString().replace("_l.jpg", "_m.jpg"));
            } else if (mediaType == MediaType.YOUTUBE) {
                imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType), foreignKey);
            } else {
                imageUrl = "";
            }
            Boolean isVideo = mediaType == MediaType.YOUTUBE;
            return new ImageListElement(imageUrl,
              String.format(Constants.MEDIA_LINK_URL_PATTERN.get(mediaType), foreignKey), isVideo);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required fields not defined for rendering. \n" +
              "Fields Required: Database.Medias.TYPE, Database.Medias.DETAILS, Database.Medias.FOREIGNKEY");
            return null;
        }
    }
}
