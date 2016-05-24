package com.thebluealliance.androidclient.models;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.types.MediaType;
import com.thebluealliance.androidclient.types.ModelType;


public class Media extends BasicModel<Media> {

    private JsonObject details;

    public Media() {
        super(Database.TABLE_MEDIAS, ModelType.MEDIA);
        details = null;
    }

    @Override
    public String getKey() {
        try {
            return getForeignKey();
        } catch (FieldNotDefinedException e) {
            return "";
        }
    }

    public MediaType getMediaType() throws FieldNotDefinedException {
        if (fields.containsKey(MediasTable.TYPE) && fields.get(MediasTable.TYPE) instanceof String) {
            return MediaType.fromString((String) fields.get(MediasTable.TYPE));
        }
        throw new FieldNotDefinedException("Field Database.Medias.TYPE is not defined");
    }

    public void setMediaType(String typeString) {
        fields.put(MediasTable.TYPE, typeString);
    }

    public void setMediaType(MediaType mediaType) {
        fields.put(MediasTable.TYPE, mediaType.toString());
    }

    public String getForeignKey() throws FieldNotDefinedException {
        if (fields.containsKey(MediasTable.FOREIGNKEY) && fields.get(MediasTable.FOREIGNKEY) instanceof String) {
            return (String) fields.get(MediasTable.FOREIGNKEY);
        }
        throw new FieldNotDefinedException("Field Database.Medias.FOREIGNKEY is not defined");
    }

    public void setForeignKey(String foreignKey) {
        fields.put(MediasTable.FOREIGNKEY, foreignKey);
    }

    public String getTeamKey() throws FieldNotDefinedException {
        if (fields.containsKey(MediasTable.TEAMKEY) && fields.get(MediasTable.TEAMKEY) instanceof String) {
            return (String) fields.get(MediasTable.TEAMKEY);
        }
        throw new FieldNotDefinedException("Field Database.MEDIAS.TEAMKEY is not defined");
    }

    public void setTeamKey(String teamKey) {
        fields.put(MediasTable.TEAMKEY, teamKey);
    }

    public JsonObject getDetails() throws FieldNotDefinedException {
        if (details != null) {
            return details;
        }
        if (fields.containsKey(MediasTable.DETAILS) && fields.get(MediasTable.DETAILS) instanceof String) {
            details = JSONHelper.getasJsonObject((String) fields.get(MediasTable.DETAILS));
            return details;
        }
        throw new FieldNotDefinedException("Field Database.Medias.TEAMKEY is not defined");
    }

    public void setDetails(JsonObject details) {
        fields.put(MediasTable.DETAILS, details.toString());
        this.details = details;
    }

    public void setDetails(String detailsJson) {
        fields.put(MediasTable.DETAILS, detailsJson);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(MediasTable.YEAR) && fields.get(MediasTable.YEAR) instanceof Integer) {
            return (Integer) fields.get(MediasTable.DETAILS);
        }
        throw new FieldNotDefinedException("Field Database.Medias.YEAR is not defined");
    }

    public void setYear(int year) {
        fields.put(MediasTable.YEAR, year);
    }

}
