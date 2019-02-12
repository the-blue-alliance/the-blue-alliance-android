package com.thebluealliance.androidclient.datafeed;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.EventDetail;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.EventDetailType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class APICache {

    private final Database mDb;
    private final Gson mGson;

    @Inject
    public APICache(Database db, Gson gson) {
        mDb = db;
        mGson = gson;
    }

    public Observable<List<Team>> fetchTeamPage(int pageNum) {
        String where = String.format("%1$s >= ? AND %1$s <= ?", TeamsTable.NUMBER);
        int startNum = 500 * pageNum;
        return Observable.create((observer) -> {
            try {
                List<Team> teams = mDb.getTeamsTable().getForQuery(null, where, new String[]{Integer.toString(startNum), Integer.toString(startNum + 499)});
                if (!observer.isUnsubscribed()) {
                    observer.onNext(teams);
                    observer.onCompleted();
                }
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<Team> fetchTeam(String teamKey) {
        return Observable.create((observer) -> {
            try {
                Team team = mDb.getTeamsTable().get(teamKey);
                observer.onNext(team);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<Integer> fetchLargestTeamNumber() {
        return Observable.create((observer) -> {
            try {
                Cursor cursor = mDb.getTeamsTable().query(new String[]{TeamsTable.KEY, TeamsTable.NUMBER}, null, null, null, null, TeamsTable.NUMBER + " DESC", "1");
                if (!observer.isUnsubscribed()) {
                    if (cursor == null || !cursor.moveToFirst()) {
                        observer.onNext(0);
                    } else {
                        observer.onNext(cursor.getInt(cursor.getColumnIndex(TeamsTable.NUMBER)));
                    }
                }
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Event>> fetchTeamEvents(String teamKey, int year) {
        return Observable.create((observer) -> {
            try {
                List<Event> events = mDb.getEventTeamsTable().getEvents(teamKey, year);
                observer.onNext(events);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Award>> fetchTeamAtEventAwards(String teamKey, String eventKey) {
        return Observable.create((observer) -> {
            try {
                observer.onNext(mDb.getAwardsTable().getTeamAtEventAwards(teamKey, eventKey));
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Match>> fetchTeamAtEventMatches(String teamKey, String eventKey) {
        return Observable.create((observer) -> {
            try {
                observer.onNext(mDb.getMatchesTable().getTeamAtEventMatches(teamKey, eventKey));
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Integer>> fetchTeamYearsParticipated(String teamKey) {
        return Observable.create((observer) -> {
            try {
                Team team = mDb.getTeamsTable().get(teamKey);
                observer.onNext(team == null ? new ArrayList<Integer>() : team.getYearsParticipated());
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Media>> fetchTeamMediaInYear(String teamKey, int year) {
        return Observable.create((observer) -> {
            try {
                String where = MediasTable.TEAMKEY + " = ? AND " + MediasTable.YEAR + " = ?";
                List<Media> medias =
                  mDb.getMediasTable()
                    .getForQuery(null, where, new String[]{teamKey, Integer.toString(year)});
                observer.onNext(medias);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Media>> fetchTeamSocialMedia(String teamKey) {
        return Observable.create((observer) -> {
            try {
                String where = MediasTable.TEAMKEY + " = ? AND " + MediasTable.YEAR + " = -1";
                List<Media> medias = mDb.getMediasTable()
                                        .getForQuery(null, where, new String[]{teamKey});
                observer.onNext(medias);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Event>> fetchEventsInYear(int year) {
        return Observable.create((observer) -> {
            try {
                String where = String.format("%1$s = ?", EventsTable.YEAR);
                List<Event> events = mDb.getEventsTable()
                  .getForQuery(null, where, new String[]{Integer.toString(year)});
                if (!observer.isUnsubscribed()) {
                    observer.onNext(events);
                    observer.onCompleted();
                }
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Event>> fetchEventsInWeek(int year, int week) {
        return Observable.create((observer) -> {
            try {
                String where =
                  String.format("%1$s = ? AND %2$s = ?", EventsTable.YEAR, EventsTable.WEEK);
                List<Event> events = mDb.getEventsTable()
                  .getForQuery(null, where,
                    new String[]{Integer.toString(year), Integer.toString(week)});
                observer.onNext(events);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Event>> fetchEventsInMonth(int year, int month) {
        return Observable.create((observer) -> {
            try {
                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.set(year, month, 1);
                String start = Long.toString(cal.getTimeInMillis());
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                String end = Long.toString(cal.getTimeInMillis());

                String where = String.format(
                  "%1$s >= ? AND %2$s <= ?",
                  EventsTable.START,
                  EventsTable.START);
                List<Event> events = mDb.getEventsTable()
                  .getForQuery(null, where, new String[]{start, end});
                observer.onNext(events);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<Event> fetchEvent(String eventKey) {
        return Observable.create((observer) -> {
            try {
                Event event = mDb.getEventsTable().get(eventKey);
                observer.onNext(event);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Team>> fetchEventTeams(String eventKey) {
        return Observable.create((observer) -> {
            try {
                List<Team> teams = mDb.getEventTeamsTable().getTeams(eventKey);
                observer.onNext(teams);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<EventTeam> fetchEventTeam(String teamKey, String eventKey) {
        return Observable.create((observer) -> {
            try {
                String etKey = EventTeamHelper.generateKey(eventKey, teamKey);
                EventTeam eventTeam = mDb.getEventTeamsTable().get(etKey);
                observer.onNext(eventTeam);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<RankingResponseObject> fetchEventRankings(String eventKey) {
        return Observable.create((observer) -> {
            try {
                String dbKey = EventDetail.buildKey(eventKey, EventDetailType.RANKINGS);
                EventDetail detail = mDb.getEventDetailsTable().get(dbKey);
                if (detail != null) {
                    observer.onNext(detail.getDataForRankings(mGson));
                }
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<EventAlliance>> fetchEventAlliances(String eventKey) {
        return Observable.create((observer) -> {
            try {
                String dbKey = EventDetail.buildKey(eventKey, EventDetailType.ALLIANCES);
                EventDetail detail = mDb.getEventDetailsTable().get(dbKey);
                if (detail != null) {
                    observer.onNext(detail.getDataForAlliances(mGson));
                }
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Match>> fetchEventMatches(String eventKey) {
        return Observable.create((observer) -> {
            try {
                String where = String.format("%1$s = ?", MatchesTable.EVENT);
                List<Match> matches = mDb.getMatchesTable()
                  .getForQuery(null, where, new String[]{eventKey});
                observer.onNext(matches);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<JsonElement> fetchJsonEventDetail(String eventKey, EventDetailType type) {
        return Observable.create((observer) -> {
           try {
               String dbKey = EventDetail.buildKey(eventKey, type);
               EventDetail detail = mDb.getEventDetailsTable().get(dbKey);
               if (detail != null) {
                   observer.onNext(detail.getDataAsJson(mGson));
               }
               observer.onCompleted();
           } catch (Exception e) {
               observer.onError(e);
           }
        });
    }

    public Observable<List<Award>> fetchEventAwards(String eventKey) {
        return Observable.create((observer) -> {
            try {
                String where = String.format("%1$s = ?", AwardsTable.EVENTKEY);
                List<Award> awards = mDb.getAwardsTable()
                  .getForQuery(null, where, new String[]{eventKey});
                observer.onNext(awards);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<District> fetchDistrict(String districtKey) {
        return Observable.create((observer) -> {
            try {
                District district = mDb.getDistrictsTable().get(districtKey);
                observer.onNext(district);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<District>> fetchDistrictList(int year) {
        return Observable.create((observer) -> {
            try {
                String where = String.format("%1$s = ?", DistrictsTable.YEAR);
                List<District> districts = mDb.getDistrictsTable()
                  .getForQuery(null, where, new String[]{Integer.toString(year)});
                observer.onNext(districts);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Event>> fetchDistrictEvents(String districtKey) {
        return Observable.create((observer) -> {
            try {
                String where =
                  String.format("%1$s = ?", EventsTable.DISTRICT_KEY);
                List<Event> events = mDb.getEventsTable().getForQuery(
                  null,
                  where,
                  new String[]{districtKey});
                observer.onNext(events);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<DistrictRanking>> fetchDistrictRankings(String districtKey) {
        return Observable.create((observer) -> {
            try {
                String where = String.format(
                  "%1$s = ?", DistrictTeamsTable.DISTRICT_KEY);
                List<DistrictRanking> districtTeams = mDb.getDistrictTeamsTable().getForQuery(
                  null,
                  where,
                  new String[]{districtKey});
                observer.onNext(districtTeams);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<Match> fetchMatch(String matchKey) {
        return Observable.create((observer) -> {
            try {
                Match match = mDb.getMatchesTable().get(matchKey);
                observer.onNext(match);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

}
