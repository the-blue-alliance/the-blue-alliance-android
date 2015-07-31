package com.thebluealliance.androidclient.binders;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.models.NoDataViewParams;

/**
 * Display a "No Data Available" notice.
 * This doesn't <i>need</i> to extend {@link AbstractDataBinder}, but it may be a good idea later
 */
public class NoDataBinder {

    //TODO maybe make this a custom view that's a container instead
    private ImageView mImageView;
    private TextView mTextView;

    public void setImageView(ImageView imageView) {
        this.mImageView = imageView;
    }

    public void setTextView(TextView textView) {
        this.mTextView = textView;
    }

    public void bindData(@Nullable NoDataViewParams data) {
        if (data == null) {
            return;
        }
        //TODO set visible
        mImageView.setImageResource(data.getImageResId());
        mTextView.setText(data.getTextResId());
    }

    public void unbindData() {
        //TODO hide views
    }
}
