package com.thebluealliance.androidclient.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SimpleEvent {
	
	public static enum TYPE{
		NONE,
		REGIONAL,
		DISTRICT,
		DISTRICT_CMP,
		CMP_DIVISION,
		CMP_FINALS,
		OFFSEASON,
		PRESEASON;
		
		public static TYPE fromString(String str) {
			switch(str) {
			case "Regional":
				return REGIONAL;
			case "District":
				return DISTRICT;
			case "District Championship":
				return DISTRICT_CMP;
			case "Championship Division":
				return CMP_DIVISION;
			case "Championship Finals":
				return CMP_FINALS;
			case "Offseason":
				return OFFSEASON;
			case "Preseason":
				return PRESEASON;
			default:
				return NONE;
			}
		}
		
		public static TYPE fromInt(int num) {
			switch(num) {
			case 0:
				return REGIONAL;
			case 1:
				return DISTRICT;
			case 2:
				return DISTRICT_CMP;
			case 3:
				return CMP_DIVISION;
			case 4:
				return CMP_FINALS;
			case 99:
				return OFFSEASON;
			case 100:
				return PRESEASON;
			default:
				return NONE;
			}
		}
	}
	public static enum DISTRICT{
		NONE,
		FIM,  /* Michigan */
		MAR,  /* Mid Atlantic */
		NE,   /* New England */
		PNW;  /* Pacific Northwest */
		
		public static DISTRICT fromString(String str) {
			/*
			 * Not implemented on TBA yet. Write it here whenever it is...
			 */
			return NONE;
		}
	}
	
	public static final DateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd",java.util.Locale.ENGLISH);
	
	String 		eventKey,
				eventName,
				location;
	TYPE		eventType;
	DISTRICT	eventDistrict;
	Date		startDate,
				endDate;
	boolean		official;
	long		last_updated;
	
	public SimpleEvent() {
		this.eventKey = "";
		this.eventName = "";
		this.location = "";
		this.eventType = TYPE.NONE;
		this.eventDistrict = DISTRICT.NONE;
		this.startDate = new Date(0);
		this.endDate = new Date(0);
		this.official = false;
		this.last_updated = -1;
	}
	
	public SimpleEvent(String eventKey, String eventName, String location, boolean official, TYPE eventType, DISTRICT eventDistrict, Date startDate, Date endDate, long last_updated) {
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

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public TYPE getEventType() {
		return eventType;
	}

	public void setEventType(TYPE eventType) {
		this.eventType = eventType;
	}
	
	public void setEventType(String typeString) {
		this.eventType = TYPE.fromString(typeString);
	}
	
	public void setEventType(int num) {
		this.eventType = TYPE.fromInt(num);
	}

	public DISTRICT getEventDistrict() {
		return eventDistrict;
	}

	public void setEventDistrict(DISTRICT eventDistrict) {
		this.eventDistrict = eventDistrict;
	}
	
	public void setEventDistrict(String districtString) {
		this.eventDistrict = DISTRICT.fromString(districtString);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setStartDate(String startString) {
		try {
			this.startDate = eventDateFormat.parse(startString);
		} catch (ParseException ex) {
			//can't parse the date
			throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setEndDate(String endString) {
		try {
			this.endDate = eventDateFormat.parse(endString);
		} catch (ParseException ex) {
			//can't parse the date
			throw new IllegalArgumentException("Invalid date format. Should be like yyyy-MM-dd");
		}
	}

	public boolean isOfficial() {
		return official;
	}

	public void setOfficial(boolean official) {
		this.official = official;
	}

	public long getLastUpdated() {
		return last_updated;
	}

	public void setLastUpdated(long last_updated) {
		this.last_updated = last_updated;
	}
	
	

}
