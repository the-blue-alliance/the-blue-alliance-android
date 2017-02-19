package com.thebluealliance.androidclient.viewmodels;

import android.text.SpannableString;
import android.text.Spanned;

public class LabelValueViewModel extends BaseViewModel{

    private String mLabel;
    private Spanned mValue;
    private boolean mBoldText;

    public LabelValueViewModel(String label, String value) {
        mLabel = label;
        mValue = new SpannableString(value);
        mBoldText = true;
    }

    public LabelValueViewModel(String label, Spanned value) {
        mLabel = label;
        mValue = value;
        mBoldText = false;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public Spanned getValue() {
        return mValue;
    }

    public void setValue(Spanned value) {
        mValue = value;
    }

    public boolean getBoldText() {
        return mBoldText;
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
