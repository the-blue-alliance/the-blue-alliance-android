package com.thebluealliance.androidclient.datafeed.status;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.activities.UpdateRequiredActivity;
import com.thebluealliance.androidclient.datafeed.retrofit.APIv2;
import com.thebluealliance.androidclient.di.components.DaggerDatafeedComponent;
import com.thebluealliance.androidclient.di.components.DatafeedComponent;
import com.thebluealliance.androidclient.models.APIStatus;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;
import retrofit.Response;
import rx.schedulers.Schedulers;

/**
 * Service to hit the TBA Status endpoint and store the result in SharedPreferences
 */
public class StatusRefreshService extends IntentService {

    @Inject @Named("retrofit") APIv2 mRetrofitAPI;
    @Inject SharedPreferences mPrefs;
    @Inject EventBus mEventBus;

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
        Schedulers.io().createWorker().schedule(this::updateTbaStatus);
    }

    @WorkerThread
    private void updateTbaStatus() {
        Response<APIStatus> response = mRetrofitAPI.status().toBlocking().first();
        if (!response.isSuccess()) {
            Log.w(Constants.LOG_TAG, "Unable to update myTBA Status\n"+
              response.code() + " " +response.message());
            return;
        }
        APIStatus status = response.body();

        /* Write the new data to shared prefs */
        mPrefs.edit()
          .putString(TBAStatusController.STATUS_PREF_KEY, status.getJsonBlob())
          .apply();

        /* Post the update to the EventBus */
        mEventBus.post(status);

        if (BuildConfig.VERSION_CODE < status.getMinAppVersion()) {
            startActivity(new Intent(this, UpdateRequiredActivity.class));
        }
    }

    private DatafeedComponent getComponenet() {
            TBAAndroid application = ((TBAAndroid) getApplication());
            return DaggerDatafeedComponent.builder()
              .applicationComponent(application.getComponent())
              .datafeedModule(application.getDatafeedModule())
              .build();
    }
}
