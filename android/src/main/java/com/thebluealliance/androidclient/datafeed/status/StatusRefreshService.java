package com.thebluealliance.androidclient.datafeed.status;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.TbaAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.activities.UpdateRequiredActivity;
import com.thebluealliance.androidclient.api.rx.TbaApiV3;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.di.components.DaggerDatafeedComponent;
import com.thebluealliance.androidclient.di.components.DatafeedComponent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.PitLocationHelper;
import com.thebluealliance.androidclient.models.ApiStatus;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.JobIntentService;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import rx.schedulers.Schedulers;

/**
 * Service to hit the TBA Status endpoint and store the result in SharedPreferences
 */
public class StatusRefreshService extends JobIntentService {

    public static final int JOB_ID = 148;

    @Inject
    @Named("tba_apiv3_rx")
    TbaApiV3 mRetrofitAPI;
    @Inject
    SharedPreferences mPrefs;
    @Inject
    EventBus mEventBus;
    @Inject
    OkHttpClient mHttpClient;
    @Inject
    AppConfig mAppConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
    }

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

        Response<ApiStatus> response;
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
        ApiStatus status = response.body();

        /* Write the new data to shared prefs */
        mPrefs.edit()
                .putString(TBAStatusController.STATUS_PREF_KEY, status.getJsonBlob())
                .apply();

        /* Post the update to the EventBus */
        mEventBus.post(status);

        /* Update Champs pit locations if necessary */
        PitLocationHelper.updateRemoteDataIfNeeded(getApplicationContext(), mAppConfig, mHttpClient);

        if (status.getMinAppVersion() != null
                && BuildConfig.VERSION_CODE < status.getMinAppVersion()) {
            startActivity(new Intent(this, UpdateRequiredActivity.class));
        }

    }

    private DatafeedComponent getComponenet() {
        TbaAndroid application = ((TbaAndroid) getApplication());
        return DaggerDatafeedComponent.builder()
                .applicationComponent(application.getComponent())
                .datafeedModule(application.getDatafeedModule())
                .httpModule(application.getHttpModule())
                .build();
    }
}
