package com.thebluealliance.androidclient.datafeed;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.models.Team;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class APICache implements APIv2 {

    private Database mDb;

    @Inject
    public APICache(Database db) {
        mDb = db;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Observable<JsonArray> fetchTeamYearsParticipated(String teamKey) {
        return Observable.create((observer) -> {
            try {
                Team team = mDb.getTeamsTable().get(teamKey);
                observer.onNext(team == null ? new JsonArray() : team.getYearsParticipated());
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
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

    @Override
    public Observable<List<Event>> fetchTeamEventHistory(String teamKey) {
        return null;
    }

    @Override
    public Observable<List<Award>> fetchTeamEventAwards(String teamKey) {
        return null;
    }

    @Override
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
                cal.set(year, month, 1);
                String start = Long.toString(cal.getTimeInMillis());
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                String end = Long.toString(cal.getTimeInMillis());

                String where = String.format(
                  "%1$s >= ? AND %2$s < ?",
                  EventsTable.START,
                  EventsTable.END);
                List<Event> events = mDb.getEventsTable()
                  .getForQuery(null, where, new String[]{start, end});
                observer.onNext(events);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
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

    @Override
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

    @Override
    public Observable<JsonArray> fetchEventRankings(String eventKey) {
        return Observable.create((observer) -> {
            try {
                Event event = mDb.getEventsTable()
                  .get(eventKey, new String[]{EventsTable.RANKINGS});
                observer.onNext(event != null ? event.getRankings() : null);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
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

    @Override
    public Observable<JsonObject> fetchEventStats(String eventKey) {
        return Observable.create((observer) -> {
            try {
                Event event = mDb.getEventsTable()
                  .get(eventKey, new String[]{EventsTable.STATS});
                observer.onNext(event != null ? event.getStats() : null);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
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

    @Override
    public Observable<JsonObject> fetchEventDistrictPoints(String eventKey) {
        return Observable.create((observer) -> {
            try {
                Event event = mDb.getEventsTable().get(eventKey);
                observer.onNext(event != null ? event.getDistrictPoints() : null);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
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

    @Override
    public Observable<List<Event>> fetchDistrictEvents(String districtShort, int year) {
        return Observable.create((observer) -> {
            try {
                String where =
                  String.format("$1%s = ? AND %2$s = ?", EventsTable.YEAR, EventsTable.DISTRICT);
                int districtEnum = DistrictHelper.DISTRICTS.fromAbbreviation(districtShort).ordinal();
                List<Event> events = mDb.getEventsTable().getForQuery(
                  null,
                  where,
                  new String[]{Integer.toString(year), Integer.toString(districtEnum)});
                observer.onNext(events);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
    public Observable<List<DistrictTeam>> fetchDistrictRankings(String districtShort, int year) {
        return Observable.create((observer) -> {
            try {
                String where = String.format(
                  "%1$s = ? AND %2$s = ?",
                  DistrictTeamsTable.YEAR,
                  DistrictTeamsTable.DISTRICT_ENUM);
                int districtEnum = DistrictHelper.DISTRICTS.fromAbbreviation(districtShort).ordinal();
                List<DistrictTeam> districtTeams = mDb.getDistrictTeamsTable().getForQuery(
                  null,
                  where,
                  new String[]{Integer.toString(year), Integer.toString(districtEnum)});
                observer.onNext(districtTeams);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    @Override
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

    public Observable<List<Subscription>> fetchUserSubscriptions(Context context) {
        return Observable.create((observer) -> {
            try {
                String account = AccountHelper.getSelectedAccount(context);
                List<Subscription> subscriptions = mDb.getSubscriptionsTable().getForUser(account);
                observer.onNext(subscriptions);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }

    public Observable<List<Favorite>> fetchUserFavorites(Context context) {
        return Observable.create((observer) -> {
            try {
                String account = AccountHelper.getSelectedAccount(context);
                List<Favorite> favorites = mDb.getFavoritesTable().getForUser(account);
                observer.onNext(favorites);
                observer.onCompleted();
            } catch (Exception e) {
                observer.onError(e);
            }
        });
    }
}
