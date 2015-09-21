package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.datafeed.LegacyAPIHelper;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListElement;

import java.util.ArrayList;
import java.util.Arrays;


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

    public static APIResponse<Media> query(Context c, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying medias table: " + whereClause + Arrays.toString(whereArgs));
        MediasTable table = Database.getInstance(c).getMediasTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        Media media;
        if (cursor != null && cursor.moveToFirst()) {
            media = table.inflate(cursor);
            cursor.close();
        } else {
            media = new Media();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Media updatedMedia = JSONHelper.getGson().fromJson(response.getData(), Media.class);
                media.merge(updatedMedia);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            media.write(c);
        }
        Log.d(Constants.DATAMANAGER_LOG, "updated in db? " + changed);
        return new APIResponse<>(media, code);
    }

    public static APIResponse<ArrayList<Media>> queryList(Context c, String teamKey, int year, RequestParams requestParams, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Log.d(Constants.DATAMANAGER_LOG, "Querying medias table: " + whereClause + Arrays.toString(whereArgs));
        MediasTable table = Database.getInstance(c).getMediasTable();
        Cursor cursor = table.query(fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<Media> medias = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                medias.add(table.inflate(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        APIResponse.CODE code = requestParams.forceFromCache ? APIResponse.CODE.LOCAL : APIResponse.CODE.CACHED304;
        boolean changed = false;
        for (String url : apiUrls) {
            APIResponse<String> response = LegacyAPIHelper.getResponseFromURLOrThrow(c, url, requestParams);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray mediaList = JSONHelper.getasJsonArray(response.getData());
                medias = new ArrayList<>();
                for (JsonElement m : mediaList) {
                    Media media = JSONHelper.getGson().fromJson(m, Media.class);
                    media.setTeamKey(teamKey);
                    media.setYear(year);
                    medias.add(media);
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if (changed) {
            //Database.getInstance(c).getMediasTable().add(medias);
        }
        Log.d(Constants.DATAMANAGER_LOG, "Found " + medias.size() + " medias, updated in db? " + changed);
        return new APIResponse<>(medias, code);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getMediasTable().add(this);
    }
}
