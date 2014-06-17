package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;

/**
 * Provides the features that should be in every activity in the app: a navigation drawer,
 * a search button, and the ability to show and hide warning messages.
 */
public abstract class BaseActivity extends NavigationDrawerActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract void showWarningMessage(String message);

    public abstract void hideWarningMessage();
}
