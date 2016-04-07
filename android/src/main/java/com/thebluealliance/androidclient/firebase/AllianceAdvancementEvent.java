package com.thebluealliance.androidclient.firebase;

import java.util.HashMap;

public class AllianceAdvancementEvent {
    public final HashMap<String, Integer> advancement;

    public AllianceAdvancementEvent(HashMap<String, Integer> advancement) {
        this.advancement = advancement;
    }
}
