package com.thebluealliance.androidclient.models;

import android.content.Context;

import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 * Created by phil on 7/23/14.
 */
public class District extends BasicModel<District> {

    public District(){
        super(Database.TABLE_DISTRICTS);
    }

    public void setKey(String key){
        fields.put(Database.Districts.KEY, key);
    }

    public String getKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.Districts.KEY) && fields.get(Database.Districts.KEY) instanceof String) {
            return (String) fields.get(Database.Districts.KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.Districts.KEY is not defined");
        }
    }

    public void setAbbreviation(String abbrev){
        fields.put(Database.Districts.ABBREV, abbrev);
    }

    public String getAbbreviation() throws FieldNotDefinedException{
        if (fields.containsKey(Database.Districts.ABBREV) && fields.get(Database.Districts.ABBREV) instanceof String) {
            return (String) fields.get(Database.Districts.ABBREV);
        }else {
            throw new FieldNotDefinedException("Field Database.Districts.ABBREV is not defined");
        }
    }

    public void setEnum(int districtEnum){
        fields.put(Database.Districts.ENUM, districtEnum);
    }

    public int getEnum() throws FieldNotDefinedException{
        if (fields.containsKey(Database.Districts.ENUM) && fields.get(Database.Districts.ENUM) instanceof Integer) {
            return (Integer) fields.get(Database.Districts.ENUM);
        }else {
            throw new FieldNotDefinedException("Field Database.Districts.ENUM is not defined");
        }
    }

    public void setYear(int year){
        fields.put(Database.Districts.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(Database.Districts.YEAR) && fields.get(Database.Districts.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.Districts.YEAR);
        }else {
            throw new FieldNotDefinedException("Field Database.Districts.YEAR is not defined");
        }
    }

    @Override
    public void write(Context c) {

    }

    @Override
    public ListElement render() {
        return null;
    }
}
