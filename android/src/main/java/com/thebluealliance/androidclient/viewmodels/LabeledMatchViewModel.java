package com.thebluealliance.androidclient.viewmodels;

import com.thebluealliance.androidclient.listitems.MatchListElement;

public class LabeledMatchViewModel extends BaseViewModel {

    private String mLabel;
    private MatchListElement mMatch;

    public LabeledMatchViewModel(String label, MatchListElement match) {
        mLabel = label;
        mMatch = match;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public MatchListElement getMatch() {
        return mMatch;
    }

    public void setMatch(MatchListElement match) {
        mMatch = match;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof LabeledMatchViewModel)) {
            return false;
        }

        LabeledMatchViewModel model = (LabeledMatchViewModel) o;

        return mLabel.equals(model.getLabel())
                && mMatch.equals(model.getMatch());
    }

    @Override public int hashCode() {
        return hashFromValues(mLabel, mMatch);
    }
}
