package com.thebluealliance.colorpicker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class ColorPickerDialog extends DialogFragment implements ColorPalette.OnColorSelectedListener {

    private static final String EXTRA_TITLE_RES = "title";
    private static final String EXTRA_COLORS = "colors";
    private static final String EXTRA_SELECTED_COLOR = "selected_color";

    private @StringRes int mTitleResId;
    private int[] mColors;
    private int mSelectedColor;

    private ColorPickerDialogCallbacks mCallbacks;

    public static ColorPickerDialog newInstance(@StringRes int titleResId, int[] colors, int selectedColor) {
        ColorPickerDialog fragment = new ColorPickerDialog();

        Bundle args = new Bundle();
        args.putInt(EXTRA_TITLE_RES, titleResId);
        args.putIntArray(EXTRA_COLORS, colors);
        args.putInt(EXTRA_SELECTED_COLOR, selectedColor);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitleResId = getArguments().getInt(EXTRA_TITLE_RES);
            mColors = getArguments().getIntArray(EXTRA_COLORS);
            mSelectedColor = getArguments().getInt(EXTRA_SELECTED_COLOR);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SELECTED_COLOR)) {
            mSelectedColor = savedInstanceState.getInt(EXTRA_SELECTED_COLOR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_SELECTED_COLOR, mSelectedColor);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(mTitleResId);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);
        ColorPalette palette = (ColorPalette) view.findViewById(R.id.palette);
        palette.setColors(mColors);
        palette.setSelectedColor(mSelectedColor);
        palette.setOnColorSelectedListener(this);

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        mSelectedColor = color;
    }

    public interface ColorPickerDialogCallbacks {
        void onColorSelectionConfirmed(@ColorInt int color);
        void onCancelled();
    }
}
