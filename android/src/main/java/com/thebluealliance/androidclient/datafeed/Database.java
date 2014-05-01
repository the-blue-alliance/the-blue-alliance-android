package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Date;

/**
 * File created by phil on 4/28/14.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "the-blue-alliance-android-database",

    TABLE_AWARDS = "awards",
            TABLE_EVENTS = "events",
            TABLE_MATCHES = "matches",
            TABLE_MEDIA = "media",
            TABLE_TEAMS = "teams";

    protected SQLiteDatabase db;
    private static Database sDatabaseInstance;

    private static Awards sAwardsInstance;
    private static Events sEventsInstance;
    private static Matches sMatchesInstance;
    private static Media sMediaInstance;
    private static Teams sTeamsInstance;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
        sAwardsInstance = new Awards();
        sEventsInstance = new Events();
        sMatchesInstance = new Matches();
        sMediaInstance = new Media();
        sTeamsInstance = new Teams();
    }

    /**
     * USE THIS METHOD TO GAIN DATABASE REFERENCES!!11!!!
     * This makes sure that db accesses stay thread-safe
     * (which becomes important with multiple AsyncTasks working simultaneously).
     * Should work, per http://touchlabblog.tumblr.com/post/24474750219/single-sqlite-connection
     *
     * @param context Context used to create Database object, if necessary
     * @return Your synchronized reference to use.
     */
    public static synchronized Database getInstance(Context context) {
        if (sDatabaseInstance == null) {
            sDatabaseInstance = new Database(context);
        }
        return sDatabaseInstance;
    }

    public Awards getAwardsTable() {
        return sAwardsInstance;
    }

    public Events getEventsTable() {
        return sEventsInstance;
    }

    public Matches getMatchesTable() {
        return sMatchesInstance;
    }

    public Media getMediaTable() {
        return sMediaInstance;
    }

    public Teams getTeamsTable() {
        return sTeamsInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_AWARDS = "CREATE TABLE " + TABLE_AWARDS + "("
                + Awards.KEY + " TEXT PRIMARY KEY, "
                + Awards.EVENTKEY + " TEXT, "
                + Awards.NAME + " TEXT, "
                + Awards.YEAR + " INTEGER, "
                + Awards.TYPE + " INTEGER, "
                + Awards.WINNER + " TEXT, "
                + Awards.LASTUPDATE + " TIMESTAMP "
                + ")";
        db.execSQL(CREATE_AWARDS);

        String CREATE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
                + Events.KEY + " TEXT PRIMARY KEY, "
                + Events.NAME + " TEXT, "
                + Events.SHORTNAME + " TEXT, "
                + Events.ABBREVIATION + " TEXT, "
                + Events.TYPE + " INTEGER, "
                + Events.DISTRICT + " INTEGER, "
                + Events.START + " TIMESTAMP, "
                + Events.END + " TIMESTAMP, "
                + Events.LOCATION + " TEXT, "
                + Events.OFFICIAL + " INTEGER, "
                + Events.WEBSITE + " TEXT , "
                + Events.WEBCASTS + " TEXT, "
                + Events.RANKINGS + " TEXT, "
                + Events.STATS + " TEXT, "
                + Events.TEAMS + " TEXT, "
                + Events.LASTUPDATE + " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_EVENTS);

        String CREATE_MATCHES = "CREATE TABLE " + TABLE_MATCHES + "("
                + Matches.KEY + " TEXT PRIMARY KEY,"
                + Matches.TYPE + " INTEGER, "
                + Matches.MATCHNUM + " INTEGER, "
                + Matches.SETNUM + " INTEGER, "
                + Matches.ALLIANCES + " TEXT, "
                + Matches.TIMESTRING + " TEXT, "
                + Matches.TIMESTAMP + " TIMESTAMP, "
                + Matches.VIDEOS + " TEXT, "
                + Matches.LASTUPDATE + " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_MATCHES);

        String CREATE_MEDIAS = "CREATE TABLE " + TABLE_MEDIA + "("
                + Medias.TYPE + " INTEGER, "
                + Medias.FOREIGNKEY + " TEXT, "
                + Medias.DETAILS + " TEXT, "
                + Medias.YEAR + " INTEGER, "
                + Medias.TEAMKEY + " TEXT, "
                + Medias.LASTUPDATE + " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_MEDIAS);

        String CREATE_TEAMS = "CREATE TABLE " + TABLE_TEAMS + "("
                + Teams.KEY + " TEXT PRIMARY KEY, "
                + Teams.NAME + " TEXT, "
                + Teams.NICKNAME + " TEXT, "
                + Teams.LOCATION + " TEXT, "
                + Teams.EVENTS + " TEXT, "
                + Teams.WEBSITE + " TEXT, "
                + Teams.LASTUPDATE + " TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TEAMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AWARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);

        // create new tables
        onCreate(db);
    }

    /*
    Awards
     */
    public class Awards implements DatabaseTable<Award> {

        /* Awards are not yet implemented yet in the API.
         * So we can hang off in implementing this class, for now
         */

        public static final String KEY = "awardKey",       //text
                NAME = "awardName",      //text
                YEAR = "year",           //int
                EVENTKEY = "eventKey",       //text
                TYPE = "awardType",      //int (from award list enum)
                WINNER = "awardWinner",    //string (JsonArray.toString)
                LASTUPDATE = "lastUpdated";    //timestamp

        // Reguired to forbid instantiation bey outside classes
        private Awards() {

        }

        @Override
        public long add(Award in) {
            return 0;
        }

        @Override
        public Award get(String key) {
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
    }

    /*
    Events
     */
    public class Events implements DatabaseTable<Event> {
        public static final String KEY = "eventKey",       //text
                NAME = "eventName",      //text
                SHORTNAME = "eventShort",     //text
                ABBREVIATION = "eventAbbrev",    //text
                TYPE = "eventType",      //int (from event types enum)
                DISTRICT = "eventDistrict",  //int (from district enum)
                START = "eventStart",     //timestamp
                END = "eventEnd",       //timestamp
                LOCATION = "location",       //text
                OFFICIAL = "eventOfficial",  //int(1) - boolean representation
                WEBSITE = "eventWebsite",   //text
                WEBCASTS = "eventWebcast",   //text (JsonArray.toString)
                RANKINGS = "eventRankings",  //text (JsonArray.toString)
                STATS = "eventStats",     //text (JsonArray.toString)
                TEAMS = "eventTeams",     //text (JsonArray.toString)
                LASTUPDATE = "lastUpdated";    //timestamp

        // Reguired to forbid instantiation bey outside classes
        private Events() {

        }

        @Override
        public long add(Event in) {
            if (!exists(in.getEventKey())) {
                return db.insert(TABLE_EVENTS, null, in.getParams());
            } else {
                return update(in);
            }
        }

        public void add(ArrayList<SimpleEvent> events){
            for(SimpleEvent e:events){
                add(e);
            }
        }

        @Override
        public Event get(String key) {
            Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY, NAME, SHORTNAME, ABBREVIATION, TYPE, DISTRICT, START, END, LOCATION, OFFICIAL, WEBSITE, WEBCASTS, RANKINGS, STATS, TEAMS, LASTUPDATE},
                    KEY + "=?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Event event = new Event();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setShortName(cursor.getString(2));
                event.setAbbreviation(cursor.getString(3));
                event.setEventType(Event.TYPE.values()[cursor.getInt(4)]);
                event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(5)]);
                event.setStartDate(new Date(cursor.getLong(6)));
                event.setEndDate(new Date(cursor.getLong(7)));
                event.setLocation(cursor.getString(8));
                event.setOfficial(cursor.getInt(9) == 1);
                event.setWebsite(cursor.getString(10));
                event.setWebcasts(JSONManager.getasJsonArray(cursor.getString(11)));
                event.setRankings(JSONManager.getasJsonArray(cursor.getString(12)));
                event.setStats(JSONManager.getasJsonObject(cursor.getString(13)));
                event.setTeams(JSONManager.getasJsonArray(cursor.getString(14)));
                event.setLastUpdated(cursor.getLong(15));

                return event;
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find event in database with key " + key);
                return null;
            }
        }

        /* Only get some of the details for this event */
        public SimpleEvent getSimple(String key) {
            Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY, NAME, TYPE, DISTRICT, START, END, LOCATION, OFFICIAL, STATS},
                    KEY + "=?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                SimpleEvent event = new SimpleEvent();
                event.setEventKey(cursor.getString(0));
                event.setEventName(cursor.getString(1));
                event.setEventType(Event.TYPE.values()[cursor.getInt(2)]);
                event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(3)]);
                event.setStartDate(new Date(cursor.getLong(4)));
                event.setEndDate(new Date(cursor.getLong(5)));
                event.setLocation(cursor.getString(6));
                event.setOfficial(cursor.getInt(7) == 1);
                event.setLastUpdated(cursor.getLong(8));

                return event;
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find event in database with key " + key);
                return null;
            }
        }

        public ArrayList<SimpleEvent> getAll(int year, int week) {
            //return all events happening during the given competition week
            Date start = Event.dateForCompetitionWeek(year, week);
            Date end = Event.dateForCompetitionWeek(year, week+1);
            Log.d("db","Getting events between "+start.getTime()+" - "+end.getTime());
            ArrayList<SimpleEvent> events = new ArrayList<>();
            Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY, NAME, SHORTNAME, ABBREVIATION, TYPE, DISTRICT, START, END, LOCATION, OFFICIAL, LASTUPDATE},
                    START + ">DATETIME(?,'unixepoch') AND "+START+"<DATETIME(?,'unixepoch')",
                    new String[]{Long.toString(start.getTime()/1000),Long.toString(end.getTime()/1000)}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SimpleEvent event = new SimpleEvent();
                    event.setEventKey(cursor.getString(0));
                    event.setEventName(cursor.getString(1));
                    event.setShortName(cursor.getString(2));
                    event.setAbbreviation(cursor.getString(3));
                    event.setEventType(Event.TYPE.values()[cursor.getInt(4)]);
                    event.setEventDistrict(Event.DISTRICT.values()[cursor.getInt(5)]);
                    event.setStartDate(cursor.getString(6));
                    event.setEndDate(cursor.getString(7));
                    event.setLocation(cursor.getString(8));
                    event.setOfficial(cursor.getInt(9) == 1);
                    event.setLastUpdated(cursor.getLong(10));

                    events.add(event);
                }while(cursor.moveToNext());
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find event in database during week " + week);
            }
            return events;
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY}, KEY + "=?", new String[]{key}, null, null, null, null);
            return cursor != null && cursor.moveToFirst();
        }

        @Override
        public int update(Event in) {
            return db.update(TABLE_EVENTS, in.getParams(), KEY + "=?", new String[]{in.getEventKey()});
        }
    }

    /*
    Matches
     */
    public class Matches implements DatabaseTable<Match> {
        public static final String KEY = "matchKey",       //text
                TYPE = "matchType",      //int (from match type enum)
                MATCHNUM = "matchNumber",    //int
                SETNUM = "matchSet",       //int
                ALLIANCES = "alliances",      //text (flattened json dict of some sort, depends on year)
                TIMESTRING = "matchTimeString",//time string from schedule
                TIMESTAMP = "matchTime",      //UNIX timestamp
                VIDEOS = "matchVideo",     //text (flattened json array)
                LASTUPDATE = "lastUpdated";    //timestamp

        // Reguired to forbid instantiation bey outside classes
        private Matches() {

        }

        @Override
        public long add(Match in) {
            if (!exists(in.getKey())) {
                return db.insert(TABLE_MATCHES, null, in.getParams());
            } else {
                return update(in);
            }
        }

        @Override
        public Match get(String key) {
            Cursor cursor = db.query(TABLE_MATCHES, new String[]{KEY, TYPE, MATCHNUM, SETNUM, ALLIANCES, TIMESTRING, TIMESTAMP, VIDEOS, LASTUPDATE},
                    KEY + "=?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Match match = new Match();
                match.setKey(cursor.getString(0));
                match.setType(Match.TYPE.values()[cursor.getInt(1)]);
                match.setMatchNumber(cursor.getInt(2));
                match.setSetNumber(cursor.getInt(3));
                match.setAlliances(JSONManager.getasJsonObject(cursor.getString(4)));
                match.setTimeString(cursor.getString(5));
                match.setTime(cursor.getLong(6));
                match.setVideos(JSONManager.getasJsonArray(cursor.getString(7)));
                match.setLastUpdated(cursor.getLong(8));

                return match;
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find match in database with key " + key);
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = db.query(TABLE_MATCHES, new String[]{KEY}, KEY + "=?", new String[]{key}, null, null, null, null);
            return cursor != null && cursor.moveToFirst();
        }

        @Override
        public int update(Match in) {
            return db.update(TABLE_MATCHES, in.getParams(), KEY + "=?", new String[]{in.getKey()});
        }
    }

    private class Medias implements DatabaseTable<Media> {

        /* NOT YET IMPLEMENTED IN API
         * Holding off until it is...
         */

        public static final String TYPE = "mediaType",      //int (from enum)
                FOREIGNKEY = "mediaKey",       //text
                DETAILS = "details",        //text, json dict of details
                YEAR = "year",           //int
                TEAMKEY = "teamKey",        //text
                LASTUPDATE = "lastUpdated";    //timestamp

        // Reguired to forbid instantiation bey outside classes
        private Medias() {

        }

        @Override
        public long add(Media in) {
            return 0;
        }

        @Override
        public Media get(String key) {
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
    }

    /*
    Teams
     */
    public class Teams implements DatabaseTable<Team> {

        public static final String KEY = "teamKey",        //text
                NAME = "teamName",       //text (full team name)
                NICKNAME = "teamNick",       //text (team nickname)
                LOCATION = "location",       //text
                EVENTS = "teamEvents",     //text (json array of events, with dict of matches competed in)
                WEBSITE = "teamWebsite",    //text
                LASTUPDATE = "lastUpdated";    //timestamp

        // Reguired to forbid instantiation bey outside classes
        private Teams() {

        }

        @Override
        public long add(Team in) {
            if (!exists(in.getTeamKey())) {
                return db.insert(TABLE_TEAMS, null, in.getParams());
            } else {
                return update(in);
            }
        }

        @Override
        public Team get(String key) {
            Cursor cursor = db.query(TABLE_TEAMS, new String[]{KEY, NAME, NICKNAME, LOCATION, WEBSITE, EVENTS, LASTUPDATE},
                    KEY + "=?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Team team = new Team();
                team.setTeamKey(cursor.getString(0));
                team.setFullName(cursor.getString(1));
                team.setNickname(cursor.getString(2));
                team.setLocation(cursor.getString(3));
                team.setWebsite(cursor.getString(4));
                team.setEvents(JSONManager.getasJsonArray(cursor.getString(5)));
                team.setLastUpdated(cursor.getLong(6));

                return team;
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find team in database with key " + key);
                return null;
            }
        }

        public SimpleTeam getSimple(String key) {
            Cursor cursor = db.query(TABLE_TEAMS, new String[]{KEY, NAME, NICKNAME, LOCATION, LASTUPDATE},
                    KEY + "=?", new String[]{key}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                SimpleTeam team = new SimpleTeam();
                team.setTeamKey(cursor.getString(0));
                team.setFullName(cursor.getString(1));
                team.setNickname(cursor.getString(2));
                team.setLocation(cursor.getString(3));
                team.setLastUpdated(cursor.getLong(4));

                return team;
            } else {
                Log.w(Constants.LOG_TAG, "Failed to find team in database with key " + key);
                return null;
            }
        }

        @Override
        public boolean exists(String key) {
            Cursor cursor = db.query(TABLE_TEAMS, new String[]{KEY, NAME}, KEY + "=?", new String[]{key}, null, null, null, null);
            Log.d("exists", "moveToFirst: " + cursor.moveToFirst());
            Log.d("exists", "getCount: " + cursor.getCount());
            if (cursor.moveToFirst()) {
                DatabaseUtils.dumpCurrentRow(cursor);
            }
            return (cursor.moveToFirst()) || (cursor.getCount() != 0);
        }

        @Override
        public int update(Team in) {
            return db.update(TABLE_TEAMS, in.getParams(), KEY + "=?", new String[]{in.getTeamKey()});
        }
    }
}
