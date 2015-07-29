package com.thebluealliance.androidclient.helpers;

import android.content.res.Resources;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.comparators.EventSortByDateComparator;
import com.thebluealliance.androidclient.comparators.EventSortByTypeAndDateComparator;
import com.thebluealliance.androidclient.eventbus.LiveEventEventUpdateEvent;
import com.thebluealliance.androidclient.listitems.EventTypeHeader;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class EventHelper {

    public static final String CHAMPIONSHIP_LABEL = "Championship Event";
    public static final String REGIONAL_LABEL = "Week %1$d";
    public static final String WEEKLESS_LABEL = "Other Official Events";
    public static final String OFFSEASON_LABEL = "Offseason Week %1$d";
    public static final String PRESEASON_LABEL = "Preseason Events";
    private static final Pattern eventKeyPattern = Pattern.compile("[a-zA-Z]+");

    private static final Pattern districtEventNamePattern = Pattern.compile("[A-Z]{2,3} District -(.+)");
    private static final Pattern eventEventNamePattern = Pattern.compile("(.+)Event");
    private static final Pattern regionalEventNamePattern =
            Pattern.compile("\\s*(?:MAR |PNW |)(?:FIRST Robotics|FRC|)(.+)(?:(?:District|Regional|Region|State|Tournament|FRC|Field)\\b)");
    private static final Pattern frcEventNamePattern = Pattern.compile("(.+)(?:FIRST Robotics|FRC)");

    public static boolean validateEventKey(String key) {
        if (key == null || key.isEmpty()) return false;
        return key.matches("^[1-9]\\d{3}[a-z,0-9]+$");
    }

    /**
     * Extracts a short name like "Silicon Valley" from an event name like "Silicon Valley Regional
     * sponsored by Google.org".
     * <p>
     * <p/>See <a href="https://github.com/the-blue-alliance/the-blue-alliance/blob/master/helpers/event_helper.py"
     * >the server's event_helper.py</a>.
     */
    public static String shortName(String eventName) {
        Matcher m1 = districtEventNamePattern.matcher(eventName); // XYZ District - NAME
        if (m1.matches()) {
            String partial = m1.group(1).trim();
            Matcher m2 = eventEventNamePattern.matcher(partial); // NAME Event...
            if (m2.lookingAt()) {
                return m2.group(1).trim();
            }
            return partial;
        }

        Matcher m3 = regionalEventNamePattern.matcher(eventName); // ... NAME Regional...
        if (m3.lookingAt()) {
            String partial = m3.group(1);
            Matcher m4 = frcEventNamePattern.matcher(partial); // NAME FIRST Robotics/FRC...
            if (m4.lookingAt()) {
                return m4.group(1).trim();
            } else {
                return partial.trim();
            }
        }

        return eventName.trim();
    }

    public static int getYearWeek(Date date) {
        if (date == null) return -1;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static int competitionWeek(Date date) {
        if (date == null) return -1;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        int week = getYearWeek(cal.getTime()) - Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
        return week < 0 ? 0 : week;
    }

    public static int getEventOrder(TYPE eventType) {
        switch (eventType) {
            default:
            case NONE:
                return 99;
            case REGIONAL:
            case DISTRICT:
                return 2;
            case DISTRICT_CMP:
                return 3;
            case CMP_DIVISION:
                return 4;
            case CMP_FINALS:
                return 5;
            case OFFSEASON:
                return 6;
            case PRESEASON:
                return 1;
        }
    }

    public static String generateLabelForEvent(Event e) throws BasicModel.FieldNotDefinedException {
        switch (e.getEventType()) {
            case CMP_DIVISION:
            case CMP_FINALS:
                return CHAMPIONSHIP_LABEL;
            case REGIONAL:
            case DISTRICT:
            case DISTRICT_CMP:
                return String.format(REGIONAL_LABEL, e.getCompetitionWeek());
            case OFFSEASON:
                int cmpWeek = Utilities.getCmpWeek(e.getEventYear());
                int compWeek = e.getCompetitionWeek();
                return String.format(OFFSEASON_LABEL, compWeek - cmpWeek);
            case PRESEASON:
                return PRESEASON_LABEL;
            default:
                return WEEKLESS_LABEL;
        }
    }

    public static String currentWeekLabel(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return weekLabelFromNum(cal.get(Calendar.YEAR), competitionWeek(date));
    }

    public static String weekLabelFromNum(int year, int weekNum) {

        if (weekNum <= 0) {
            return PRESEASON_LABEL;
        }

        //let's find the week of CMP and base everything else off that
        //there should always be something in the CMP set for every year
        int cmpWeek = Utilities.getCmpWeek(year);

        if (weekNum > 0 && weekNum < cmpWeek) {
            return String.format(REGIONAL_LABEL, weekNum);
        }
        if (weekNum == cmpWeek) {
            return CHAMPIONSHIP_LABEL;
        }
        if (weekNum > cmpWeek) {
            return OFFSEASON_LABEL;
        }
        return WEEKLESS_LABEL;
    }

    public static int weekNumFromLabel(int year, String label) {
        for (int i = 0; i < 20; i++) {
            if (weekLabelFromNum(year, i).equals(label)) {
                return i;
            }
        }
        return -1;
    }

    public static HashMap<String, List<Event>> groupByWeek(List<Event> events) {
        HashMap<String, List<Event>> groups = new HashMap<>();
        ArrayList<Event> offseason = new ArrayList<>(),
                preseason = new ArrayList<>(),
                weekless = new ArrayList<>();

        for (Event e : events) {
            List<Event> list;
            try {
                boolean official = e.isOfficial();
                TYPE type = e.getEventType();
                Date start = e.getStartDate();
                if (official && (type == TYPE.CMP_DIVISION || type == TYPE.CMP_FINALS)) {
                    if (!groups.containsKey(CHAMPIONSHIP_LABEL) || groups.get(CHAMPIONSHIP_LABEL) == null) {
                        list = new ArrayList<>();
                        groups.put(CHAMPIONSHIP_LABEL, list);
                    } else {
                        list = groups.get(CHAMPIONSHIP_LABEL);
                    }
                    list.add(e);
                } else if (official && (type == TYPE.REGIONAL || type == TYPE.DISTRICT || type == TYPE.DISTRICT_CMP)) {
                    if (start == null) {
                        weekless.add(e);
                    } else {
                        String label = String.format(REGIONAL_LABEL, e.getCompetitionWeek());
                        if (groups.containsKey(label) && groups.get(label) != null) {
                            groups.get(label).add(e);
                        } else {
                            list = new ArrayList<>();
                            list.add(e);
                            groups.put(label, list);
                        }
                    }
                } else if (type == TYPE.PRESEASON) {
                    preseason.add(e);
                } else {
                    offseason.add(e);
                }
            } catch (BasicModel.FieldNotDefinedException ex) {
                Log.w(Constants.LOG_TAG, "Couldn't determine week for event without the following fields:\n" +
                        "Database.Events.OFFICIAL, Database.Events.TYPE, Database.Events.START");
            }
        }

        if (!weekless.isEmpty()) {
            groups.put(WEEKLESS_LABEL, weekless);
        }
        if (!offseason.isEmpty()) {
            groups.put(OFFSEASON_LABEL, offseason);
        }
        if (!preseason.isEmpty()) {
            groups.put(PRESEASON_LABEL, preseason);
        }

        Log.d(Constants.LOG_TAG, "Categories: " + groups.keySet().toString());

        return groups;
    }

    /* Do not insert any new entries above the existing enums!!!
         * Things depend on their ordinal values, so you can only to the bottom of the list
         */
    public static enum TYPE {
        NONE,
        REGIONAL,
        DISTRICT,
        DISTRICT_CMP,
        CMP_DIVISION,
        CMP_FINALS,
        OFFSEASON,
        PRESEASON;

        public String toString() {
            switch (ordinal()) {
                default:
                case 0:
                    return "";
                case 1:
                    return "Regional Events";
                case 2:
                    return "District Events";
                case 3:
                    return "District Championships";
                case 4:
                    return "Championship Divisions";
                case 5:
                    return "Championship Finals";
                case 6:
                    return "Offseason Events";
                case 7:
                    return "Preseason Events";
            }
        }

        public static TYPE fromString(String str) {
            switch (str) {
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
            switch (num) {
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

        public static TYPE fromLabel(String label) {
            switch (label) {
                case OFFSEASON_LABEL:
                    return OFFSEASON;
                case PRESEASON_LABEL:
                    return PRESEASON;
                case CHAMPIONSHIP_LABEL:
                    return CMP_DIVISION;
                case WEEKLESS_LABEL:
                    return NONE;
                default:
                    return REGIONAL;
            }
        }
    }

    /**
     * Returns a list of events sorted by start date and type. This is optimal for viewing a team's
     * season schedule.
     *
     * @param events a list of events to render
     * @param output list to render events into
     */
    public static void renderEventListForTeam(List<Event> events, List<ListItem> output) {
        renderEventListWithComparator(events, output, new EventSortByTypeAndDateComparator());
    }

    /**
     * Returns a list of events sorted by name and type. This is optimal for quickly finding a
     * particular event within a given week.
     *
     * @param events a list of events to render
     * @param output list to render events into
     */
    public static void renderEventListForWeek(List<Event> events, List<ListItem> output) {
        renderEventListWithComparator(events,output, new EventSortByTypeAndDateComparator());
    }

    private static void renderEventListWithComparator(
      List<Event> events,
      List<ListItem> output,
      Comparator<Event> comparator) {
        Collections.sort(events, comparator);
        EventHelper.TYPE lastType = null, currentType = null;
        int lastDistrict = -1, currentDistrict = -1;
        for (Event event : events) {
            try {
                currentType = event.getEventType();
                currentDistrict = event.getDistrictEnum();
                if (currentType != lastType ||
                  (currentType == EventHelper.TYPE.DISTRICT
                    && currentDistrict != lastDistrict)) {
                    if (currentType == EventHelper.TYPE.DISTRICT) {
                        output.add(
                          new EventTypeHeader(event.getDistrictTitle() + " District Events"));
                    } else {
                        output.add(new EventTypeHeader(currentType.toString()));
                    }
                }
                output.add(event.render());

                if (event.isHappeningNow()) {
                    //send out that there are live matches happening for other things to pick up
                    Log.d(Constants.LOG_TAG, "Sending live event broadcast: " + event.getKey());
                    EventBus.getDefault().post(new LiveEventEventUpdateEvent(event));
                }

            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Missing fields for rendering event lists");
            }
            lastType = currentType;
            lastDistrict = currentDistrict;
        }
    }

    public static void renderEventListForDistrict(
      List<Event> events,
      List<ListItem> output) {
        Collections.sort(events, new EventSortByDateComparator());
        String lastHeader = null, currentHeader = null;
        for (Event event : events) {
            try {
                currentHeader = weekLabelFromNum(event.getEventYear(), event.getCompetitionWeek());
                if (!currentHeader.equals(lastHeader)) {
                    output.add(new EventTypeHeader(currentHeader + " Events"));
                }
                output.add(event.render());

                if (event.isHappeningNow()) {
                    //send out that there are live matches happening for other things to pick up
                    Log.d(Constants.LOG_TAG, "Sending live event broadcast: " + event.getKey());
                    EventBus.getDefault().post(new LiveEventEventUpdateEvent(event));
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.LOG_TAG, "Missing fields for rendering event lists");
            }
            lastHeader = currentHeader;
        }
    }

    public static String getDateString(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return "";
        if (startDate.equals(endDate)) {
            return ThreadSafeFormatters.renderEventDate(startDate);
        }
        return ThreadSafeFormatters.renderEventShortFormat(startDate) + " to " +
          ThreadSafeFormatters.renderEventDate(endDate);
    }

    public static void addFieldByAPIUrl(Event event, String url, String data) {
        if (url.contains("teams")) {
            event.setTeams(data);
        } else if (url.contains("rankings")) {
            event.setRankings(data);
        } else if (url.contains("matches")) {
            event.setMatches(JSONHelper.getasJsonArray(data));
        } else if (url.contains("stats")) {
            event.setStats(data);
        } else if (url.contains("district_points")) {
            event.setDistrictPoints(data);
        }
    }

    public static String extractRankingString(CaseInsensitiveMap rankingElements) {
        // Find if the rankings contain a record; remove it if it does
        Iterator it = rankingElements.entrySet().iterator();
        String record = null;
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) it.next();
            if (entry.getKey().toLowerCase().contains("record".toLowerCase())) {
                record = "(" + rankingElements.get(entry.getKey()) + ")";
                it.remove();
                break;
            }
        }

        if (record == null) {
            Set<String> keys = rankingElements.keySet();
            if (keys.contains("wins") && keys.contains("losses") && keys.contains("ties")) {
                record = "(" + rankingElements.get("wins") + "-" + rankingElements.get("losses") + "-" + rankingElements.get("ties") + ")";
                rankingElements.remove("wins");
                rankingElements.remove("losses");
                rankingElements.remove("ties");
            }
        }

        return record;
    }

    public static String createRankingBreakdown(CaseInsensitiveMap rankingElements) {
        String rankingString = "";
        // Construct rankings string
        Iterator it = rankingElements.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String value = entry.getValue().toString();
            // If we have a number like 235.00, remove the useless .00 so it looks cleaner
            try {
                value = ThreadSafeFormatters.formatDoubleTwoPlaces(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                //Item is not a number
            }

            // Capitalization hack
            String rankingKey = entry.getKey().toString();
            if (rankingKey.length() <= 3) {
                rankingKey = rankingKey.toUpperCase();
            } else {
                rankingKey = capitalize(rankingKey);
            }
            rankingString += rankingKey + ": " + value;
            if (it.hasNext()) {
                rankingString += ", ";
            }
        }
        return rankingString;
    }

    /**
     * Hacky capitalize method to remove dependency on apache lib for only one method Stupid DEX
     * limit...
     *
     * @param string Input string
     * @return Input string with first letter of each word capitalized
     */
    private static String capitalize(String string) {
        StringBuilder sb = new StringBuilder();
        String[] split = string.split(" ");
        for (String s : split) {
            sb.append(s.substring(0, 1).toUpperCase());
            sb.append(s.substring(1));
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String getShortCodeForEventKey(String eventKey) {
        if (validateEventKey(eventKey)) {
            return eventKey.replaceAll("[0-9]+", "");
        } else {
            return eventKey;
        }
    }

    public static class CaseInsensitiveMap<K> extends HashMap<String, K> {

        @Override
        public K put(String key, K value) {
            return super.put(key.toLowerCase(), value);
        }

        public K get(String key) {
            return super.get(key.toLowerCase());
        }

        public boolean contains(String key) {
            return get(key) != null;
        }
    }

    /**
     * Returns an abbreviated event or district code like "CALB" from a match key like
     * "2014calb_qm17" or event key like "2014necmp" or district key like "2014pnw". Returns "" if
     * the argument doesn't parse as containing an event/district code.
     */
    public static String getEventCode(String matchOrEventOrDistrictKey) {
        Matcher m = eventKeyPattern.matcher(matchOrEventOrDistrictKey);

        return m.find() ? m.group().toUpperCase(Locale.US) : "";
    }

    public static String generateAllianceSummary(Resources r, int allianceNumber, int alliancePick) {
        String[] args = new String[2];
        String summary;
        if (allianceNumber > 0) {
            switch (alliancePick) {
                case 0:
                    args[0] = r.getString(R.string.team_at_event_captain);
                    args[1] = allianceNumber + Utilities.getOrdinalFor(allianceNumber);
                    break;
                case -1:
                    args[0] = allianceNumber + Utilities.getOrdinalFor(allianceNumber);
                    break;
                default:
                    args[0] = alliancePick + Utilities.getOrdinalFor(alliancePick) + " " + r.getString(R.string.team_at_event_pick);
                    args[1] = allianceNumber + Utilities.getOrdinalFor(allianceNumber);
                    break;
            }
            if (alliancePick == -1) {
                summary = String.format(r.getString(R.string.alliance_summary_no_pick_num), args[0]);
            } else {
                summary = String.format(r.getString(R.string.alliance_summary), args[0], args[1]);
            }
        } else {
            summary = r.getString(R.string.not_picked);
        }
        return summary;
    }
}
