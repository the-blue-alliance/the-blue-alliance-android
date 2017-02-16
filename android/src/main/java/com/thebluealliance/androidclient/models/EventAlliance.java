package com.thebluealliance.androidclient.models;

import com.thebluealliance.api.model.IEventAlliance;

import java.util.List;

import javax.annotation.Nullable;

public class EventAlliance implements IEventAlliance {

    private String name;
    private @Nullable List<String> picks;
    private @Nullable List<String> declines;
    private @Nullable Long lastModified;

    @Override public String getName() {
        return name;
    }

    @Override public void setName(String name) {
        this.name = name;
    }

    @Override @Nullable public List<String> getPicks() {
        return picks;
    }

    @Override public void setPicks(@Nullable List<String> picks) {
        this.picks = picks;
    }

    @Override @Nullable public List<String> getDeclines() {
        return declines;
    }

    @Override public void setDeclines(@Nullable List<String> declines) {
        this.declines = declines;
    }

    @Override @Nullable public Long getLastModified() {
        return lastModified;
    }

    @Override public void setLastModified(@Nullable Long lastModified) {
        this.lastModified = lastModified;
    }
}
