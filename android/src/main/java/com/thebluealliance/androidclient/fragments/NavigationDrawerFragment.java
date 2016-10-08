package com.thebluealliance.androidclient.fragments;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.adapters.NavigationDrawerAdapter;
import com.thebluealliance.androidclient.auth.AuthProvider;
import com.thebluealliance.androidclient.auth.User;
import com.thebluealliance.androidclient.auth.firebase.MigrateLegacyUserToFirebase;
import com.thebluealliance.androidclient.di.components.HasMyTbaComponent;
import com.thebluealliance.androidclient.listitems.DividerListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.NavDrawerItem;
import com.thebluealliance.androidclient.listitems.SpacerListItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * <p>
 * Opening or closing the drawer will trigger a call to onPrepareOptionsMenu().
 * <p>
 * Activities containing a NavigationDrawerFragment <strong>must</strong> implement {@link
 * com.thebluealliance.androidclient.fragments.NavigationDrawerFragment.NavigationDrawerListener}.
 * <p>
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

    private static final int PROFILE_PIC_SIZE = 200;

    private static final List<ListItem> NAVIGATION_ITEMS = new ArrayList<>();


    static {
        NAVIGATION_ITEMS.add(new SpacerListItem());
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_my_tba, R.string.nav_drawer_mytba_title, R.drawable.ic_star_black_24dp, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_gameday, R.string.nav_drawer_gameday_title, R.drawable.ic_videocam_black_24dp, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_events, R.string.nav_drawer_events_title, R.drawable.ic_event_black_24dp, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_districts, R.string.nav_drawer_districts_title, R.drawable.ic_assignment_black_24dp, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_teams, R.string.nav_drawer_teams_title, R.drawable.ic_group_black_24dp, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_notifications, R.string.nav_drawer_recent_notifications_title, R.drawable.ic_notifications_black_24dp, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new SpacerListItem());
        NAVIGATION_ITEMS.add(new DividerListItem());
        NAVIGATION_ITEMS.add(new SpacerListItem());
        NAVIGATION_ITEMS.add(new NavDrawerItem(R.id.nav_item_settings, R.string.nav_drawer_settings_title, R.drawable.ic_settings_black_24dp, R.layout.nav_drawer_item));
        NAVIGATION_ITEMS.add(new SpacerListItem());
    }

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mNavHeaderWithoutAccount;
    private View mNavHeaderWithAccount;
    private CircleImageView mProfilePicture;
    private TextView mProfileName;
    private View mFragmentContainerView;
    private NavigationDrawerAdapter mNavigationAdapter;
    private NavigationDrawerListener mListener;

    private Picasso mPicasso;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private boolean mUseActionBarToggle;

    @Inject AccountController mAccountController;
    @Inject @Named("firebase_auth") AuthProvider mAuthProvider;

    // Required empty constructor
    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity instanceof HasMyTbaComponent) {
            ((HasMyTbaComponent) activity).getMyTbaComponent().inject(this);
        }

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        mFromSavedInstanceState = (savedInstanceState == null ? true : false);

        mNavigationAdapter = new NavigationDrawerAdapter(getActivity(), NAVIGATION_ITEMS);

        mPicasso = Picasso.with(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mDrawerListView = (ListView) v.findViewById(R.id.left_drawer);
        mDrawerListView.setOnItemClickListener((parent, view, position, id) -> navigationItemClicked(position));
        mDrawerListView.setAdapter(mNavigationAdapter);

        mNavHeaderWithAccount = v.findViewById(R.id.header_with_account);
        mNavHeaderWithoutAccount = v.findViewById(R.id.header_without_account);

        mProfileName = (TextView) v.findViewById(R.id.profile_name);
        mProfilePicture = (CircleImageView) v.findViewById(R.id.profile_image);

        setupNavDrawerHeader();

        return v;
    }

    public void setupNavDrawerHeader() {

        boolean hasAccountDetails = false;
        if (mAccountController.isMyTbaEnabled()) {
            User currentUser = mAuthProvider.getCurrentUser();
            if (currentUser != null) {
                String displayName = currentUser.getName();
                if (!displayName.isEmpty()) {
                    mProfileName.setText(displayName);
                    hasAccountDetails = true;
                }

                Uri profilePic = currentUser.getProfilePicUrl();
                if (profilePic != null) {
                    String personPhotoUrl = profilePic.toString();
                    personPhotoUrl = personPhotoUrl.substring(0,
                                                              personPhotoUrl.length() - 2)
                                     + PROFILE_PIC_SIZE;

                    TbaLogger.d("Profile photo url: " + personPhotoUrl);

                    mPicasso.load(personPhotoUrl).into(mProfilePicture);
                    hasAccountDetails = true;
                }
            } else {
                TbaLogger.w("No current user found");
                // If myTBA /should/ be enabled, but we don't have a registered Firebase user,
                // then we're probably upgrading from a pre-firebase version. Try and migrate
                Activity activity = getActivity();
                activity.startService(new Intent(activity, MigrateLegacyUserToFirebase.class));
            }
        }

        if (hasAccountDetails) {
            mNavHeaderWithAccount.setVisibility(View.VISIBLE);
            mNavHeaderWithoutAccount.setVisibility(View.GONE);
        } else {
            mNavHeaderWithAccount.setVisibility(View.GONE);
            mNavHeaderWithoutAccount.setVisibility(View.VISIBLE);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId         The android:id of this fragment in its activity's layout.
     * @param drawerLayout       The DrawerLayout containing this fragment's UI.
     * @param encourageLearning  True to encourage the user learning how to use the navigation
     *                           drawer by showing the drawer automatically when this method is
     *                           called until the user has demonstrated knowledge of the drawer's
     *                           existence by opening the drawer. False will disable this behavior
     *                           and only show the drawer when manually opened.
     * @param useActionBarToggle True if the Action Bar home button should be used to open the
     *                           navigation drawer; false if otherwise. Some hosts may want to use
     *                           up navigation so we provide the option to disable this.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, boolean encourageLearning, boolean useActionBarToggle) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mUseActionBarToggle = useActionBarToggle;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        if (mUseActionBarToggle) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            // ActionBarDrawerToggle ties together the the proper interactions
            // between the navigation drawer and the action bar app icon.
            mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),                    /* host Activity */
                    mDrawerLayout,                    /* DrawerLayout object */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }

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

                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            // Defer code dependent on restoration of previous instance state.
            mDrawerLayout.post(() -> mDrawerToggle.syncState());
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
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    // Do nothing
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
    private void navigationItemClicked(int position) {
        ListItem item = mNavigationAdapter.getItem(position);

        // Ignore clicks on items like dividers and spacers
        if (!(item instanceof NavDrawerItem)) {
            return;
        }

        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }

        mListener.onNavDrawerItemClicked((NavDrawerItem) item);

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    /**
     * Set the currently selected item in the drawer
     * <p>
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
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mUseActionBarToggle && mDrawerToggle != null) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    private ActionBar getSupportActionBar() {
        return getActivity() != null ? ((AppCompatActivity) getActivity()).getSupportActionBar() : null;
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
        void onNavDrawerItemClicked(NavDrawerItem item);

    }

    public static class LinearGradientTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap outBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());

            // Create shaders
            Shader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Shader linearGradient = new LinearGradient(0, source.getHeight() / 2, 0, source.getHeight(), Color.TRANSPARENT, 0xB4000000, Shader.TileMode.CLAMP);

            // create a shader that combines both effects
            ComposeShader shader = new ComposeShader(bitmapShader, linearGradient, PorterDuff.Mode.DST_OUT);

            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setShader(shader);

            Paint black = new Paint();
            black.setColor(Color.BLACK);

            Canvas c = new Canvas(outBitmap);
            c.drawRect(0, 0, source.getWidth(), source.getHeight(), black);
            c.drawPaint(p);
            source.recycle();
            return outBitmap;
        }

        @Override
        public String key() {
            return "linear_gradient";
        }
    }

    /**
     * Called when the insets of the nav drawer are changed. This allows us to properly place the
     * contents so that they don't flow under the status bar.
     */
    public void onInsetsChanged(Rect insets) {
        RelativeLayout navDrawereHeader = (RelativeLayout) getView().findViewById(R.id.nav_drawer_header);

        int pBottom = navDrawereHeader.getPaddingBottom();
        int pLeft = navDrawereHeader.getPaddingLeft();
        int pRight = navDrawereHeader.getPaddingRight();

        navDrawereHeader.setPadding(pLeft, insets.top, pRight, pBottom);
    }
}
