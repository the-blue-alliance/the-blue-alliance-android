package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.datafeed.Database;

public class SimpleTeam extends Team implements BasicModel{


	
	public SimpleTeam() {
		this.teamKey = "";
		this.nickname = "";
		this.location = "";
		this.teamNumber = -1;
		this.last_updated = -1;
	}
	public SimpleTeam(String teamKey, int teamNumber, String nickname, String location, long last_updated) {
		super();
        this.teamKey = teamKey;
		this.nickname = nickname;
		this.location = location;
		this.teamNumber = teamNumber;
		this.last_updated = last_updated;
	}

    @Override
    public ContentValues getParams() {
        ContentValues cv = new ContentValues();
        cv.put(Database.Teams.KEY, teamKey);
        cv.put(Database.Teams.NUMBER, teamNumber);
        cv.put(Database.Teams.NAME, fullName);
        cv.put(Database.Teams.SHORTNAME, nickname);
        cv.put(Database.Teams.LOCATION, location);
        return cv;
    }
}
