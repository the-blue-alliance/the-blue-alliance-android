package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.core.graphics.drawable.DrawableCompat;

import com.thebluealliance.androidclient.R;

public class NoDataView extends RelativeLayout {
    private ImageView mImageView;
    private TextView mTextView;

    public NoDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.no_data_view, this);
        mImageView = findViewById(R.id.no_data_image);
        mTextView = findViewById(R.id.no_data_text);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setText(@StringRes int resId) {
        mTextView.setText(resId);
    }

    public void setImage(Drawable drawable) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getContext().getResources().getColor(R.color.primary_text_color));
        mImageView.setImageDrawable(drawable);
    }

    public void setImage(@DrawableRes int resId) {
        Drawable drawable = getContext().getResources().getDrawable(resId);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getContext().getResources().getColor(R.color.primary_text_color));
        mImageView.setImageDrawable(drawable);
    }

    public void setImageVisisble(boolean visible) {
        mImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @VisibleForTesting
    public CharSequence getText() {
        return mTextView != null ? mTextView.getText() : null;
    }

    @VisibleForTesting
    public Drawable getImage() {
        return mImageView.getDrawable();
    }
}
