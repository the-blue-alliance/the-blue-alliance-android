package com.thebluealliance.androidclient.types;

import android.support.annotation.StringRes;

import com.thebluealliance.androidclient.R;

public enum MatchType {
    NONE(R.string.match_type_unknown, R.string.match_abbrev_unknown, 0, false, "m"),
    QUAL(R.string.match_type_qual, R.string.match_abbrev_qual, 1, false, "qm") {
        @Override
        public MatchType previous() {
            return null; // see below for options for this line
        }
    },
    OCTO(R.string.match_type_octo, R.string.match_abbrev_octo, 2, true, "ef"),
    QUARTER(R.string.match_type_quarter, R.string.match_abbrev_quarter, 3, true, "qf"),
    SEMI(R.string.match_type_semis, R.string.match_abbrev_semi, 4, true, "sf"),
    FINAL(R.string.match_type_finals, R.string.match_abbrev_final, 5, true, "f") {
        @Override
        public MatchType next() {
            return null; // see below for options for this line
        }
    };

    private final @StringRes int typeName;
    private final @StringRes int typeAbbreviation;
    private final int playOrder;
    private final boolean isPlayoff;
    private final String compLevel;

    MatchType(@StringRes int typeName, @StringRes int typeAbbreviation, int playOrder, boolean playoff, String compLevel) {
        this.typeName = typeName;
        this.typeAbbreviation = typeAbbreviation;
        this.playOrder = playOrder;
        this.isPlayoff = playoff;
        this.compLevel = compLevel;
    }

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
        return typeName;
    }

    public @StringRes int getTypeAbbreviation() {
        return typeAbbreviation;
    }

    public int getPlayOrder() {
        return playOrder;
    }

    public boolean isPlayoff() {
        return isPlayoff;
    }

    public String getCompLevel() {
        return compLevel;
    }

    public static MatchType fromShortType(String str) {
        if (str == null) return NONE;
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
