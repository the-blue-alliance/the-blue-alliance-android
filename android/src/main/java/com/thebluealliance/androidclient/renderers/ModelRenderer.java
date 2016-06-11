package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.types.ModelType;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public interface ModelRenderer<MODEL, ARGS> {

    /**
     * Render a MyTBA-Style model (simplified, with key/value info)
     * TODO eventually move this to rendering regular items
     * @param key Key of the model to render
     * @param type Enum type of which model to render
     * @param args
     * @return A ListItem of the rendered model
     */
    @WorkerThread
    @Nullable ListElement renderFromKey(String key, ModelType type, ARGS args);

    @WorkerThread
    @Nullable ListElement renderFromModel(MODEL model, @Nullable ARGS args);
}
