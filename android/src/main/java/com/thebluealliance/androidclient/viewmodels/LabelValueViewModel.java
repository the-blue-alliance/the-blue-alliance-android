package com.thebluealliance.androidclient.viewmodels;

import android.view.View;

public class LabelValueViewModel extends BaseViewModel {

    private String mLabel;
    private String mValue;
    private View mValueView;

    public LabelValueViewModel(String label, String value) {
        mLabel = label;
        mValue = value;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof LabelValueViewModel)) {
            return false;
        }

        LabelValueViewModel model = (LabelValueViewModel) o;

        return mLabel.equals(model.getLabel())
                && mValue.equals(model.getValue());
    }

    @Override public int hashCode() {
        return hashFromValues(mLabel, mValue);
    }
}
