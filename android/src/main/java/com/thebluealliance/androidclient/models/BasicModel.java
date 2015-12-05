package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.ModelRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

public abstract class BasicModel<T extends BasicModel> implements RenderableModel {

    /* Map of the requested fields for this object
     * This is done for two reasons - since different parts of the model are loaded from different API queries,
     * we aren't necessarily going to have every bit of data for the model.
     * Also, we can now only request the parts of the model that we want to use
     */
    protected ContentValues fields;
    protected ModelType type;

    //database table that holds this model's information
    private String table;

    public BasicModel(String table, ModelType type) {
        this.table = table;
        this.type = type;
        fields = new ContentValues();
    }

    public void merge(T in) {
        if (in != null) {
            fields.putAll(in.fields);
        }
    }

    public String getTable(){
        return table;
    }

    public ContentValues getParams() {
        return fields;
    }

    public abstract String getKey();

    @Override
    @SuppressWarnings("unchecked")
    public ListElement render(ModelRendererSupplier supplier) {
        ModelRenderer<T, ?> renderer = supplier.getRendererForType(type);
        return renderer != null ? renderer.renderFromModel((T)this, null) : null;
    }

    /*
     * When we're ready for it, I can foresee wanting easy inflating/deflating with json. Uncomment whenever that is...
    public JsonObject toJson();
    public static BasicModel fromJson(Json Object in);
     */

    public static class FieldNotDefinedException extends Exception {
        public FieldNotDefinedException(String message) {
            super(message);
        }

        public FieldNotDefinedException(String message, Throwable t) {
            super(message, t);
        }
    }
}
