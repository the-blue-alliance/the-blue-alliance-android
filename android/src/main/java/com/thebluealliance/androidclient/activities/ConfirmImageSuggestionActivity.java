package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.imgur.ImgurUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConfirmImageSuggestionActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE_URI = "image_uri";
    private static final String EXTRA_TEAM_KEY = "team_key";
    private static final String EXTRA_YEAR = "year";

    @Bind(R.id.image) ImageView mImageView;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private Uri mUri;
    private String mTeamKey;
    private int mYear;

    private File mImageFile;

    public static Intent newIntent(Context context, Uri imageUri, String teamKey, int year) {
        Bundle extras = new Bundle();
        extras.putParcelable(EXTRA_IMAGE_URI, imageUri);
        extras.putString(EXTRA_TEAM_KEY, teamKey);
        extras.putInt(EXTRA_YEAR, year);
        Intent intent = new Intent(context, ConfirmImageSuggestionActivity.class);
        intent.putExtras(extras);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm_image_suggestion);
        ButterKnife.bind(this);

        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbar.setContentInsetsRelative(0, 0);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Confirm suggestion");

        Bundle extras = getIntent().getExtras() == null ? new Bundle() : getIntent().getExtras();
        if (!extras.containsKey(EXTRA_IMAGE_URI)
                || !extras.containsKey(EXTRA_TEAM_KEY)
                || !extras.containsKey(EXTRA_YEAR)) {
            throw new IllegalArgumentException("ConfirmImageSuggestionActivity is missing required extras");
        }

        mUri = extras.getParcelable(EXTRA_IMAGE_URI);
        mTeamKey = extras.getString(EXTRA_TEAM_KEY);
        mYear = extras.getInt(EXTRA_YEAR);

        // Validate intent extras
        if (!TeamHelper.validateTeamKey(mTeamKey)) {
            throw new IllegalArgumentException("Invalid team key!");
        }
        if (mUri == null) {
            throw new IllegalArgumentException("URI is null!");
        }

        // We'll fetch and decode the image in a background thread to keep things responsive
        // This will also write the file to our cache directory so that we can pass it to the
        // upload service. We can't pass the URI directly.
        Observable.just(mUri).map((uri) -> {
            mImageFile = ImgurUtils.createFile(uri, ConfirmImageSuggestionActivity.this);
            if (mImageFile == null) {
                // TODO error handling!
                Log.e(Constants.LOG_TAG, "Image was null!");
            }
            return mImageFile;
        }).map((file) -> {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                FileInputStream stream = new FileInputStream(file);
                BitmapFactory.decodeStream(stream, null, options);
                stream.close();
                stream = null;

                options.inSampleSize = calculateInSampleSize(options, mImageView.getWidth(), mImageView.getHeight());

                options.inJustDecodeBounds = false;
                stream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
                stream.close();
                return bitmap;
            } catch (IOException e) {
                throw new RuntimeException("Error reading bitmap");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
            mImageView.setImageBitmap(bitmap);
        });
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
