package com.thebluealliance.androidclient.imgur;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.datafeed.gce.TbaSuggestionController;
import com.thebluealliance.androidclient.di.components.DaggerSuggestionComponent;
import com.thebluealliance.androidclient.di.components.SuggestionComponent;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

/**
 * An {@link IntentService} that takes details about an image stored locally, uploads it to imgur,
 * and then posts it as a suggestion to TBA
 */
public class ImgurSuggestionService extends IntentService {

    public static final String EXTRA_FILEPATH = "filepath";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_TEAMKEY = "teamKey";
    public static final String EXTRA_YEAR = "year";

    @Inject ImgurController mImgurController;
    @Inject TbaSuggestionController mSuggestionController;
    @Inject NotificationManager mNotificationManager;

    /**
     * Create a new Intent pass to {@link Context#startService(Intent)} that uploads the given image
     * file to imgur
     * @param context Context to create the intent with
     * @param filepath Local filepath of the image file to suggest
     * @param title Title for the image on imgur
     * @param description Description for the image on imgur
     * @param teamKey Team to suggest the image with
     * @param year Year to suggest the image for, along with teamKey
     * @return An Intent to pass to {@link Context#startService(Intent)}
     */
    public static Intent newIntent(Context context,
                                   Uri filepath,
                                   String title,
                                   String description,
                                   String teamKey,
                                   int year) {
        Intent intent = new Intent(context, ImgurSuggestionService.class);
        intent.putExtra(EXTRA_FILEPATH, filepath.toString());
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_TEAMKEY, teamKey);
        intent.putExtra(EXTRA_YEAR, year);

        return intent;
    }

    /**
     * Creates an ImgurSuggestionService
     */
    public ImgurSuggestionService() {
        super("imgurSuggestion");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(Constants.LOG_TAG, "IMGUR SERVICE START");
        String filepath = intent.getStringExtra(EXTRA_FILEPATH);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);
        String teamKey = intent.getStringExtra(EXTRA_TEAMKEY);
        int year = intent.getIntExtra(EXTRA_YEAR,0);
        ImgurSuggestionCallback callback = new ImgurSuggestionCallback(
                mSuggestionController,
                "frc1124", //teamKey,
                2016); //year);

        try {
            mImgurController.uploadImage(getApplicationContext(), filepath, title, description, callback);
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "IO Exception while uploading to imgur");
            e.printStackTrace();
        }
    }

    private SuggestionComponent getComponent() {
        TBAAndroid application = (TBAAndroid)getApplication();
        return DaggerSuggestionComponent.builder()
                .applicationComponent(application.getComponent())
                .gceModule(application.getGceModule())
                .httpModule(application.getHttpModule())
                .imgurModule(application.getImgurModule())
                .tBAAndroidModule(application.getModule())
                .build();
    }
}
