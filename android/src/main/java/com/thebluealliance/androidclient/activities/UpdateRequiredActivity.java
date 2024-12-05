package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;

public class UpdateRequiredActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);

        setContentView(R.layout.activity_update_required);

        findViewById(R.id.update_google_play_button).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient"));
            startActivity(i);
        });
    }

}
