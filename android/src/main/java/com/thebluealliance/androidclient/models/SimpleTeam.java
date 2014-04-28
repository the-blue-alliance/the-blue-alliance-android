package com.thebluealliance.androidclient.models;

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
}
