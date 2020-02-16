package com.thebluealliance.androidclient.eventbus;

import androidx.annotation.Nullable;

public class TeamAvatarUpdateEvent {
    private String b64Image;

    public TeamAvatarUpdateEvent(String b64Image) {
        this.b64Image = b64Image;
    }

    public String getB64Image() {
        return b64Image;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof TeamAvatarUpdateEvent)
                && ((TeamAvatarUpdateEvent) obj).getB64Image().equals(b64Image);
    }

    @Override
    public int hashCode() {
        return b64Image.hashCode();
    }
}
