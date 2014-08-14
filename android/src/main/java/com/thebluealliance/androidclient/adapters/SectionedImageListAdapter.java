package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.models.Media;

import java.util.ArrayList;

/**
 * Created by Nathan on 8/14/2014.
 */
public class SectionedImageListAdapter extends BaseAdapter {

    public ArrayList<Section> mSections;

    private int mImagesPerRow = -1;

    private int mImageSize = -1;

    private int mDividerWidth;

    private Context mContext;

    public SectionedImageListAdapter(Context context, ListView listView, ArrayList<Section> sections) {
        mContext = context;
        mSections = sections;

        int listViewWidth = listView.getWidth();
        int minImageWidth = mContext.getResources().getDimensionPixelSize(R.dimen.sectioned_image_list_image_min_width);
        mDividerWidth = mContext.getResources().getDimensionPixelSize(R.dimen.sectioned_image_list_divider_width);

        // Calculate number of items that can fit horizontally in a row
        int numChildrenPerRow = listViewWidth / minImageWidth;

        // Calculate the width of each image
        int totalDividerWidth = mDividerWidth * (numChildrenPerRow - 1);
        while ((numChildrenPerRow * minImageWidth) + totalDividerWidth < listViewWidth) {
            minImageWidth++;
        }

        mImageSize = minImageWidth;
        mImagesPerRow = numChildrenPerRow;
    }

    @Override
    public int getCount() {
        // Count the number of rows we have. That amounts to one header and a certain number of image
        // rows per section
        int count = 0;
        for (Section section : mSections) {
            count++;
            count += section.getImageRowCount(mImagesPerRow);
        }
        return count;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Locate the specific section we need to get the row from
        Log.d(Constants.LOG_TAG, "getView position: " + position);
        int currentRow = -1;
        int sectionNum = -1;
        int sectionRow = -1;
        boolean header = false;
        boolean foundRow = false;
        for (Section section : mSections) {
            currentRow++;
            sectionNum++;
            // If the first row in the section is the desired position, this is a header row.
            if (currentRow == position) {
                header = true;
                Log.d(Constants.LOG_TAG, "Position: " + position + "; header");
                break;
            } else {
                // Iterate through the rows of the section to see if the one we want is in here.
                Log.d(Constants.LOG_TAG, "Image row count: " + section.getImageRowCount(mImagesPerRow));
                // New section; reset the position of the row within the section
                sectionRow = -1;
                for (int i = 0; i < section.getImageRowCount(mImagesPerRow); i++) {
                    currentRow++;
                    sectionRow++;
                    if (currentRow == position) {
                        header = false;
                        Log.d(Constants.LOG_TAG, "Position: " + position + "; not header; section row: " + sectionRow);
                        foundRow = true;
                        break;
                    }
                }
                if(foundRow) {
                    break;
                }
            }
        }

        View v = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (header) {
            // This is a header
            v = inflater.inflate(R.layout.section_name_row, null);
            ((TextView) v.findViewById(R.id.section_name)).setText(mSections.get(sectionNum).getTitle());
            return v;
        } else {
            v = inflater.inflate(R.layout.image_row, null);
            Media[] medias = mSections.get(sectionNum).getMediasForRow(sectionRow, mImagesPerRow);
            Picasso picasso = Picasso.with(mContext);
            for (int i = 0; i < medias.length; i++) {
                // This is an image
                ImageView image = new ImageView(mContext);
                image.setClickable(true);
                image.setFocusable(true);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mImageSize, mImageSize);
                // Only set right margin if it's not the last item in a row
                if (i != medias.length - 1) {
                    lp.setMargins(0, 0, mDividerWidth, 0);
                }
                ((LinearLayout) v).addView(image, i, lp);
                picasso.load(medias[i].getMediaURL()).into(image);
                bindMediaToView(image, medias[i]);
            }
            return v;
        }
    }

    private void bindMediaToView(View v, final Media media) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Track Click
                Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, mContext);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("media_click")
                        .setAction(media.getMediaURL())
                        .setLabel(media.isVideo()?"video":"cd_photo")
                        .build());

                mContext.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(media.getMediaURL())));
            }
        });
    }

    public static class Section {
        String title;
        ArrayList<Media> medias;

        public Section(String title, ArrayList<Media> medias) {
            this.title = title;
            this.medias = medias;
        }

        /**
         * Calculates how many rows of images this section will display.
         *
         * @return the number of rows of images this section has.
         */
        int getImageRowCount(int imagesPerRow) {
            int rows = medias.size() / imagesPerRow;
            if (medias.size() % imagesPerRow != 0) {
                rows++;
            }
            return rows;
        }

        public Media[] getMediasForRow(int row, int imagesPerRow) throws ArrayIndexOutOfBoundsException {
            Log.d(Constants.LOG_TAG, "Row#: " + row);
            Log.d(Constants.LOG_TAG, "Images per row: " + imagesPerRow);
            // Get index of first image in row
            int indexOfFirst = imagesPerRow * row;
            if (indexOfFirst > medias.size()) {
                throw new ArrayIndexOutOfBoundsException("That row doesn't exist in this adapter! Desired row: " + row);
            } else {
                int rowLength = 0;
                Log.d(Constants.LOG_TAG, "imagesPerRow: " + imagesPerRow);
                if (indexOfFirst + imagesPerRow > medias.size()) {
                    rowLength = medias.size() - indexOfFirst;
                } else {
                    rowLength = imagesPerRow;
                }
                Media[] mediasArray = new Media[rowLength];
                Log.d(Constants.LOG_TAG, "Row length: " + rowLength);
                Log.d(Constants.LOG_TAG, "Index of first: " + indexOfFirst);
                for (int i = 0; i < rowLength; i++) {
                    mediasArray[i] = medias.get(indexOfFirst + i);
                }
                return mediasArray;
            }
        }

        public String getTitle() {
            return title;
        }

        public int getImageCount() {
            return medias.size();
        }
    }
}
