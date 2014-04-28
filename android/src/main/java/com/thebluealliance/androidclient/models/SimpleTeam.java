package com.thebluealliance.androidclient.models;

public class SimpleTeam {
	String 	teamKey,
			nickname,
			location;
	int teamNumber;
	long last_updated;

	
	public SimpleTeam() {
		this.teamKey = "";
		this.nickname = "";
		this.location = "";
		this.teamNumber = -1;
		this.last_updated = -1;
	}
	public SimpleTeam(String teamKey, int teamNumber, String nickname, String location, long last_updated) {
		this.teamKey = teamKey;
		this.nickname = nickname;
		this.location = location;
		this.teamNumber = teamNumber;
		this.last_updated = last_updated;
	}
	
	public String getTeamKey() {
		return teamKey;
	}
	public void setTeamKey(String teamKey) {
		this.teamKey = teamKey;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getTeamNumber() {
		return teamNumber;
	}
	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}
	public long getLastUpdated() {
		return last_updated;
	}
	public void setLastUpdated(long last_updated) {
		this.last_updated = last_updated;
	}

}
