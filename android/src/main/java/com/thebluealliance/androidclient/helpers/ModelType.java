package com.thebluealliance.androidclient.helpers;

public class ModelType {
    public enum MODELS {
        EVENT,
        TEAM,
        MATCH,
        EVENTTEAM,
        DISTRICT,
        DISTRICTTEAM,
        AWARD,
        MEDIA;

        public String getTitle() {
            switch (this) {
                case EVENT:
                    return "Events";
                case TEAM:
                    return "Teams";
                case MATCH:
                    return "Matches";
                case EVENTTEAM:
                    return "Team@Event";
                case DISTRICT:
                    return "Districts";
                case DISTRICTTEAM:
                    return "Team@District";
                case AWARD:
                    return "Awards";
            }
            return "";
        }

        public String getSingularTitle() {
            switch (this) {
                case EVENT:
                    return "Event";
                case TEAM:
                    return "Team";
                case MATCH:
                    return "Match";
                case EVENTTEAM:
                    return "Team@Event";
                case DISTRICT:
                    return "District";
                case DISTRICTTEAM:
                    return "Team@District";
                case AWARD:
                    return "Awards";
            }
            return "";
        }

        public int getEnum() {
            return this.ordinal();
        }
    }
}
