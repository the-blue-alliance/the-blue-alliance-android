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

    /**
     * The number of images in each row of the list
     */
    private int mImagesPerRow = -1;

    /**
     * Size of each side of the image, in pixels
     */
    private int mImageSize = -1;

    /**
     * Width of the dividers between images, in pixels
     */
    private int mDividerWidth;

    private Context mContext;

    /**
     * Constructs an adapter from the given parameters.
     *
     * @param context  A context for use in the adapter
     * @param listView The ListView that the adapter will be added to (used for determining dimensions)
     * @param sections An ArrayList of sections, each composed of a title and a number of Media objects
     */
    public SectionedImageListAdapter(Context context, ListView listView, ArrayList<Section> sections) {
        mContext = context;
        mSections = sections;

        // Get the width of the ListView that will be used to show this adapter's contents
        int listViewWidth = listView.getWidth();

        // Retrieve and store some resources
        int minImageWidth = mContext.getResources().getDimensionPixelSize(R.dimen.sectioned_image_list_image_min_width);
        mDividerWidth = mContext.getResources().getDimensionPixelSize(R.dimen.sectioned_image_list_divider_width);

        // Calculate number of items that can fit horizontally in a row
        int numChildrenPerRow = listViewWidth / minImageWidth;

        // Calculate the width of each image by increasing the minimum width of the image until
        // the sum of the widths of all the images and dividers is equal to or greater than the
        // width of the containing view.
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
        // rows per section.
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
        int currentRow = -1;
        int sectionNum = -1;
        int sectionRow = -1;
        boolean header = false;
        boolean foundRow = false;
        // Iterate through each of the sections
        for (Section section : mSections) {
            currentRow++;
            sectionNum++;
            // If the first row in the section is the desired position, this is a header row.
            if (currentRow == position) {
                header = true;
                break;
            } else {
                // Iterate through the rows of the section to see if the position we want is in here.
                // New section; reset the position of the row within the section
                sectionRow = -1;
                for (int i = 0; i < section.getImageRowCount(mImagesPerRow); i++) {
                    currentRow++;
                    sectionRow++;
                    if (currentRow == position) {
                        header = false;
                        foundRow = true;
                        break;
                    }
                }
                if (foundRow) {
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
            // This is a row of images. Construct a layout to hold the images.
            v = inflater.inflate(R.layout.image_row, null);
            Media[] medias = mSections.get(sectionNum).getMediasForRow(sectionRow, mImagesPerRow);
            Picasso picasso = Picasso.with(mContext);
            for (int i = 0; i < medias.length; i++) {
                ImageView image = new ImageView(mContext);
                // These two are required to make the images clickable
                image.setClickable(true);
                image.setFocusable(true);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mImageSize, mImageSize);
                // If the image is the farthest to the right in this row, don't put space on the left side.
                // Similarly, if the image is in the last row of the section, don't put space below it.
                lp.setMargins(0, 0, i != medias.length - 1 ? mDividerWidth : 0, mSections.get(sectionNum).isLastRowInSection(sectionRow, mImagesPerRow) ? 0 : mDividerWidth);
                ((LinearLayout) v).addView(image, i, lp);
                picasso.load(medias[i].getMediaURL()).into(image);
                // This will associate an analytics tracker and a click handler with this view.
                bindMediaToView(image, medias[i]);
            }
            return v;
        }
    }

    /**
     * Attaches a click listener to the specified view with the URL from the given Media. This will also
     * report the click to Analytics.
     *
     * @param v the view to attach the click listener to
     * @param media the media used to construct the intent that will be fired on a click
     */
    private void bindMediaToView(View v, final Media media) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Track Click
                Tracker t = Analytics.getTracker(Analytics.GAnalyticsTracker.ANDROID_TRACKER, mContext);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("media_click")
                        .setAction(media.getMediaURL())
                        .setLabel(media.isVideo() ? "video" : "cd_photo")
                        .build());

                mContext.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(media.getMediaURL())));
            }
        });
    }

    /**
     * Represents a section in the adapter. A section is composed of a title and a number of Media objects,
     * which are displayed in a grid below the title.
     */
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

        public boolean isLastRowInSection(int row, int imagesPerRow) {
            if (getImageRowCount(imagesPerRow) - 1 == row) {
                return true;
            }
            return false;
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
