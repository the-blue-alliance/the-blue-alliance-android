package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.databinding.ActivityConfirmImageSuggestionBinding;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.imgur.ImgurSuggestionService;
import com.thebluealliance.androidclient.imgur.ImgurUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConfirmImageSuggestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_IMAGE_URI = "image_uri";
    private static final String EXTRA_TEAM_KEY = "team_key";
    private static final String EXTRA_YEAR = "year";

    private static final String SAVED_TEMP_FILE_PATH = "saved_file_url";

    private ActivityConfirmImageSuggestionBinding mBinding;

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
        Utilities.configureActivityForEdgeToEdge(this);

        mBinding = ActivityConfirmImageSuggestionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.confirmFab.setOnClickListener(this);
        mBinding.cancelFab.setOnClickListener(this);

        // Disable the "confirm" FAB until we have a valid file to submit
        mBinding.confirmFab.setEnabled(false);

        setSupportActionBar(mBinding.toolbar);
        // TODO don't use hardcoded string
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

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_TEMP_FILE_PATH)) {
            mImageFile = new File(savedInstanceState.getString(SAVED_TEMP_FILE_PATH));
        }

        // Set up the header view, which displays "Team NUMBER (YEAR)"
        mBinding.header.setText(getString(R.string.imgur_confirm_image_header, Integer.toString(TeamHelper.getTeamNumber(mTeamKey)), Integer.toString(mYear)));

        // Don't begin caching and loading the image until layout is complete; the ImageView must
        // have a defined width and height in order to compute inSampleSize for loading the bitmap
        ViewTreeObserver vto = mBinding.image.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                cacheAndLoadImage();
            }
        });
    }

    /**
     * Loads the image from {@code mUri} into our cache directory and displays it in this activity.
     * The caching part is important because the image at {@code mUri} is not guaranteed to be in
     * local storage; for instance, it could be an image that needs to be loaded from Google Drive.
     * We need to store it in a local file so that {@link ImgurSuggestionService}
     * can upload it properly.
     * <p>
     * This should not be called until after initial layout is complete; loading the Bitmap into
     * memory efficiently requires that we know how big the target ImageView so we can scale it
     * properly during the decoding process.
     */
    private void cacheAndLoadImage() {
        Observable<File> fileObservable;

        if (mImageFile != null) {
            fileObservable = Observable.just(mImageFile);
        } else {
            fileObservable = Observable.just(mUri).map((uri) -> {
                mImageFile = ImgurUtils.createFile(uri, ConfirmImageSuggestionActivity.this);
                if (mImageFile == null) {
                    // TODO error handling!
                    TbaLogger.e("Image was null!");
                }
                return mImageFile;
            });
        }

        fileObservable.map((file) -> {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                InputStream stream = new BufferedInputStream(new FileInputStream(file));
                BitmapFactory.decodeStream(stream, null, options);
                stream.close();

                options.inSampleSize = calculateInSampleSize(options, mBinding.image.getWidth(), mBinding.image.getHeight());

                options.inJustDecodeBounds = false;
                stream = new BufferedInputStream(new FileInputStream(file));
                Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
                stream.close();
                return bitmap;
            } catch (IOException e) {
                throw new RuntimeException("Error reading bitmap");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    mBinding.image.setImageBitmap(bitmap);

                    // Fade ImageView in and ProgressBar out
                    mBinding.image.setAlpha(0.0f);
                    mBinding.image.animate().alpha(1.0f).setDuration(500).start();
                    mBinding.progress.setAlpha(1.0f);
                    mBinding.progress.animate().alpha(0.0f).setDuration(500).start();

                    mBinding.confirmFab.setEnabled(true);
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mImageFile != null) {
            outState.putString(SAVED_TEMP_FILE_PATH, mImageFile.getAbsolutePath());
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_fab:
                Toast.makeText(this, "Your image will be uploaded in the background!", Toast.LENGTH_SHORT).show();
                startService(ImgurSuggestionService.newIntent(this, mImageFile.getAbsolutePath(), mTeamKey, mYear));
                this.finish();
                break;
            case R.id.cancel_fab:
                Toast.makeText(this, "Submission cancelled", Toast.LENGTH_SHORT).show();
                // Delete the cached image file to free up storage space
                if (mImageFile != null) {
                    mImageFile.delete();
                }
                this.finish();
                break;
        }
    }
}
