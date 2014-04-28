package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",

        TABLE_AWARDS                    = "awards",
        TABLE_EVENTS                    = "events",
        TABLE_MATCHES                   = "matches",
        TABLE_MEDIA                     = "media",
        TABLE_TEAMS                     = "teams";

    protected SQLiteDatabase db;
    private static Database instance;

    public Database(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        db = getWritableDatabase();
    }

    /**
     * USE THIS METHOD TO GAIN DATABASE REFERENCES!!11!!!
     * This makes sure that db accesses stay thread-safe
     * (which becomes important with multiple AsyncTasks working simultaneously).
     * Should work, per http://touchlabblog.tumblr.com/post/24474750219/single-sqlite-connection
     * @param context Context used to create Database object, if necessary
     * @return Your synchronized reference to use.
     */
    public static synchronized Database getInstance(Context context){
        if(instance == null){
            instance = new Database(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public class Awards implements DatabaseTable<Award>{
        public static final String  KEY                 = "awardKey",       //text
                                    NAME                = "awardName",      //text
                                    YEAR                = "year",           //int
                                    EVENTKEY            = "eventKey",       //text
                                    TYPE                = "awardType",      //int (from award list enum)
                                    WINNER              = "awardWinner",    //string (JsonArray.toString)
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Award in) {
            return 0;
        }

        @Override
        public Award get(String key) {
            return null;
        }

        @Override
        public ArrayList<Award> getAll() {
            return null;
        }

        @Override
        public boolean exists(String key) {
            return false;
        }

        @Override
        public int update(Award in) {
            return 0;
        }

        @Override
        public int delete(String key) {
            return 0;
        }
    }
    public class Events implements DatabaseTable<Event>{
        public static final String  KEY                 = "eventKey",       //text
                                    NAME                = "eventName",      //text
                                    SHORTNAME           = "eventShort",     //text
                                    ABBREVIATION        = "eventAbbrev",    //text
                                    TYPE                = "eventType",      //int (from event types enum)
                                    DISTRICT            = "eventDistrict",  //int (from district enum)
                                    YEAR                = "eventYear",      //int
                                    START               = "eventStart",     //timestamp
                                    END                 = "eventEnd",       //timestamp
                                    LOCATION            = "location",       //text
                                    OFFICIAL            = "eventOfficial",  //int(1) - boolean representation
                                    WEBSITE             = "eventWebsite",   //text
                                    WEBCASTS            = "eventWebcast",   //text (JsonArray.toString)
                                    RANKINGS            = "eventRankings",  //text (JsonArray.toString)
                                    STATS               = "eventStats",     //text (JsonArray.toString)
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Event in) {
            return 0;
        }

        @Override
        public Event get(String key) {
            return null;
        }

        public SimpleEvent getSimple(String key){
            return null;
        }

        @Override
        public ArrayList<Event> getAll() {
            return null;
        }

        @Override
        public boolean exists(String key) {
            return false;
        }

        @Override
        public int update(Event in) {
            return 0;
        }

        @Override
        public int delete(String key) {
            return 0;
        }
    }
    public class Matches implements DatabaseTable<Match>{
        public static final String  KEY                 = "matchKey",       //text
                                    TYPE                = "matchType",      //int (from match type enum)
                                    MATCHNUM            = "matchNumber",    //int
                                    SETNUM              = "matchSet",       //int
                                    ALLIANCES           = "alliances",      //text (flattened json dict of some sort, depends on year)
                                    TIME                = "matchTime",      //time string from schedule
                                    VIDEOS              = "matchVideo",     //text (flattened json array)
                                    LASTUPDATE          = "lastUpdated";   //timestamp

        @Override
        public long add(Match in) {
            return 0;
        }

        @Override
        public Match get(String key) {
            return null;
        }

        @Override
        public ArrayList<Match> getAll() {
            return null;
        }

        @Override
        public boolean exists(String key) {
            return false;
        }

        @Override
        public int update(Match in) {
            return 0;
        }

        @Override
        public int delete(String key) {
            return 0;
        }
    }
    public class Medias implements DatabaseTable<Media>{
        public static final String  TYPE                = "mediaType",      //int (from enum)
                                    FOREIGNKEY          = "mediaKey",       //text
                                    DETAILS             = "details",        //text, json dict of details
                                    YEAR                = "year",           //int
                                    TEAMKEY             = "teamKey",        //text
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Media in) {
            return 0;
        }

        @Override
        public Media get(String key) {
            return null;
        }

        @Override
        public ArrayList<Media> getAll() {
            return null;
        }

        @Override
        public boolean exists(String key) {
            return false;
        }

        @Override
        public int update(Media in) {
            return 0;
        }

        @Override
        public int delete(String key) {
            return 0;
        }
    }
    public class Teams implements DatabaseTable<Team>{
        public static final String  KEY                 = "teamKey",        //text
                                    NAME                = "teamName",       //text (full team name)
                                    NICKNAME            = "teamNick",       //text (team nickname)
                                    LOCATION            = "location",       //text
                                    EVENTS              = "teamEvents",      //text (json array of events, with dict of matches competed in)
                                    LASTUPDATE          = "lastUpdated";    //timestamp

        @Override
        public long add(Team in) {
            return 0;
        }

        @Override
        public Team get(String key) {
            return null;
        }

        public Team getSimple(String key){
            return null;
        }

        @Override
        public ArrayList<Team> getAll() {
            return null;
        }

        @Override
        public boolean exists(String key) {
            return false;
        }

        @Override
        public int update(Team in) {
            return 0;
        }

        @Override
        public int delete(String key) {
            return 0;
        }
    }
}
