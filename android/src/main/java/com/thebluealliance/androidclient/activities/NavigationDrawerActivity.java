package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.widget.FrameLayout;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.NavDrawerItem;
import com.thebluealliance.androidclient.fragments.NavigationDrawerFragment;

/**
 * Activity that provides a navigation drawer.
 * <p/>
 * This allows for the easy reuse of a single navigation drawer throughout the app.
 * <p/>
 * Created by Nathan on 5/15/2014.
 */
public abstract class NavigationDrawerActivity extends FragmentActivity implements NavigationDrawerFragment.OnNavigationDrawerListener {

    private NavigationDrawerFragment mNavDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mContentView;

    private boolean mUseActionBarToggle = false;

    /**
     * Tells the activity whether or not to use the action bar toggle for
     * the navigation drawer.
     *
     * @param use True if this activity should use the action bar toggle
     */
    public void useActionBarToggle(boolean use) {
        mUseActionBarToggle = use;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_start);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);

        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment,
                (DrawerLayout) findViewById(R.id.nav_drawer_layout),
                true, mUseActionBarToggle);
        mContentView = (FrameLayout) findViewById(R.id.container);
    }

    /**
     * Inflates the specified view into the "content container" of the activity.
     * This allows the resuse of a single layout containing a navigation drawer and said container
     * across all instances of this activity. Subclassing activities that call setContentView(...)
     * will have their requested layout inserted into the content container.
     *
     * @param layoutResID id of the view to be inflated into the content container
     */
    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, mContentView);
    }

    /**
     * Provides a default implementation of item click handling that simply opens the
     * specified mode in a StartActivity that is inserted into the back stack. Children
     * classes can override this if they want to enable custom handling of click events.
     * If children override this method, they should <strong>not</strong> call through to
     * this method.
     *
     * @param item The item that was clicked
     */
    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        int id = item.getId();

        // Open settings in the foreground
        if (id == R.id.nav_item_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        }

        /*
         * We manually add the start activity to the back stack so that we maintain proper
         * back button functionality and so we get the proper "activity finish" animation
         */
        TaskStackBuilder.create(this).addNextIntent(StartActivity.newInstance(this, id)).startActivities();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isDrawerOpen()) {
            getActionBar().setTitle(R.string.app_name);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * Allows the navigation drawer to be enabled or disabled.
     *
     * @param enabled true if the navigation drawer should be enabled
     */
    public void setNavigationDrawerEnabled(boolean enabled) {
        if (enabled) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.closeDrawers();
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /**
     * Shows the specified item in the navigation drawer with a highlight.
     *
     * @param position index of the item to be selected
     */
    public void setNavigationDrawerItemSelected(int position) {
        mNavDrawerFragment.setItemSelected(position);
    }

    /**
     * Check if the navigation drawer is visible
     *
     * @return true if the drawer is open
     */
    public boolean isDrawerOpen() {
        return mNavDrawerFragment.isDrawerOpen();
    }
}
