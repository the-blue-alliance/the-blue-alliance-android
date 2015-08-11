package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.annotation.*;

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
        mImageView = (ImageView) findViewById(R.id.no_data_image);
        mTextView = (TextView) findViewById(R.id.no_data_text);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setText(@StringRes int resId) {
        mTextView.setText(resId);
    }

    public void setImage(Drawable drawable) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getContext().getResources().getColor(R.color.black));
        mImageView.setImageDrawable(drawable);
    }

    public void setImage(@DrawableRes int resId) {
        Drawable drawable = getContext().getResources().getDrawable(resId, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getContext().getResources().getColor(R.color.black));
        mImageView.setImageDrawable(drawable);
    }

    public void setImageVisisble(boolean visible) {
        mImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
