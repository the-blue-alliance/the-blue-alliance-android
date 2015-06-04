package com.thebluealliance.androidclient.background.mytba;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesRegistrationRequest;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.accounts.PlusHelper;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.gcm.GCMAuthHelper;

import java.io.IOException;

/**
 * File created by phil on 8/29/14.
 */
public class DisableMyTBA extends AsyncTask<String, Void, Void> {

    private Context context;

    public DisableMyTBA(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String user = params[0];

        Database.Favorites favorites = Database.getInstance(context).getmFavoritesTable();
        favorites.recreate(user);

        Database.Subscriptions subscriptions = Database.getInstance(context).getSubscriptionsTable();
        subscriptions.recreate(user);

        ModelsMobileApiMessagesRegistrationRequest request = new ModelsMobileApiMessagesRegistrationRequest();
        request.setMobileId(GCMAuthHelper.getRegistrationId(context));
        request.setOperatingSystem(GCMAuthHelper.OS_ANDROID);
        request.setDeviceUuid(Utilities.getDeviceUUID(context));

        TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
        if (service == null) {
            Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
            Handler mainHandler = new Handler(context.getMainLooper());
            mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show());
            return null;
        }
        try {
            ModelsMobileApiMessagesBaseResponse response = service.unregister(request).execute();
            if (response.getCode() == 200) {
                Log.i(Constants.LOG_TAG, "Unregistered from GCM");
                // Remove the GCM ID so it'll be fetched anew if myTBA is re-enabled
                AccountHelper.setSelectedAccount(context, "");
                GCMAuthHelper.storeRegistrationId(context, "");
                if (PlusHelper.isConnected()) {
                    PlusHelper.disconnect();
                }
            } else {
                Log.e(Constants.LOG_TAG, "error unregistering from gcm: " + response.getMessage());
            }
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "unable to unregister");
            e.printStackTrace();
        }

        return null;
    }
}
