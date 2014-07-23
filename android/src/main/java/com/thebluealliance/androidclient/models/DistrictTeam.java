package com.thebluealliance.androidclient.models;

import android.content.Context;

import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 * Created by phil on 7/23/14.
 */
public class DistrictTeam extends BasicModel<DistrictTeam> {

    public DistrictTeam(){
        super(Database.TABLE_DISTRICTTEAMS);
    }

    public void setKey(String key){
        fields.put(Database.DistrictTeams.KEY, key);
    }

    public String getKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.KEY) && fields.get(Database.DistrictTeams.KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.KEY is not defined");
        }
    }

    public void setTeamKey(String key){
        fields.put(Database.DistrictTeams.TEAM_KEY, key);
    }

    public String getTeamKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.TEAM_KEY) && fields.get(Database.DistrictTeams.TEAM_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.TEAM_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.TEAM_KEY is not defined");
        }
    }

    public void setDistrictEnum(int districtEnum){
        fields.put(Database.DistrictTeams.DISTRICT_ENUM, districtEnum);
    }

    public int getDistrictEnum() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.DISTRICT_ENUM) && fields.get(Database.DistrictTeams.DISTRICT_ENUM) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.DISTRICT_ENUM);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.DISTRICT_ENUM is not defined");
        }
    }

    public void setYear(int year){
        fields.put(Database.DistrictTeams.YEAR, year);
    }

    public int getYear() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.YEAR) && fields.get(Database.DistrictTeams.YEAR) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.YEAR);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.YEAR is not defined");
        }
    }

    public void setRank(int year){
        fields.put(Database.DistrictTeams.RANK, year);
    }

    public int getRank() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.RANK) && fields.get(Database.DistrictTeams.RANK) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.RANK);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.RANK is not defined");
        }
    }

    public void setEvent1Key(String key){
        fields.put(Database.DistrictTeams.EVENT1_KEY, key);
    }

    public String getEvent1Key() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.EVENT1_KEY) && fields.get(Database.DistrictTeams.EVENT1_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.EVENT1_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_KEY is not defined");
        }
    }

    public void setEvent1Points(int points){
        fields.put(Database.DistrictTeams.EVENT1_POINTS, points);
    }

    public int getEvent1Points() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.EVENT1_POINTS) && fields.get(Database.DistrictTeams.EVENT1_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.EVENT1_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_POINTS is not defined");
        }
    }

    public void setEvent2Key(String key){
        fields.put(Database.DistrictTeams.EVENT2_KEY, key);
    }

    public String getEvent2Key() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.EVENT2_KEY) && fields.get(Database.DistrictTeams.EVENT2_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.EVENT2_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT2_KEY is not defined");
        }
    }

    public void setEvent2Points(int points){
        fields.put(Database.DistrictTeams.EVENT2_POINTS, points);
    }

    public int getEvent2Points() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.EVENT2_POINTS) && fields.get(Database.DistrictTeams.EVENT2_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.EVENT2_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.EVENT1_POINTS is not defined");
        }
    }

    public void setCmpKey(String key){
        fields.put(Database.DistrictTeams.CMP_KEY, key);
    }

    public String getCmpKey() throws FieldNotDefinedException{
        if (fields.containsKey(Database.DistrictTeams.CMP_KEY) && fields.get(Database.DistrictTeams.CMP_KEY) instanceof String) {
            return (String) fields.get(Database.DistrictTeams.CMP_KEY);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_KEY is not defined");
        }
    }

    public void setCmpPoints(int points){
        fields.put(Database.DistrictTeams.CMP_POINTS, points);
    }

    public int getCmpPoints() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.CMP_POINTS) && fields.get(Database.DistrictTeams.CMP_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.CMP_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_POINTS is not defined");
        }
    }

    public void setRookiePoints(int points){
        fields.put(Database.DistrictTeams.ROOKIE_POINTS, points);
    }

    public int getRookiePoints() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.ROOKIE_POINTS) && fields.get(Database.DistrictTeams.ROOKIE_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.ROOKIE_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.CMP_POINTS is not defined");
        }
    }

    public void setTotalPoints(int points){
        fields.put(Database.DistrictTeams.TOTAL_POINTS, points);
    }

    public int getTotalPoints() throws FieldNotDefinedException {
        if (fields.containsKey(Database.DistrictTeams.TOTAL_POINTS) && fields.get(Database.DistrictTeams.TOTAL_POINTS) instanceof Integer) {
            return (Integer) fields.get(Database.DistrictTeams.TOTAL_POINTS);
        }else {
            throw new FieldNotDefinedException("Field Database.DistrictTeams.TOTAL_POINTS is not defined");
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
