package com.thebluealliance.androidclient.types;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.EventAlliance;

import android.support.annotation.StringRes;

public enum PlayoffAdvancement {
    NONE(R.string.match_abbrev_unknown, R.string.playoff_advancement_none, 0),
    OCTO(R.string.match_abbrev_octo, R.string.playoff_advancement_octo, 1),
    QUARTER(R.string.match_abbrev_quarter, R.string.playoff_advancement_quarter, 2),
    SEMI(R.string.match_abbrev_semi, R.string.playoff_advancement_semi, 3),
    FINAL(R.string.match_abbrev_final, R.string.playoff_advancement_final, 4),
    WINNER(R.string.match_abbrev_winner, R.string.playoff_advancement_winner, 5);

    private final @StringRes int abbreviation;
    private final @StringRes int details;
    private final int level;

    PlayoffAdvancement(@StringRes int abbreviation, @StringRes int details, int level) {
        this.abbreviation = abbreviation;
        this.level = level;
        this.details = details;
    }

    public @StringRes int getAbbreviation() {
        return abbreviation;
    }

    public @StringRes int getDetails() {
        return details;
    }

    public int getLevel() {
        return level;
    }

    public static PlayoffAdvancement fromMatchType(MatchType matchType) {
        switch (matchType) {
            case NONE: return NONE;
            case QUAL: return NONE;
            case OCTO: return OCTO;
            case QUARTER: return QUARTER;
            case SEMI: return SEMI;
            case FINAL: return FINAL;
        }
        return NONE;
    }

    public static PlayoffAdvancement fromAlliance(EventAlliance alliance) {
        if (alliance == null
                || alliance.getStatus() == null
                || alliance.getStatus().getLevel() == null) {
            return NONE;
        }
        switch (alliance.getStatus().getLevel()) {
            case "ef": return OCTO;
            case "qf": return QUARTER;
            case "sf": return SEMI;
            case "f":
                if ("won".equals(alliance.getStatus().getStatus())) return WINNER;
                else return FINAL;
            default:
                return NONE;
        }
    }
}
