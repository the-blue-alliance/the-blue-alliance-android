package com.thebluealliance.androidclient.accounts;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.widget.Toast;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 8/2/14.
 */
public class UserFavorite extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private MenuItem icon;

    public UserFavorite(Context context, MenuItem icon) {
        this.context = context;
        this.icon = icon;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        icon.setActionView(R.layout.actionbar_indeterminate_progress);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        icon.setActionView(null);
        String text;
        if (result) {
            icon.setIcon(R.drawable.ic_action_remove_favorite);
            text = "Favorite added";
        } else {
            text = "Error adding favorite";
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
