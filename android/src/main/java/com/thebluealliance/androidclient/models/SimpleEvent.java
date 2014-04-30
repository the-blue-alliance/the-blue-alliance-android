package com.thebluealliance.androidclient.models;

import java.util.Date;


public class SimpleEvent extends Event implements BasicModel{
	
	public SimpleEvent() {
		super();
	}
	
	public SimpleEvent(String eventKey, String eventName, String location, boolean official, TYPE eventType, DISTRICT eventDistrict, Date startDate, Date endDate, long last_updated) {
		super();
        if(!Event.validateEventKey(eventKey)) throw new IllegalArgumentException("Invalid match key. Should be format <year><event>, like 2014cthar");
        this.eventKey = eventKey;
		this.eventName = eventName;
		this.location = location;
		this.eventType = eventType;
		this.eventDistrict = eventDistrict;
		this.startDate = startDate;
		this.endDate = endDate;
		this.official = official;
		this.last_updated = last_updated;
	}
}
