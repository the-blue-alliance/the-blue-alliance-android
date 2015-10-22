package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListElement;


public class Media extends BasicModel<Media> {

    public enum TYPE {
        NONE,
        YOUTUBE,
        CD_PHOTO_THREAD;

        public static TYPE fromString(String string) {
            switch (string) {
                case "cdphotothread":
                    return CD_PHOTO_THREAD;
                case "youtube":
                    return YOUTUBE;
                default:
                    return NONE;
            }
        }

        public String toString() {
            switch (this) {
                case NONE:
                    return "";
                case YOUTUBE:
                    return "youtube";
                case CD_PHOTO_THREAD:
                    return "cdphotothread";
            }
            return "";
        }
    }

    private JsonObject details;

    public Media() {
        super(Database.TABLE_MEDIAS);
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

    public Media.TYPE getMediaType() throws FieldNotDefinedException {
        if (fields.containsKey(MediasTable.TYPE) && fields.get(MediasTable.TYPE) instanceof String) {
            return TYPE.fromString((String) fields.get(MediasTable.TYPE));
        }
        throw new FieldNotDefinedException("Field Database.Medias.TYPE is not defined");
    }

    public void setMediaType(String typeString) {
        fields.put(MediasTable.TYPE, typeString);
    }

    public void setMediaType(Media.TYPE mediaType) {
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

    @Override
    public ListElement render() {
        String imageUrl;
        try {
            TYPE mediaType = getMediaType();
            String foreignKey = getForeignKey();
            if (mediaType == TYPE.CD_PHOTO_THREAD) {
                JsonObject details = getDetails();
                imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType), details.get("image_partial").getAsString().replace("_l.jpg", "_m.jpg"));
            } else if (mediaType == TYPE.YOUTUBE) {
                imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType), foreignKey);
            } else {
                imageUrl = "";
            }
            Boolean isVideo = mediaType == TYPE.YOUTUBE;
            return new ImageListElement(imageUrl,
                    String.format(Constants.MEDIA_LINK_URL_PATTERN.get(mediaType), foreignKey), isVideo);
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required fields not defined for rendering. \n" +
                    "Fields Required: Database.Medias.TYPE, Database.Medias.DETAILS, Database.Medias.FOREIGNKEY");
            return null;
        }
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getMediasTable().add(this);
    }
}
