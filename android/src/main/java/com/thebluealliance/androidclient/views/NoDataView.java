package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.annotation.*;

import com.thebluealliance.androidclient.R;

/**
 * Created by Nathan on 6/2/2015.
 */
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
        mImageView.setImageDrawable(drawable);
    }

    public void setImage(@DrawableRes int resId) {
        mImageView.setImageResource(resId);
    }

    public void setImageVisisble(boolean visible) {
        mImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}