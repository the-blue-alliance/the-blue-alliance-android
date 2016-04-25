package com.thebluealliance.androidclient.eventbus;


import com.thebluealliance.androidclient.models.Award;

import java.util.List;

public class EventAwardsEvent {
    private List<Award> mAwards;

    public EventAwardsEvent(List<Award> awards) {
        mAwards = awards;
    }

    public List<Award> getAwards() {
        return mAwards;
    }
}
