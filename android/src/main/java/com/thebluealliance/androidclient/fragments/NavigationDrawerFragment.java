package com.thebluealliance.androidclient.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.NavigationDrawerAdapter;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * <p/>
 * Opening or closing the drawer will trigger a call to onPrepareOptionsMenu().
 * <p/>
 * Activities containing a NavigationDrawerFragment <strong>must</strong> implement
 * {@link com.thebluealliance.androidclient.fragments.NavigationDrawerFragment.NavigationDrawerListener}.
 * <p/>
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for an explanation of the behaviors implemented here.
 *
 * @author tanis7x
 */
public class NavigationDrawerFragment extends Fragment {
    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private static final List<ListItem> NAVIGATION_ITEMS = new ArrayList<>();

    static {
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_my_tba, "My TBA", R.drawable.my_tba_icon_selector, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_events, "Events", R.drawable.event_icon_selector, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_districts, "Districts", R.drawable.districts_icon_selector, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_teams, "Teams", R.drawable.team_icon_selector, R.layout.nav_drawer_item));
        //NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_insights, "Insights", R.drawable.insights_icon_selector, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_settings, "SETTINGS", R.drawable.settings_icon_selector, R.layout.nav_drawer_item_small));
    }

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private NavigationDrawerAdapter mNavigationAdapter;
    private NavigationDrawerListener mListener;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private boolean mUseActionBarToggle;

    // Required empty constructor
    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        mFromSavedInstanceState = (savedInstanceState == null ? true : false);

        mNavigationAdapter = new NavigationDrawerAdapter(getActivity(), NAVIGATION_ITEMS);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(mNavigationAdapter);

        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId         The android:id of this fragment in its activity's layout.
     * @param drawerLayout       The DrawerLayout containing this fragment's UI.
     * @param encourageLearning  True to encourage the user learning how to use the navigation drawer
     *                           by showing the drawer automatically when this method is called until
     *                           the user has demonstrated knowledge of the drawer's existence by opening
     *                           the drawer. False will disable this behavior and only show the drawer
     *                           when manually opened.
     * @param useActionBarToggle True if the Action Bar home button should be used to open the navigation
     *                           drawer; false if otherwise. Some hosts may want to use up navigation so we
     *                           provide the option to disable this.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, boolean encourageLearning, boolean useActionBarToggle) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mUseActionBarToggle = useActionBarToggle;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        if (mUseActionBarToggle) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            // ActionBarDrawerToggle ties together the the proper interactions
            // between the navigation drawer and the action bar app icon.
            mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),                    /* host Activity */
                    mDrawerLayout,                    /* DrawerLayout object */
                    R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }

                    mListener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (!isAdded()) {
                        return;
                    }

                    if (!mUserLearnedDrawer) {
                        // The user manually opened the drawer; store this flag to prevent auto-showing
                        // the navigation drawer automatically in the future.
                        mUserLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager
                                .getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }

                    mListener.onNavDrawerOpened();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            // Defer code dependent on restoration of previous instance state.
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });
        } else {
            mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {

                @Override
                public void onDrawerOpened(View drawerView) {
                    if (!isAdded()) {
                        return;
                    }

                    if (!mUserLearnedDrawer) {
                        // The user manually opened the drawer; store this flag to prevent auto-showing
                        // the navigation drawer automatically in the future.
                        mUserLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager
                                .getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }

                    mListener.onNavDrawerOpened();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if (!isAdded()) {
                        return;
                    }

                    mListener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            });
        }

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (encourageLearning && !mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    /**
     * Called when an item in the navigation drawer is clicked
     *
     * @param position The position of the clicked item
     */
    private void selectItem(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            mNavigationAdapter.setItemSelected(position);
        }

        NavDrawerItem item = mNavigationAdapter.getItem(position);
        mListener.onNavDrawerItemClicked(item);

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    /**
     * Set the currently selected item in the drawer
     * <p/>
     * This will NOT trigger the OnNavigationDrawerListener callbacks or close the drawer.
     *
     * @param itemId The ID of the item to select
     */
    public void setItemSelected(int itemId) {
        if (mDrawerListView != null) {
            int position = mNavigationAdapter.getPositionForId(itemId);
            mDrawerListView.setItemChecked(position, true);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof NavigationDrawerListener) {
            mListener = (NavigationDrawerListener) activity;
        } else {
            throw new IllegalStateException("Activities hosting a NavigationDrawerFragment must implement OnNavigationDrawerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mUseActionBarToggle && mDrawerToggle != null) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * title, rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Interface for receiving navigation drawer callbacks
     */
    public interface NavigationDrawerListener {
        /**
         * Called when a NavDrawerItem in the navigation drawer is clicked
         *
         * @param item The item that was clicked
         */
        public void onNavDrawerItemClicked(NavDrawerItem item);

        /**
         * Called when the drawer is opened.
         */
        public void onNavDrawerOpened();

        /**
         * CAlled when the drawer is opened.
         */
        public void onNavDrawerClosed();

    }
}
