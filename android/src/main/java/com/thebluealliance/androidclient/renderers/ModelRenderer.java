package com.thebluealliance.androidclient.renderers;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.ListItem;

public interface ModelRenderer<MODEL, ARGS> {

    /**
     * Render a MyTBA-Style model (simplified, with key/value info)
     * TODO eventually move this to rendering regular items
     * @param key Key of the model to render
     * @param type Enum type of which model to render
     * @return A ListItem of the rendered model
     */
    @WorkerThread
    @Nullable ListItem renderFromKey(String key, ModelType type);

    @WorkerThread
    @Nullable ListItem renderFromModel(MODEL model, ARGS args);
}
