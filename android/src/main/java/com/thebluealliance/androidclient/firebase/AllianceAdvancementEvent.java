package com.thebluealliance.androidclient.firebase;

import java.util.HashMap;

public class AllianceAdvancementEvent {
    public final HashMap<String, Integer> advancement;

    public AllianceAdvancementEvent(HashMap<String, Integer> advancement) {
        this.advancement = advancement;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AllianceAdvancementEvent)
                && (((AllianceAdvancementEvent) o).advancement == advancement);
    }
}
