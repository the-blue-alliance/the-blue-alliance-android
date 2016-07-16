package com.thebluealliance.androidclient.datafeed.status;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.activities.UpdateRequiredActivity;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.di.components.DaggerDatafeedComponent;
import com.thebluealliance.androidclient.di.components.DatafeedComponent;
import com.thebluealliance.androidclient.helpers.PitLocationHelper;
import com.thebluealliance.androidclient.models.APIStatus;

import org.greenrobot.eventbus.EventBus;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.WorkerThread;
import com.thebluealliance.androidclient.TbaLogger;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import rx.schedulers.Schedulers;

/**
 * Service to hit the TBA Status endpoint and store the result in SharedPreferences
 */
public class StatusRefreshService extends IntentService {

    @Inject @Named("tba_api") APIv2 mRetrofitAPI;
    @Inject SharedPreferences mPrefs;
    @Inject EventBus mEventBus;
    @Inject OkHttpClient mHttpClient;

    public StatusRefreshService() {
        super("API Status Refresh");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getComponenet().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TbaLogger.d("Updating TBA Status");
        Schedulers.io().createWorker().schedule(this::updateTbaStatus);
    }

    @WorkerThread
    private void updateTbaStatus() {
        Response<APIStatus> response;
        try {
            response = mRetrofitAPI.status().toBlocking().first();
        } catch (Exception ex) {
            TbaLogger.w("Error updating TBA status");
            ex.printStackTrace();
            return;
        }
        if (!response.isSuccessful()) {
            TbaLogger.w("Unable to update myTBA Status\n"
                        + response.code() + " " + response.message());
            return;
        }
        APIStatus status = response.body();

        /* Write the new data to shared prefs */
        mPrefs.edit()
                .putString(TBAStatusController.STATUS_PREF_KEY, status.getJsonBlob())
                .apply();

        /* Post the update to the EventBus */
        mEventBus.post(status);

        /* Update Champs pit locations if necessary */
        if (PitLocationHelper.shouldUpdateFromRemoteUrl(getApplicationContext(), status)) {
            try {
                Request request = new Request.Builder()
                        .url(status.getChampsPitLocationsUrl())
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();

                okhttp3.Response champsPitLocation = mHttpClient.newCall(request).execute();
                String responseString = champsPitLocation.body().string();
                PitLocationHelper.updateFromRemoteUrl(getApplicationContext(), responseString, status.getChampsPitLocationsUpdateTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (BuildConfig.VERSION_CODE < status.getMinAppVersion()) {
            startActivity(new Intent(this, UpdateRequiredActivity.class));
        }

    }

    private DatafeedComponent getComponenet() {
        TBAAndroid application = ((TBAAndroid) getApplication());
        return DaggerDatafeedComponent.builder()
                .applicationComponent(application.getComponent())
                .datafeedModule(application.getDatafeedModule())
                .httpModule(application.getHttpModule())
                .build();
    }
}
