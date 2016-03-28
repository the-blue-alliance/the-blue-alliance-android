package com.thebluealliance.colorpicker;

import com.thebluealliance.colorpicker.internal.ColorCircleDrawable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ColorPreference extends DialogPreference {

    private static final @ColorInt int DEFAULT_VALUE = Color.BLACK;

    private @ColorInt int[] mColors;
    private @ColorInt int mCurrentValue;
    private ColorPalette mColorPalette;
    private View mColorView;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .ColorPreference, 0, 0);
        try {
            int id = a.getResourceId(R.styleable.ColorPreference_colors, 0);
            if (id != 0) {
                mColors = getContext().getResources().getIntArray(id);
            }
        } finally {
            a.recycle();
        }

        setDialogLayoutResource(R.layout.dialog_color_picker);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View s = super.onCreateView(parent);
        mColorView = new View(getContext());
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.color_preference_color_view_size);
        mColorView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        updateColorView();
        ViewGroup w = (ViewGroup) s.findViewById(android.R.id.widget_frame);
        w.setVisibility(View.VISIBLE);
        w.addView(mColorView);
        return s;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        // Don't show the positive button; clicking a color will be the "positive" action
        builder.setPositiveButton(null, null);
    }

    private void updateColorView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mColorView.setBackground(new ColorCircleDrawable(mCurrentValue));
        } else {
            mColorView.setBackgroundDrawable(new ColorCircleDrawable(mCurrentValue));
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mColorPalette = (ColorPalette) view.findViewById(R.id.palette);
        mColorPalette.setColors(mColors);
        mColorPalette.setSelectedColor(mCurrentValue);
        mColorPalette.setOnColorSelectedListener(new ColorPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                mCurrentValue = color;
                updateColorView();
                ColorPreference.this.onClick(null, DialogInterface.BUTTON_POSITIVE);
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.value = mCurrentValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        mCurrentValue = myState.value;
        //mNumberPicker.setValue(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
