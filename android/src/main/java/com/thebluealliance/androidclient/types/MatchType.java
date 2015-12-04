package com.thebluealliance.androidclient.types;

public enum MatchType {
    NONE,
    QUAL {
        @Override
        public MatchType previous() {
            return null; // see below for options for this line
        }
    },
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

    public static MatchType fromShortType(String str) {
        switch (str) {
            case "qm":
                return QUAL;
            case "ef":
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
        if (key.contains("_ef") || key.contains("_qf")) return QUARTER;
        if (key.contains("_sf")) return SEMI;
        if (key.contains("_f")) return FINAL;
        return NONE;
    }
}
