package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.listitems.ListElement;

import javax.inject.Inject;

public class ContributorClickListener implements AdapterView.OnItemClickListener {

    private Context mContext;

    @Inject
    public ContributorClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String login = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
        String url = "https://github.com/" + login;
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
