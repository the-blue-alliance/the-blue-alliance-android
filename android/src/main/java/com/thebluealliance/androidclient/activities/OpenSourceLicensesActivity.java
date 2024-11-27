package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.databinding.ActivityOpenSourceLicensesBinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OpenSourceLicensesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.configureActivityForEdgeToEdge(this);

        ActivityOpenSourceLicensesBinding binding = ActivityOpenSourceLicensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setupActionBar();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.licenses)));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                    line = br.readLine();
                }
                String everything = sb.toString();
                binding.text.setText(Html.fromHtml(everything));
            } finally {
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.text.setText("Error reading licenses file.");
        }
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.license_label);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
