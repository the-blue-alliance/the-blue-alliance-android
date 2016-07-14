package com.thebluealliance.androidclient.imgur;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.appspot.tbatv_prod_hrd.TeamMedia;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesMediaSuggestionMessage;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.datafeed.gce.GceAuthController;
import com.thebluealliance.androidclient.di.components.DaggerSuggestionComponent;
import com.thebluealliance.androidclient.di.components.SuggestionComponent;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.imgur.ImgurApi;
import com.thebluealliance.imgur.responses.UploadResponse;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import com.thebluealliance.androidclient.Log;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

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

    private static final String TEAM_LINK = "team";
    private static final String DELETEHASH_KEY = "deletehash";

    @Inject ImgurApi mImgurApi;
    @Inject TeamMedia mTeamMediaApi;
    @Inject GceAuthController mGceAuthController;

    /**
     * Create a new Intent pass to {@link Context#startService(Intent)} that uploads the given image
     * file to imgur and suggests it to The Blue Alliance.
     *
     * @param context     Context to create the intent with
     * @param filepath    Local filepath of the image file to suggest
     * @param title       Title for the image on imgur
     * @param description Description for the image on imgur
     * @param teamKey     Team to suggest the image with
     * @param year        Year to suggest the image for, along with teamKey
     * @return An Intent to pass to {@link Context#startService(Intent)}
     */
    public static Intent newIntent(Context context,
                                   String filepath,
                                   String title,
                                   String description,
                                   String teamKey,
                                   int year) {
        Intent intent = new Intent(context, ImgurSuggestionService.class);
        intent.putExtra(EXTRA_FILEPATH, filepath);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_TEAMKEY, teamKey);
        intent.putExtra(EXTRA_YEAR, year);

        return intent;
    }

    /**
     * Create a new Intent pass to {@link Context#startService(Intent)} that uploads the given image
     * file to imgur and suggests it to The Blue Alliance. This method will automatically generate
     * a title and description for the image; useful if the user didn't specify any.
     *
     * @param context     Context to create the intent with
     * @param filepath    Local filepath of the image file to suggest
     * @param teamKey     Team to suggest the image with
     * @param year        Year to suggest the image for, along with teamKey
     * @return An Intent to pass to {@link Context#startService(Intent)}
     */
    public static Intent newIntent(Context context,
                                   String filepath,
                                   String teamKey,
                                   int year) {
        Intent intent = new Intent(context, ImgurSuggestionService.class);
        intent.putExtra(EXTRA_FILEPATH, filepath);
        intent.putExtra(EXTRA_TITLE, "Team " + TeamHelper.getTeamNumber(teamKey) + " (" + year + ")");
        intent.putExtra(EXTRA_DESCRIPTION, "Uploaded from The Blue Alliance Android app");
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
        int year = intent.getIntExtra(EXTRA_YEAR, 0);

        ImgurUploadNotification notification = new ImgurUploadNotification(getApplicationContext());
        notification.onUploadStarting();

        boolean successful = true;

        // Get a wake lock so any long uploads will not be interrupted
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.acquire();

        // Catch-all error handling for now
        // TODO should we catch each exception individually and handle them better (e.g. retyr?)
        try {
            String authToken = getAuthHeader(getApplicationContext());
            File file = new File(filepath);
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);

            Response<UploadResponse> response = mImgurApi.uploadImage(authToken, titlePart, descPart, body).execute();

            if (response != null && response.isSuccessful()) {
                UploadResponse uploadResponse = response.body();
                Log.d(Constants.LOG_TAG, "Uploaded imgur image: " + uploadResponse.data.link);

                String link = uploadResponse.data.link;
                String deletehash = uploadResponse.data.deletehash;

                Log.d(Constants.LOG_TAG, "Imgur link: " + link);

                // Do suggestion
                String authHeader = mGceAuthController.getAuthHeader();
                ModelsMobileApiMessagesMediaSuggestionMessage message = buildSuggestionMessage(
                        teamKey,
                        year,
                        link,
                        deletehash);
                Response<ModelsMobileApiMessagesBaseResponse> suggestionResponse = mTeamMediaApi.suggestion(authHeader, message).execute();
                if (suggestionResponse != null && suggestionResponse.isSuccessful()) {
                    // Yay, everything worked!
                } else {
                    // Crap
                    // TODO handle this
                    successful = false;
                }
            } else {
                Log.e(Constants.LOG_TAG, "Error uploading imgur image\n"
                        + response.code() + " " + response.message());
                successful = false;
            }
        } catch (Exception e) {
            // Something broke
            successful = false;
            e.printStackTrace();
        } finally {
            if (successful) {
                notification.onUploadSuccess();
            } else {
                notification.onUploadFailure();
            }

            // Delete the temp cached image
            new File(filepath).delete();

            wakeLock.release();
        }
    }

    private SuggestionComponent getComponent() {
        TBAAndroid application = (TBAAndroid) getApplication();
        return DaggerSuggestionComponent.builder()
                .applicationComponent(application.getComponent())
                .gceModule(application.getGceModule())
                .httpModule(application.getHttpModule())
                .imgurModule(application.getImgurModule())
                .tBAAndroidModule(application.getModule())
                .build();
    }

    private static String getAuthHeader(Context context) {
        String clientId = Utilities.readLocalProperty(context, "imgur.clientId");
        return String.format("Client-ID %1$s", clientId);
    }

    private static ModelsMobileApiMessagesMediaSuggestionMessage buildSuggestionMessage(
            String teamKey,
            int year,
            String imgurLink,
            String deleteHash) {
        ModelsMobileApiMessagesMediaSuggestionMessage message =
                new ModelsMobileApiMessagesMediaSuggestionMessage();
        message.media_url = imgurLink;
        message.reference_key = teamKey;
        message.year = year;
        message.reference_type = TEAM_LINK;
        message.details_json = getDetailsJson(deleteHash);
        return message;
    }

    private static String getDetailsJson(String deletehash) {
        JsonObject json = new JsonObject();
        json.add(DELETEHASH_KEY, new JsonPrimitive(deletehash));
        return json.toString();
    }
}
