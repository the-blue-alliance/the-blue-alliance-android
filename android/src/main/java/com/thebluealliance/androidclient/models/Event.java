package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;


public class Event extends SimpleEvent{
	
	String 		website;
	JsonArray 	matches,
				rankings,
				webcasts;
	JsonObject	stats;
	
	public Event() {
		super();
		website = "";
		matches = new JsonArray();
		rankings = new JsonArray();
		webcasts = new JsonArray();
		stats = new JsonObject();
	}
	
	public Event(String eventKey, String eventName, String location, boolean official, TYPE eventType, DISTRICT eventDistrict, Date startDate, Date endDate, 
				 String website, JsonArray matches, JsonArray rankings, JsonArray webcasts, JsonObject stats, long last_updated) {
		super(eventKey,eventName,location,official,eventType,eventDistrict,startDate,endDate,last_updated);
		this.website = website;
		this.matches = matches;
		this.rankings = rankings;
		this.webcasts = webcasts;
		this.stats = stats;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public JsonArray getMatches() {
		return matches;
	}

	public void setMatches(JsonArray matches) {
		this.matches = matches;
	}

	public JsonArray getRankings() {
		return rankings;
	}

	public void setRankings(JsonArray rankings) {
		this.rankings = rankings;
	}

	public JsonArray getWebcasts() {
		return webcasts;
	}

	public void setWebcasts(JsonArray webcasts) {
		this.webcasts = webcasts;
	}

	public JsonObject getStats() {
		return stats;
	}

	public void setStats(JsonObject stats) {
		this.stats = stats;
	}

}
