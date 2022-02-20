package com.thebluealliance.androidclient.gcm;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;

import javax.annotation.Nullable;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class TestGCMBroadcastReceiver {

    private Context mApplicationContext;
    private JobScheduler mJobScheduler;

    @Before
    public void setUp() {
        mApplicationContext = ApplicationProvider.getApplicationContext();
        mJobScheduler = (JobScheduler) mApplicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @Test
    public void testSchedulesJob() {
        Intent intent = buildIntent();
        mApplicationContext.sendBroadcast(intent);
        shadowOf(Looper.getMainLooper()).idle();

        @Nullable JobInfo job = mJobScheduler.getPendingJob(GCMMessageHandler.JOB_ID);
        assertNotNull(job);
    }

    private Intent buildIntent() {
        JsonObject notificationData = ModelMaker.getModel(JsonObject.class, "notification_upcoming_match");
        Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
        intent.putExtra("notification_type", "upcoming_match");
        intent.putExtra("message_data", notificationData.toString());
        return intent;
    }
}
