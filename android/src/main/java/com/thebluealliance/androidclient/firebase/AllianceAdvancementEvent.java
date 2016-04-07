package com.thebluealliance.androidclient.firebase;

import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import java.util.HashMap;

public class AllianceAdvancementEvent {
    public final HashMap<String, PlayoffAdvancement> advancement;

    public AllianceAdvancementEvent(HashMap<String, PlayoffAdvancement> advancement) {
        this.advancement = advancement;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AllianceAdvancementEvent)
                && (((AllianceAdvancementEvent) o).advancement == advancement);
    }
}
