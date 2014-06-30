package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.comparators.EventSortByTypeAndDateComparator;
import com.thebluealliance.androidclient.comparators.EventSortByTypeAndNameComparator;
import com.thebluealliance.androidclient.listitems.EventWeekHeader;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.SimpleEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File created by phil on 6/15/14.
 */
public class EventHelper {

    public static final DateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
    public static final SimpleDateFormat renderDateFormat = new SimpleDateFormat("MMM d, yyyy");
    public static final SimpleDateFormat shortRenderDateFormat = new SimpleDateFormat("MMM d");
    public static final SimpleDateFormat weekFormat = new SimpleDateFormat("w");
    public static final String CHAMPIONSHIP_LABEL = "Championship Event";
    public static final String REGIONAL_LABEL = "Week %d";
    public static final String WEEKLESS_LABEL = "Other Official Events";
    public static final String OFFSEASON_LABEL = "Offseason Events";
    public static final String PRESEASON_LABEL = "Preseason Events";

    public static boolean validateEventKey(String key) {
        if (key == null || key.isEmpty()) return false;
        return key.matches("^[1-9]\\d{3}[a-z,0-9]+$");
    }

    public static int competitionWeek(Date date) {
        if (date == null) return -1;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = Integer.parseInt(weekFormat.format(date)) - Utilities.getFirstCompWeek(cal.get(Calendar.YEAR));
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

    public static String generateLabelForEvent(Event e) {
        switch (e.getEventType()) {
            case CMP_DIVISION:
            case CMP_FINALS:
                return CHAMPIONSHIP_LABEL;
            case REGIONAL:
            case DISTRICT:
            case DISTRICT_CMP:
                return String.format(REGIONAL_LABEL, e.getCompetitionWeek());
            case OFFSEASON:
                return OFFSEASON_LABEL;
            case PRESEASON:
                return PRESEASON_LABEL;
            default:
                return WEEKLESS_LABEL;
        }
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

    public static int weekNumFromLabel(HashMap<String, ArrayList<SimpleEvent>> groupedEvents, String label) {
        if (groupedEvents.containsKey(label)) {
            SimpleEvent e = groupedEvents.get(label).get(0);
            return e.getCompetitionWeek();
        } else {
            return -1;
        }
    }

    public static HashMap<String, ArrayList<SimpleEvent>> groupByWeek(ArrayList<SimpleEvent> events) {
        HashMap<String, ArrayList<SimpleEvent>> groups = new HashMap<>();
        ArrayList<SimpleEvent> offseason = new ArrayList<>(),
                preseason = new ArrayList<>(),
                weekless = new ArrayList<>();

        for (SimpleEvent e : events) {
            ArrayList<SimpleEvent> list;
            if (e.isOfficial() && (e.getEventType() == TYPE.CMP_DIVISION || e.getEventType() == TYPE.CMP_FINALS)) {
                if (!groups.containsKey(CHAMPIONSHIP_LABEL) || groups.get(CHAMPIONSHIP_LABEL) == null) {
                    list = new ArrayList<>();
                    groups.put(CHAMPIONSHIP_LABEL, list);
                } else {
                    list = groups.get(CHAMPIONSHIP_LABEL);
                }
                list.add(e);
            } else if (e.isOfficial() && (e.getEventType() == TYPE.REGIONAL || e.getEventType() == TYPE.DISTRICT || e.getEventType() == TYPE.DISTRICT_CMP)) {
                if (e.getStartDate() == null) {
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
            } else if (e.getEventType() == TYPE.PRESEASON) {
                preseason.add(e);
            } else {
                offseason.add(e);
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
     * Returns a list of events sorted by start date and type. This is optimal for viewing a team's season schedule.
     *
     * @param events a list of events to render
     * @return a list of ListItems representing the sorted events
     */
    public static ArrayList<ListItem> renderEventListForTeam(ArrayList<SimpleEvent> events) {
        return renderEventListWithComparator(events, new EventSortByTypeAndDateComparator());
    }

    /**
     * Returns a list of events sorted by name and type. This is optimal for quickly finding a particular
     * event within a given week.
     *
     * @param events a list of events to render
     * @return a list of ListItems representing the sorted events
     */
    public static ArrayList<ListItem> renderEventListForWeek(ArrayList<SimpleEvent> events) {
        return renderEventListWithComparator(events, new EventSortByTypeAndNameComparator());
    }

    private static ArrayList<ListItem> renderEventListWithComparator(ArrayList<SimpleEvent> events, Comparator<Event> comparator) {
        ArrayList<ListItem> eventListItems = new ArrayList<>();
        Collections.sort(events, comparator);
        EventHelper.TYPE lastType = null, currentType;
        int lastDistrict = -1, currentDistrict;
        for (SimpleEvent event : events) {
            currentType = event.getEventType();
            currentDistrict = event.getDistrictEnum();
            if (currentType != lastType || (currentType == EventHelper.TYPE.DISTRICT && currentDistrict != lastDistrict)) {
                if (currentType == EventHelper.TYPE.DISTRICT) {
                    eventListItems.add(new EventWeekHeader(event.getDistrictTitle() + " District Events"));
                } else {
                    eventListItems.add(new EventWeekHeader(currentType.toString()));
                }
            }
            eventListItems.add(event.render());
            lastType = currentType;
            lastDistrict = currentDistrict;
        }
        return eventListItems;
    }

    public static String getShortNameForEvent(String eventName, TYPE eventType) {
        // Preseason and offseason events will probably fail our regex matcher
        if (eventType == EventHelper.TYPE.PRESEASON || eventType == EventHelper.TYPE.OFFSEASON) {
            return eventName;
        }
        String shortName = "";
        Pattern regexPattern = Pattern.compile("(MAR |PNW )?(FIRST Robotics|FRC)?(.*)( FIRST Robotics| FRC)?( District| Regional| Region| State| Tournament| FRC| Field| Division)( Competition| Event| Championship)?( sponsored by.*)?");
        Matcher m = regexPattern.matcher(eventName);
        if (m.matches()) {
            String s = m.group(3);
            regexPattern = Pattern.compile("(.*)(FIRST Robotics|FRC)");
            m = regexPattern.matcher(s);
            if (m.matches()) {
                shortName = m.group(1).trim();
            } else {
                shortName = s.trim();
            }
        }

        // In case an event has a weird name, return the event name
        if(shortName.isEmpty()) {
            return eventName;
        }

        return shortName;
    }

    public static String getDateString(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return "";
        if (startDate.equals(endDate)) {
            return EventHelper.renderDateFormat.format(startDate);
        }
        return EventHelper.shortRenderDateFormat.format(startDate) + " to " + EventHelper.renderDateFormat.format(endDate);
    }
}
