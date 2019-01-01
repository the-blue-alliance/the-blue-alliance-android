package com.thebluealliance.androidclient.listeners;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

/**
 * A class to open a dialog describing an Alliance's Playoff Advancement
 */
public class PlayoffAdvancementClickListener implements View.OnClickListener {

    private final Context mContext;
    private final PlayoffAdvancement mAdvancement;

    public PlayoffAdvancementClickListener(Context context, PlayoffAdvancement advancement) {
        mContext = context;
        mAdvancement = advancement;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.playoff_advancement_title)
                .setMessage(mAdvancement.getDetails())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.cancel();
                });
        builder.create().show();
    }
}
