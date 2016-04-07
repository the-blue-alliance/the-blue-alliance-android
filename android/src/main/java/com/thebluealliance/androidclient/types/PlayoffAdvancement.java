package com.thebluealliance.androidclient.types;

import com.thebluealliance.androidclient.R;

import android.support.annotation.StringRes;

public enum PlayoffAdvancement {
    NONE(R.string.match_abbrev_unknown, 0),
    OCTO(R.string.match_abbrev_octo, 1),
    QUARTER(R.string.match_abbrev_quarter, 2),
    SEMI(R.string.match_abbrev_semi, 3),
    FINAL(R.string.match_abbrev_final, 4),
    WINNER(R.string.match_abbrev_winner, 5);

    private @StringRes int abbreviation;
    private int level;

    PlayoffAdvancement(@StringRes int abbreviation, int level) {
        this.abbreviation = abbreviation;
        this.level = level;
    }

    public @StringRes int getAbbreviation() {
        return abbreviation;
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
}
