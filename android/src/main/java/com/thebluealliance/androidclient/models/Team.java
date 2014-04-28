package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;

public class Team extends SimpleTeam {
	String	fullName,
			website;
	JsonArray events;
	
	public Team() {
		super();
		this.fullName = "";
		this.website = "";
		this.events = new JsonArray();
	}
	public Team(String teamKey, int teamNumber, String fullName, String nickname, String location, String website, JsonArray events, long last_updated) {
		super(teamKey,teamNumber,nickname,location,last_updated);
		this.fullName = fullName;
		this.events = events;
		this.website = website;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public JsonArray getEvents() {
		return events;
	}
	public void setEvents(JsonArray events) {
		this.events = events;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
		
}
