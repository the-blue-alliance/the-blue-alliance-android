package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",

    TABLE_AWARDS                    = "awards",
                KEY_AWARDKEY        = "awardKey",       //text
                KEY_AWARDNAME       = "awardName",      //text
                KEY_YEAR            = "year",           //int
                KEY_EVENTKEY        = "eventKey",       //text
                KEY_AWARDTYPE       = "awardType",      //int (from award list enum)
                KEY_AWARDWINNER     = "awardWinner",    //string (JsonArray.toString)
                KEY_LASTUPDATE      = "lastUpdated",    //timestamp

    TABLE_EVENTS                    = "events",
                //KEY_EVENTKEY      = "eventKey",       //text
                KEY_EVENTNAME       = "eventName",      //text
                KEY_EVENTSHORT      = "eventShort",     //text
                KEY_EVENTABBREV     = "eventAbbrev",    //text
                KEY_EVENTTYPE       = "eventType",      //int (from event types enum)
                KEY_EVENTDISTRICT   = "eventDistrict",  //int (from district enum)
                KEY_EVENTYEAR       = "eventYear",      //int
                KEY_EVENTSTART      = "eventStart",     //timestamp
                KEY_EVENTEND        = "eventEnd",       //timestamp
                KEY_LOCATION        = "location",       //text
                KEY_EVENTOFFICIAL   = "eventOfficial",  //int(1) - boolean representation
                KEY_EVENTWEBSITE    = "eventWebsite",   //text
                KEY_EVENTWEBCAST    = "eventWebcast",   //text (JsonArray.toString)
                KEY_EVENTRANKINGS   = "eventRankings",  //text (JsonArray.toString)
                KEY_EVENTSTATS      = "eventStats",     //text (JsonArray.toString)
                //KEY_LASTUPDATE    = "lastUpdated",    //timestamp

    TABLE_MATCHES                   = "matches",
                KEY_MATCHKEY        = "matchKey",       //text
                KEY_MATCHTYPE       = "matchType",      //int (from match type enum)
                KEY_MATCHNUMBER     = "matchNumber",    //int
                KEY_SETNUMBER       = "matchSet",       //int
                KEY_ALLIANCES       = "alliances",      //text (flattened json dict of some sort, depends on year)
                KEY_MATCHTIME       = "matchTime",      //time string from schedule
                KEY_MATCHVIDS       = "matchVideo",     //text (flattened json array)
                //KEY_LASTUPDATE    = "lastUpdadted",   //timestamp

    TABLE_MEDIA                     = "media",
                KEY_MEDIATYPE       = "mediaType",      //int (from enum)
                KEY_FOREIGNKEY      = "mediaKey",       //text
                KEY_DETAILS         = "details",        //text, json dict of details
                //KEY_YEAR          = "year",           //int
                KEY_TEAMKEY         = "teamKey",        //text
                //KEY_LASTUPDATE    = "lastUpdated",    //timestamp

    TABLE_TEAMS                     = "teams",
                //KEY_TEAMKEY       = "teamKey",        //text
                KEY_TEAMNAME        = "teamName",       //text (full team name)
                KEY_TEAMNICK        = "teamNick",       //text (team nickname)
                //KEY_LOCATION      = "location",       //text
                KEY_TEAMEVENTS      = "teamEvents"      //text (json array of events, with dict of matches competed in)
                //KEY_LASTUPDATE    = "lastUpdated",    //timestamp
    ;

    private SQLiteDatabase db;

    public Database(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
