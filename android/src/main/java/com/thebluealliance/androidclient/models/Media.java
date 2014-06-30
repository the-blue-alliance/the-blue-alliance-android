package com.thebluealliance.androidclient.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.ModelInflater;
import com.thebluealliance.androidclient.listitems.ImageListElement;
import com.thebluealliance.androidclient.listitems.ListElement;

import java.util.ArrayList;


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

        public String toString(){
            switch(this){
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

    public Media() {
        super(Database.TABLE_MEDIAS);
    }

    public Media.TYPE getMediaType() throws FieldNotDefinedException {
        if(fields.containsKey(Database.Medias.TYPE) && fields.get(Database.Medias.TYPE) instanceof String) {
            return TYPE.fromString((String) fields.get(Database.Medias.TYPE));
        }
        throw new FieldNotDefinedException("Field Database.Medias.TYPE is not defined");
    }

    public void setMediaType(String typeString) {
        fields.put(Database.Medias.TYPE, typeString);
    }

    public void setMediaType(Media.TYPE mediaType) {
        fields.put(Database.Medias.TYPE, mediaType.toString());
    }

    public String getForeignKey() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Medias.FOREIGNKEY) && fields.get(Database.Medias.FOREIGNKEY) instanceof String) {
            return (String) fields.get(Database.Medias.FOREIGNKEY);
        }
        throw new FieldNotDefinedException("Field Database.Medias.FOREIGNKEY is not defined");
    }

    public void setForeignKey(String foreignKey) {
        fields.put(Database.Medias.FOREIGNKEY, foreignKey);
    }

    public String getTeamKey() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Medias.TEAMKEY) && fields.get(Database.Medias.TEAMKEY) instanceof String) {
            return (String) fields.get(Database.Medias.TEAMKEY);
        }
        throw new FieldNotDefinedException("Field Database.MEDIAS.TEAMKEY is not defined");
    }

    public void setTeamKey(String teamKey) {
        fields.put(Database.Medias.TEAMKEY, teamKey);
    }

    public JsonObject getDetails() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Medias.DETAILS) && fields.get(Database.Medias.DETAILS) instanceof String) {
            return JSONManager.getasJsonObject((String) fields.get(Database.Medias.DETAILS));
        }
        throw new FieldNotDefinedException("Field Database.Medias.TEAMKEY is not defined");
    }

    public void setDetails(JsonObject details){
        fields.put(Database.Medias.DETAILS, details.toString());
    }

    public int getYear() throws FieldNotDefinedException{
        if(fields.containsKey(Database.Medias.YEAR) && fields.get(Database.Medias.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.Medias.DETAILS);
        }
        throw new FieldNotDefinedException("Field Database.Medias.YEAR is not defined");
    }

    public void setYear(int year) {
        fields.put(Database.Medias.YEAR, year);
    }

    @Override
    public ListElement render() {
        String imageUrl;
        try {
            TYPE mediaType = getMediaType();
            JsonObject details = getDetails();
            String foreignKey = getForeignKey();
            if (mediaType == TYPE.CD_PHOTO_THREAD) {
                imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType), details.get("image_partial").getAsString().replace("_l.jpg", "_m.jpg"));
            } else {
                imageUrl = String.format(Constants.MEDIA_IMG_URL_PATTERN.get(mediaType), foreignKey);
            }
            return new ImageListElement(imageUrl,
                    String.format(Constants.MEDIA_LINK_URL_PATTERN.get(mediaType), foreignKey));
        } catch (FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Required fields not defined for rendering. \n" +
                    "Fields Required: Database.Medias.TYPE, Database.Medias.DETAILS, Database.Medias.FOREIGNKEY");
            return null;
        }
    }

    public static APIResponse<Media> query(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_MEDIAS, fields, whereClause, whereArgs, null, null, null, null);
        Media media;
        if(cursor != null && cursor.moveToFirst()){
            media = ModelInflater.inflateMedia(cursor);
        }else{
            media = new Media();
        }

        APIResponse.CODE code = APIResponse.CODE.CACHED304;
        boolean changed = false;
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                Media updatedMedia = JSONManager.getGson().fromJson(response.getData(), Media.class);
                media.merge(updatedMedia);
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if(changed){
            media.write(c);
        }
        return new APIResponse<>(media, code);
    }

    public static APIResponse<ArrayList<Media>> queryList(Context c, boolean forceFromCache, String[] fields, String whereClause, String[] whereArgs, String[] apiUrls) throws DataManager.NoDataException {
        Cursor cursor = Database.getInstance(c).safeQuery(Database.TABLE_MEDIAS, fields, whereClause, whereArgs, null, null, null, null);
        ArrayList<Media> medias = new ArrayList<>();
        if(cursor != null && cursor.moveToFirst()){
            do{
                medias.add(ModelInflater.inflateMedia(cursor));
            }while(cursor.moveToNext());
        }

        APIResponse.CODE code = APIResponse.CODE.CACHED304;
        boolean changed = false;
        for(String url: apiUrls) {
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, url, forceFromCache);
            if (response.getCode() == APIResponse.CODE.WEBLOAD || response.getCode() == APIResponse.CODE.UPDATED) {
                JsonArray matchList = JSONManager.getasJsonArray(response.getData());
                medias = new ArrayList<>();
                for(JsonElement m: matchList){
                    medias.add(JSONManager.getGson().fromJson(m, Media.class));
                }
                changed = true;
            }
            code = APIResponse.mergeCodes(code, response.getCode());
        }

        if(changed){
            Database.getInstance(c).getMediasTable().add(medias);
        }
        return new APIResponse<>(medias, code);
    }

    @Override
    public void write(Context c) {
        Database.getInstance(c).getMediasTable().add(this);
    }
}
