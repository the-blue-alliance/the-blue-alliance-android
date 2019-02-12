package com.thebluealliance.androidclient.renderers;

import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.types.MediaType;
import com.thebluealliance.androidclient.types.ModelType;

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
        MediaType mediaType = MediaType.fromString(media.getType());
        return new ImageListElement(media.getDirectUrl(), media.getViewUrl(), mediaType.isVideo());
    }
}
