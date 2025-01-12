package com.thebluealliance.androidclient.datafeed.status;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.activities.UpdateRequiredActivity;
import com.thebluealliance.androidclient.api.rx.TbaApiV3;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.PitLocationHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.JobIntentService;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import rx.schedulers.Schedulers;
import thebluealliance.api.model.APIStatus;

/**
 * Service to hit the TBA Status endpoint and store the result in SharedPreferences
 */
@AndroidEntryPoint
public class StatusRefreshService extends JobIntentService {

    public static final int JOB_ID = 148;

    @Inject
    @Named("tba_apiv3_rx")
    TbaApiV3 mRetrofitAPI;

    @Inject
    Gson mGson;

    @Inject
    SharedPreferences mPrefs;
    @Inject
    EventBus mEventBus;
    @Inject
    OkHttpClient mHttpClient;
    @Inject
    AppConfig mAppConfig;

    public static void enqueueWork(Context context) {
        enqueueWork(context, StatusRefreshService.class, JOB_ID, new Intent(context, StatusRefreshService.class));
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        TbaLogger.d("Updating TBA Status");
        Schedulers.io().createWorker().schedule(this::updateTbaStatus);
    }

    @WorkerThread
    private void updateTbaStatus() {

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            return;
        }

        /* Updating FirebaseRemoteConfig */
        try {
            mAppConfig.updateRemoteDataBlocking();
        } catch (ExecutionException | InterruptedException e) {
            TbaLogger.w("Error updating FirebaseRemoteConfig: " + e.getMessage(), e);
        }

        Response<APIStatus> response;
        try {
            response = mRetrofitAPI.fetchApiStatus().toBlocking().first();
        } catch (Exception ex) {
            TbaLogger.w("Error updating TBA status: " + ex.getMessage(), ex);
            return;
        }
        if (!response.isSuccessful()) {
            TbaLogger.w("Unable to update myTBA Status\n"
                    + response.code() + " " + response.message());
            return;
        }
        APIStatus status = response.body();

        /* Write the new data to shared prefs */
        String statusJson = mGson.toJson(status);
        mPrefs.edit()
                .putString(TBAStatusController.STATUS_PREF_KEY, statusJson)
                .apply();

        /* Post the update to the EventBus */
        mEventBus.post(status);

        /* Update Champs pit locations if necessary */
        PitLocationHelper.updateRemoteDataIfNeeded(getApplicationContext(), mAppConfig, mHttpClient);

        if (status != null && BuildConfig.VERSION_CODE < status.getAndroid().getMinAppVersion()) {
            Intent newIntent = new Intent(this, UpdateRequiredActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        }

    }
}
