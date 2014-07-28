package com.thebluealliance.androidclient.background.firstlaunch;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.adapters.SingleChoiceListViewAdapter;

import java.util.ArrayList;

/**
 * File created by phil on 7/28/14.
 */
public class LoadAccountPicker extends AsyncTask<Void, Void, Void> {

    private LaunchActivity activity;
    private ListView listView;
    private ArrayList<String> accounts;

    public LoadAccountPicker(LaunchActivity activity, ListView listView) {
        super();
        this.activity = activity;
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        AccountManager manager = (AccountManager) activity.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accountList = manager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

        accounts = new ArrayList<>();
        for (Account account : accountList) {
            accounts.add(account.name);
        }
        accounts.add(activity.getString(R.string.no_account));

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        SingleChoiceListViewAdapter adapter = new SingleChoiceListViewAdapter(activity, accounts);
        listView.setAdapter(adapter);
        activity.setAccounts(accounts);
    }
}
