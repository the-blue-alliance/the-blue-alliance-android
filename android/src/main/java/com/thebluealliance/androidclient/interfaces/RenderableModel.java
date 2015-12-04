package com.thebluealliance.androidclient.interfaces;

import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

public interface RenderableModel<T> {
    ListElement render(ModelRendererSupplier rendererSupplier);
}
