package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.drawerlayout.widget.DrawerLayout;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.settings.SettingsActivity;
import com.thebluealliance.androidclient.fragments.NavigationDrawerFragment;
import com.thebluealliance.androidclient.listitems.NavDrawerItem;
import com.thebluealliance.androidclient.views.ScrimInsetsFrameLayout;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity that provides a navigation drawer.
 * <p>
 * This allows for the easy reuse of a single navigation drawer throughout the app.
 */
@AndroidEntryPoint
public abstract class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerListener {

    private static final String IS_DRAWER_OPEN = "is_drawer_open";

    protected static final int DRAWER_CLOSE_ANIMATION_DURATION = 600;

    private NavigationDrawerFragment mNavDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mContentView;
    private ScrimInsetsFrameLayout mDrawerContainer;

    private String mActionBarTitle;

    private boolean mUseActionBarToggle = false;
    private boolean mEncourageLearning = false;

    protected Handler handler;

    /**
     * Tells the activity whether or not to use the action bar toggle for the navigation drawer.
     *
     * @param use True if this activity should use the action bar toggle
     */
    public void useActionBarToggle(boolean use) {
        mUseActionBarToggle = use;
    }

    /**
     * Tells the activity whether or not to use the action bar toggle for the navigation drawer.
     *
     * @param encourage True if this activity should use the action bar toggle
     */
    public void encourageLearning(boolean encourage) {
        mEncourageLearning = encourage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_navigation_drawer);

        Utilities.configureActivityForEdgeToEdge(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        mContentView = (FrameLayout) findViewById(R.id.content);
        mDrawerLayout.setStatusBarBackground(R.color.primary);
        mDrawerContainer = (ScrimInsetsFrameLayout) findViewById(R.id.navigation_drawer_fragment_container);

        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNavDrawerFragment != null) {
            mNavDrawerFragment.setupNavDrawerHeader();
        }
    }

    /**
     * We set up the nav drawer here to give child activities a chance to set the window's Action
     * Bar if they're using a Toolbar instead of the default Action Bar.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Call this so that subclasses can configure the navigation drawer before it is created
        onCreateNavigationDrawer();

        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment_container,
                (DrawerLayout) findViewById(R.id.nav_drawer_layout),
                mEncourageLearning, mUseActionBarToggle);
        mDrawerContainer.setOnInsetsCallback(insets -> mNavDrawerFragment.onInsetsChanged(insets));

        // Restore the state of the navigation drawer on rotation changes
        if (savedInstanceState != null && savedInstanceState.getBoolean(IS_DRAWER_OPEN, false)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }

        onNavigationDrawerCreated();
    }

    /**
     * Called before the navigation drawer is created. Subclasses can override this to perform
     * setup before the navigation drawer is created, such as enabling or disabling it or disabling
     * the action bar toggle. Subclasses do not have to call through to super.
     */
    public void onCreateNavigationDrawer() {
        // Default implementation is empty
    }

    /**
     * Called after the notification drawer is created. Allows subclasses to override this and
     * configure the navigation drawer.
     */
    public void onNavigationDrawerCreated() {
        // Default implementation is empty
    }

    /**
     * Inflates the specified view into the "content container" of the activity. This allows the
     * resuse of a single layout containing a navigation drawer and said container across all
     * instances of this activity. Subclassing activities that call setContentView(...) will have
     * their requested layout inserted into the content container.
     *
     * @param layoutResID id of the view to be inflated into the content container
     */
    @Override
    public void setContentView(int layoutResID) {
        mContentView.removeAllViews();
        getLayoutInflater().inflate(layoutResID, mContentView);
    }

    /**
     * Provides a default implementation of item click handling that simply opens the specified
     * mode in a StartActivity that is inserted into the back stack. Children classes can override
     * this if they want to enable custom handling of click events. If children override this
     * method, they should <strong>not</strong> call through to this method.
     *
     * @param item The item that was clicked
     */
    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        final int id = item.getId();

        final Intent intent;
        switch (id) {
            case R.id.nav_item_settings:
                intent = new Intent(NavigationDrawerActivity.this, SettingsActivity.class);
                break;
            default:
                intent = null;
                break;
        }
        // Launch after a short delay to give the drawer time to close.
        if (intent != null) {
            handler.postDelayed(() -> startActivity(intent), DRAWER_CLOSE_ANIMATION_DURATION);
        }

        /*
         * We manually add the start activity to the back stack so that we maintain proper
         * back button functionality and so we get the proper "activity finish" animation.
         *
         * Launch after a short delay to give the drawer time to close.
         */

        handler.postDelayed(() -> TaskStackBuilder.create(NavigationDrawerActivity.this).addNextIntent(HomeActivity.newInstance(NavigationDrawerActivity.this, id)).startActivities(), DRAWER_CLOSE_ANIMATION_DURATION);
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
     * @param itemId ID of the item to be selected
     */
    public void setNavigationDrawerItemSelected(int itemId) {
        mNavDrawerFragment.setItemSelected(itemId);
    }

    /**
     * Check if the navigation drawer is visible
     *
     * @return true if the drawer is open
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    /**
     * Closes the navigation drawer
     */
    public void closeDrawer() {
        getDrawerLayout().closeDrawer(Gravity.LEFT);
    }

    /**
     * Opens the navigation drawer
     */
    public void openDrawer() {
        getDrawerLayout().openDrawer(Gravity.LEFT);
    }

    /**
     * Allows access to the DrawerLayout that this activity hosts
     *
     * @return the DrawerLayout of this activity
     */
    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    /**
     * Allows access to the DrawerFragment that this activity hosts
     *
     * @return the DrawerFragment of this activity
     */
    public NavigationDrawerFragment getDrawerFragment() {
        return mNavDrawerFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_DRAWER_OPEN, isDrawerOpen());
    }

    /**
     * Sets the title of this activity's action bar.
     * <p>
     * If subclassing activities want the title to be automatically handled when the nav drawer is
     * opened or closed, they should set the action bar title via this method
     *
     * @param title The desired title string
     */
    public void setActionBarTitle(String title) {
        mActionBarTitle = title;
        if (!isDrawerOpen() && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mActionBarTitle);
        }
    }

    public void setActionBarSubtitle(String subtitle) {
        if (!isDrawerOpen() && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    /**
     * Sets the title of this activity's action bar.
     * <p>
     * If subclassing activities want the title to be automatically handled when the nav drawer is
     * opened or closed, they should set the action bar title via this method
     *
     * @param resID The desired title string resource
     */
    public void setActionBarTitle(int resID) {
        mActionBarTitle = getResources().getString(resID);
        if (!isDrawerOpen()) {
            getSupportActionBar().setTitle(mActionBarTitle);
        }
    }
}
