package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.thebluealliance.androidclient.R;

/**
 * @author Adam Corpstein
 * @since 10/26/2014
 */
public class SelectableImage extends ImageView {
    protected int mColorStateListId = R.color.default_image_selected;

    public SelectableImage(Context context) {
        super(context);
    }

    public SelectableImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        //Get the resource state
        ColorStateList list = getResources().getColorStateList(mColorStateListId);
        //Set the color filter based on the drawable state
        setColorFilter(list.getColorForState(getDrawableState(), Color.TRANSPARENT));
        //Invalidate so the color updates
        invalidate();
    }


    public void setColorStateListId(int resId) {
        mColorStateListId = resId;
        drawableStateChanged();
    }
}
