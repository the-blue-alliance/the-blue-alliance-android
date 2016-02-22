package com.thebluealliance.androidclient.renderers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.types.MediaType;
import com.thebluealliance.androidclient.types.ModelType;

import android.support.annotation.Nullable;
import android.util.Log;

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

            /* Build the link of the remote image based on foreign key */
            switch (mediaType) {
                case CD_PHOTO_THREAD:
                    JsonObject details = media.getDetails();
                    imageUrl = String.format(mediaType.getImageUrlPattern(),
                            details.get("image_partial").getAsString().replace("_l.jpg", "_m.jpg"));
                    break;
                case YOUTUBE:
                case IMGUR:
                    imageUrl = String.format(mediaType.getImageUrlPattern(), foreignKey);
                    break;
                default:
                    imageUrl = "";
            }
            Boolean isVideo = mediaType == MediaType.YOUTUBE;
            return new ImageListElement(imageUrl,
              String.format(mediaType.getLinkUrlPattern(), foreignKey), isVideo);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required fields not defined for rendering. \n" +
              "Fields Required: Database.Medias.TYPE, Database.Medias.DETAILS, Database.Medias.FOREIGNKEY");
            return null;
        }
    }
}
