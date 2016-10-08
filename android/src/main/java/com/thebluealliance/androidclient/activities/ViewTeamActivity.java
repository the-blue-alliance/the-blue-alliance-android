package com.thebluealliance.androidclient.activities;

import com.thebluealliance.androidclient.NfcUris;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.ShareUris;
import com.thebluealliance.androidclient.TBAAndroid;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.adapters.DialogListWithIconsAdapter;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.components.DaggerFragmentComponent;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;
import com.thebluealliance.androidclient.listeners.ClickListenerModule;
import com.thebluealliance.androidclient.subscribers.SubscriberModule;
import com.thebluealliance.androidclient.subscribers.YearsParticipatedDropdownSubscriber;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.views.SlidingTabs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

public class ViewTeamActivity extends MyTBASettingsActivity implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener,
        HasFragmentComponent,
        YearsParticipatedUpdate {

    public static final String EXTRA_TEAM_KEY = "team_key";
    public static final String TEAM_YEAR = "team_year";
    public static final String SELECTED_YEAR = "year";
    public static final String SELECTED_TAB = "tab";
    public static final String CURRENT_PHOTO_URI = "current_photo_uri";

    private static final String PREF_MEDIA_SNACKBAR_DISMISSED = "pref_media_snackbar_dismissed";

    private static final int CHOOSE_IMAGE_REQUEST = 42;
    private static final int TAKE_PICTURE_REQUEST = 43;

    @Bind(R.id.year_selector_container) View mYearSelectorContainer;
    @Bind(R.id.year_selector_subtitle_container) View mYearSelectorSubtitleContainer;
    @Bind(R.id.year_selector_title) TextView mYearSelectorTitle;
    @Bind(R.id.year_selector_subtitle) TextView mYearSelectorSubtitle;
    @Bind(R.id.view_pager) ViewPager mPager;

    private Snackbar mMediaSnackbar;

    private FragmentComponent mComponent;
    private int mCurrentSelectedYearPosition = -1,
            mSelectedTab = -1;

    private String mCurrentPhotoUri;

    private int[] mYearsParticipated;

    @Inject TBAStatusController mStatusController;
    @Inject CacheableDatafeed mDatafeed;
    @Inject AccountController mAccountController;

    // Should come in the format frc####
    private String mTeamKey;
    private int mYear;

    ViewTeamFragmentPagerAdapter mAdapter;

    public static Intent newInstance(Context context, String teamKey) {
        System.out.println("making intent for " + teamKey);
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(EXTRA_TEAM_KEY, teamKey);
        return intent;
    }

    public static Intent newInstance(Context context, String teamKey, int year) {
        Intent intent = new Intent(context, ViewTeamActivity.class);
        intent.putExtra(EXTRA_TEAM_KEY, teamKey);
        intent.putExtra(TEAM_YEAR, year);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTeamKey = getIntent().getStringExtra(EXTRA_TEAM_KEY);
        if (mTeamKey == null) {
            throw new IllegalArgumentException("ViewTeamActivity must be created with a team key!");
        }

        setModelKey(mTeamKey, ModelType.TEAM);
        setShareEnabled(true);
        setContentView(R.layout.activity_view_team);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_TAB)) {
                mSelectedTab = savedInstanceState.getInt(SELECTED_TAB);
            }
            if (savedInstanceState.containsKey(SELECTED_YEAR)) {
                mYear = savedInstanceState.getInt(SELECTED_YEAR);
            }
            if (savedInstanceState.containsKey(CURRENT_PHOTO_URI)) {
                mCurrentPhotoUri = savedInstanceState.getString(CURRENT_PHOTO_URI);
            }
        } else {
            int maxYear = mStatusController.getMaxCompYear();
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(TEAM_YEAR)) {
                mYear = getIntent().getIntExtra(TEAM_YEAR, maxYear);
            } else {
                mYear = maxYear;
            }
            mSelectedTab = 0;
        }

        mPager.setOffscreenPageLimit(3);
        mPager.setPageMargin(Utilities.getPixelsFromDp(this, 16));
        // We will notify the fragments of the year later
        mAdapter = new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, mYear);
        mPager.setAdapter(mAdapter);

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
        tabs.setOnPageChangeListener(this);
        ViewCompat.setElevation(tabs, getResources().getDimension(R.dimen.toolbar_elevation));

        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(BaseActivity.WARNING_OFFLINE);
        }

        mDatafeed.fetchTeamYearsParticipated(mTeamKey, null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new YearsParticipatedDropdownSubscriber(this));

        // We can call this even though the years participated haven't been loaded yet.
        // The years won't be shown yet; this just shows the team number in the toolbar.
        setupActionBar();

        boolean wasMediaSnackbarDismissed = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_MEDIA_SNACKBAR_DISMISSED, false);
        if (!wasMediaSnackbarDismissed && mAccountController.isMyTbaEnabled()) {
            mMediaSnackbar = createSnackbar(Html.fromHtml(getString(R.string.imgur_media_snackbar_message)), Snackbar.LENGTH_INDEFINITE);
            mMediaSnackbar.setAction(R.string.imgur_media_snackbar_Action_dismiss, (view) -> {
            });
            mMediaSnackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    markMediaSnackbarAsDismissed();
                }
            }).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setShareUri(String.format(ShareUris.URI_TEAM, TeamHelper.getTeamNumber(mTeamKey), mYear));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_YEAR, mYear);
        outState.putInt(SELECTED_TAB, mSelectedTab);
        if (mCurrentPhotoUri != null) {
            outState.putString(CURRENT_PHOTO_URI, mCurrentPhotoUri);
        }
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    private void setupActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
            String teamNumber = mTeamKey.replace("frc", "");
            mYearSelectorTitle.setText(String.format(getString(R.string.team_actionbar_title),
                    teamNumber));

            // If we call this and the years participated haven't been loaded yet, don't try to use them
            if (mYearsParticipated != null && mYearsParticipated.length > 0) {

                mYearSelectorSubtitleContainer.setVisibility(View.VISIBLE);

                final Dialog dialog = makeDialogForYearSelection(R.string.select_year, mYearsParticipated);

                mYearSelectorContainer.setOnClickListener(v -> dialog.show());
            } else {
                // If there are no valid years, hide the subtitle and disable clicking
                mYearSelectorSubtitleContainer.setVisibility(View.GONE);
                mYearSelectorContainer.setOnClickListener(null);
            }
        }
    }

    private Dialog makeDialogForYearSelection(@StringRes int titleResId, int[] dropdownItems) {
        // Create an array of strings from the int years
        String[] years = new String[dropdownItems.length];
        for (int i = 0; i < years.length; i++) {
            years[i] = String.valueOf(dropdownItems[i]);
        }

        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(res.getString(titleResId));
        builder.setItems(years, (dialog, which) -> {
            onYearSelected(which);
        });

        return builder.create();
    }

    private void updateTeamYearSelector(int selectedPosition) {
        if (selectedPosition < 0 || selectedPosition >= mYearsParticipated.length) {
            return;
        }
        mYearSelectorSubtitle.setText(String.valueOf(mYearsParticipated[selectedPosition]));
    }

    @Override
    public void updateYearsParticipated(int[] years) {
        mYearsParticipated = years;

        // If we received a desired year in the intent, find the index of that year if it exists
        int requestedYearIndex = 0;
        for (int i = 0; i < years.length; i++) {
            if (years[i] == mYear) {
                requestedYearIndex = i;
            }
        }

        // Refresh action bar; this will the year subtitle if there are no valid ones
        setupActionBar();

        onYearSelected(requestedYearIndex);
    }

    private void onYearSelected(int position) {
        // Only handle this if the year has actually changed
        if (position == mCurrentSelectedYearPosition) {
            return;
        }

        // Bounds checking!
        if (position < 0 || position >= mYearsParticipated.length) {
            return;
        }

        mCurrentSelectedYearPosition = position;
        updateTeamYearSelector(position);
        int newYear = mYearsParticipated[mCurrentSelectedYearPosition];
        if (newYear == mYear) {
            return;
        }
        mYear = newYear;
        setBeamUri(String.format(NfcUris.URI_TEAM_IN_YEAR, mTeamKey, mYear));
        setShareUri(String.format(ShareUris.URI_TEAM, TeamHelper.getTeamNumber(mTeamKey), mYear));
        mAdapter.updateYear(mYear);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isDrawerOpen()) {
                closeDrawer();
                return true;
            }

            // If this tasks exists in the back stack, it will be brought to the front and all other activities
            // will be destroyed. HomeActivity will be delivered this intent via onNewIntent().
            startActivity(HomeActivity.newInstance(this, R.id.nav_item_teams).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mSelectedTab = position;

        switch (position) {
            case ViewTeamFragmentPagerAdapter.TAB_INFO:
                syncFabVisibilityWithMyTbaEnabled(true);
                setupFabForMyTbaSettingsTab();
                break;
            case ViewTeamFragmentPagerAdapter.TAB_MEDIA:
                syncFabVisibilityWithMyTbaEnabled(true);
                setFabColor(R.color.accent);
                setFabDrawable(R.drawable.ic_add_a_photo_white_24dp);
                break;
            default:
                hideFab(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected boolean onFabClick() {
        switch (mSelectedTab) {
            case ViewTeamFragmentPagerAdapter.TAB_MEDIA:
                // If the device doesn't have a camera, send them straight to the image picker
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    selectImage();
                } else {
                    final String[] items = new String[]{"Take picture", "Choose image"};
                    final Integer[] icons = new Integer[]{R.drawable.ic_photo_camera_black_24dp, R.drawable.ic_photo_library_black_24dp};
                    ListAdapter adapter = new DialogListWithIconsAdapter(this, items, icons);

                    new AlertDialog.Builder(this)
                            .setTitle("Add team image")
                            .setAdapter(adapter, (dialog, position) -> {
                                switch (position) {
                                    case 0: // take picture
                                        takePicture();
                                        dialog.cancel();
                                        break;
                                    case 1: // select from gallery
                                        selectImage();
                                        dialog.cancel();
                                        break;
                                }
                            })
                            .show();
                }

                // Dismiss the awareness snackbar when the fab is clicked
                if (mMediaSnackbar != null) {
                    mMediaSnackbar.dismiss();
                    markMediaSnackbarAsDismissed();
                }
                return true;
        }
        return false;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE_REQUEST);
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // TODO handle this
                ex.printStackTrace();
                showSnackbar(R.string.error_taking_picture);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                TbaLogger.i("Uri: " + uri.toString());
                // Pass off the URI to ConfirmImageSuggestionActivity, it will handle uploading
                // and suggesting the appropriate image
                startActivity(ConfirmImageSuggestionActivity.newIntent(this, uri, mTeamKey, mYear));
            }
        } else if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            // Tell Media Scanner about the new photo
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(mCurrentPhotoUri)));

            // Off to ConfirmImageSuggestionActivity!
            startActivity(ConfirmImageSuggestionActivity.newIntent(this, Uri.parse(mCurrentPhotoUri), mTeamKey, mYear));
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir = new File(storageDir, "The Blue Alliance");
        storageDir.mkdir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoUri = "file:" + image.getAbsolutePath();
        return image;
    }

    private void markMediaSnackbarAsDismissed() {
        PreferenceManager.getDefaultSharedPreferences(ViewTeamActivity.this)
                .edit().putBoolean(PREF_MEDIA_SNACKBAR_DISMISSED, true).commit();
    }

    public FragmentComponent getComponent() {
        if (mComponent == null) {
            TBAAndroid application = ((TBAAndroid) getApplication());
            mComponent = DaggerFragmentComponent.builder()
                    .applicationComponent(application.getComponent())
                    .datafeedModule(application.getDatafeedModule())
                    .binderModule(application.getBinderModule())
                    .databaseWriterModule(application.getDatabaseWriterModule())
                    .gceModule(application.getGceModule())
                    .subscriberModule(new SubscriberModule(this))
                    .clickListenerModule(new ClickListenerModule(this))
                    .build();
        }
        return mComponent;
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }
}
