package com.thebluealliance.androidclient.types;

public enum EventDetailType {
    ALLIANCES("alliances"),
    RANKINGS("rankings"),
    OPRS("oprs"),
    INSIGHTS("insights"),
    DISTRICT_POINTS("districtPoints");

    private final String keySuffix;
    EventDetailType(String keySuffix) {
        this.keySuffix = keySuffix;
    }

    public String getKeySuffix() {
        return keySuffix;
    }
}
