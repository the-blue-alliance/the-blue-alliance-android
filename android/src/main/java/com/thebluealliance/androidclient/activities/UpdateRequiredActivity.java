package com.thebluealliance.androidclient.activities;

import com.thebluealliance.androidclient.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class UpdateRequiredActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_required);

        findViewById(R.id.update_google_play_button).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient"));
            startActivity(i);
        });
    }

}
