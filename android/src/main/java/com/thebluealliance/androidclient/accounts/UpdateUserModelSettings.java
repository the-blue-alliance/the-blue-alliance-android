package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.helpers.ModelNotificationFavoriteSettings;
import com.thebluealliance.androidclient.interfaces.ModelSettingsCallbacks;
import com.thebluealliance.androidclient.mytba.ModelPrefsResult;

import java.lang.ref.WeakReference;

public class UpdateUserModelSettings extends AsyncTask<String, Void, ModelPrefsResult> {

    private final Context mContext;
    private final ModelNotificationFavoriteSettings mSettings;
    private final MyTbaDatafeed mMyTbaDatafeed;

    // We use a WeakReference so that the Activity can be garbage-collected if need be.
    private WeakReference<ModelSettingsCallbacks> callbacks;

    public UpdateUserModelSettings(Context context,
                                   MyTbaDatafeed myTbaDatafeed,
                                   ModelNotificationFavoriteSettings settings) {
        mContext = context;
        mSettings = settings;
        mMyTbaDatafeed = myTbaDatafeed;
    }

    public void setCallbacks(ModelSettingsCallbacks callbacks) {
        this.callbacks = new WeakReference<>(callbacks);
    }

    @Override
    protected ModelPrefsResult doInBackground(String... params) {
        return mMyTbaDatafeed.updateModelSettings(mContext, mSettings);
    }

    @Override
    protected void onPostExecute(ModelPrefsResult result) {
        super.onPostExecute(result);

        if (callbacks.get() != null) {
            ModelSettingsCallbacks cb = callbacks.get();
            switch (result) {
                case SUCCESS:
                    cb.onSuccess();
                    break;
                case NOOP:
                    cb.onNoOp();
                    break;
                case ERROR:
                    cb.onError();
                    break;
            }
        } else {
            Toast.makeText(mContext, "Callbacks were null", Toast.LENGTH_SHORT).show();
        }
    }
}
