package com.thebluealliance.androidclient.types;

import com.thebluealliance.androidclient.R;

import android.support.annotation.StringRes;

public enum MatchType {
    NONE,
    QUAL {
        @Override
        public MatchType previous() {
            return null; // see below for options for this line
        }
    },
    OCTO,
    QUARTER,
    SEMI,
    FINAL {
        @Override
        public MatchType next() {
            return null; // see below for options for this line
        }
    };

    public MatchType next() {
        // No bounds checking required here, because the last instance overrides
        return values()[ordinal() + 1];
    }

    public MatchType previous() {
        // No bounds checking required here, because the last instance overrides
        return values()[ordinal() - 1];
    }

    public MatchType get(String str) {
        return valueOf(str);
    }

    public @StringRes int getTypeName() {
        switch (this) {
            case QUAL: return R.string.match_type_qual;
            case OCTO: return R.string.match_type_octo;
            case QUARTER: return R.string.match_type_quarter;
            case SEMI: return R.string.match_type_semis;
            case FINAL: return R.string.match_type_finals;
            default: return R.string.match_type_unknown;
        }
    }

    public @StringRes int getTypeAbbreviation() {
        switch (this) {
            case QUAL: return R.string.match_abbrev_qual;
            case OCTO: return R.string.match_abbrev_octo;
            case QUARTER: return R.string.match_abbrev_quarter;
            case SEMI: return R.string.match_abbrev_semi;
            case FINAL: return R.string.match_abbrev_final;
            default: return R.string.match_abbrev_unknown;
        }
    }

    public int getPlayOrder() {
        switch (this) {
            case QUAL: return 1;
            case OCTO: return 2;
            case QUARTER: return 3;
            case SEMI: return 4;
            case FINAL: return 5;
            default: return 0;
        }
    }

    public static MatchType fromShortType(String str) {
        switch (str) {
            case "qm":
                return QUAL;
            case "ef":
                return OCTO;
            case "qf":
                return QUARTER;
            case "sf":
                return SEMI;
            case "f":
                return FINAL;
            default:
                throw new IllegalArgumentException("Invalid short type");
        }
    }

    public static MatchType fromKey(String key) {
        if (key.contains("_qm")) return QUAL;
        if (key.contains("_qf")) return QUARTER;
        if (key.contains("_ef")) return OCTO;
        if (key.contains("_sf")) return SEMI;
        if (key.contains("_f")) return FINAL;
        return NONE;
    }
}
