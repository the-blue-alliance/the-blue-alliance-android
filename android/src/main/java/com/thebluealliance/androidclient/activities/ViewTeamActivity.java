package com.thebluealliance.androidclient.activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.ShareUris;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountController;
import com.thebluealliance.androidclient.adapters.DialogListWithIconsAdapter;
import com.thebluealliance.androidclient.adapters.ViewTeamFragmentPagerAdapter;
import com.thebluealliance.androidclient.databinding.ActivityViewTeamBinding;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.eventbus.TeamAvatarUpdateEvent;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.interfaces.YearsParticipatedUpdate;
import com.thebluealliance.androidclient.subscribers.YearsParticipatedDropdownSubscriber;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.views.SlidingTabs;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.schedulers.Schedulers;

@RuntimePermissions
@AndroidEntryPoint
public class ViewTeamActivity extends MyTBASettingsActivity implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener,
        YearsParticipatedUpdate {

    public static final String EXTRA_TEAM_KEY = "team_key";
    public static final String TEAM_YEAR = "team_year";
    public static final String SELECTED_YEAR = "year";
    public static final String SELECTED_TAB = "tab";
    public static final String CURRENT_PHOTO_URI = "current_photo_uri";

    private static final String PREF_MEDIA_SNACKBAR_DISMISSED = "pref_media_snackbar_dismissed";

    private static final int CHOOSE_IMAGE_REQUEST = 42;
    private static final int TAKE_PICTURE_REQUEST = 43;

    private ActivityViewTeamBinding mBinding;

    private Snackbar mMediaSnackbar;
    private int mCurrentSelectedYearPosition = -1,
            mSelectedTab = -1;

    private Uri mCurrentPhotoUri;

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
        mBinding = ActivityViewTeamBinding.inflate(getLayoutInflater(), mRootView, true);

        setModelKey(mTeamKey, ModelType.TEAM);
        setShareEnabled(true);

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
                mCurrentPhotoUri = savedInstanceState.getParcelable(CURRENT_PHOTO_URI);
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

        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.viewPager.setPageMargin(Utilities.getPixelsFromDp(this, 16));
        // We will notify the fragments of the year later
        mAdapter = new ViewTeamFragmentPagerAdapter(getSupportFragmentManager(), mTeamKey, mYear);
        mBinding.viewPager.setAdapter(mAdapter);

        SlidingTabs tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(mBinding.viewPager);
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
            outState.putParcelable(CURRENT_PHOTO_URI, mCurrentPhotoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ViewTeamActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
            mBinding.yearSelectorTitle.setText(String.format(getString(R.string.team_actionbar_title),
                    teamNumber));

            // If we call this and the years participated haven't been loaded yet, don't try to use them
            if (mYearsParticipated != null && mYearsParticipated.length > 0) {

                mBinding.yearSelectorSubtitleContainer.setVisibility(View.VISIBLE);

                final Dialog dialog = makeDialogForYearSelection(R.string.select_year, mYearsParticipated);

                mBinding.yearSelectorContainer.setOnClickListener(v -> dialog.show());
            } else {
                // If there are no valid years, hide the subtitle and disable clicking
                mBinding.yearSelectorSubtitleContainer.setVisibility(View.GONE);
                mBinding.yearSelectorContainer.setOnClickListener(null);
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
        mBinding.yearSelectorSubtitle.setText(String.valueOf(mYearsParticipated[selectedPosition]));
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

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateTeamAvatar(TeamAvatarUpdateEvent avatarUpdateEvent) {
        if (avatarUpdateEvent == null
                || avatarUpdateEvent.getB64Image() == null
                || avatarUpdateEvent.getB64Image().isEmpty()) {
            mBinding.teamAvatar.setVisibility(View.GONE);
        } else {
            byte[] bytes = Base64.decode(avatarUpdateEvent.getB64Image(), Base64.DEFAULT);
            Bitmap avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mBinding.teamAvatar.setImageBitmap(Bitmap.createScaledBitmap(avatar, 80, 80, false));
            mBinding.teamAvatar.setVisibility(View.VISIBLE);
        }
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
                                        ViewTeamActivityPermissionsDispatcher.takePictureWithPermissionCheck(this);
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

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void takePicture() {
        if (!checkHasStoragePermission()) {
            TbaLogger.e("Permission WRITE_EXTERNAL_STORAGE not granted");
            showSnackbar(R.string.error_taking_picture);
            return;
        }

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
                TbaLogger.e("Unable to create image file", ex);
                showSnackbar(R.string.error_taking_picture);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
            } else {
                TbaLogger.w("Unable to open file for photo");
                showSnackbar(R.string.error_taking_picture);
            }
        } else {
            TbaLogger.w("Unable to resolve ");
            showSnackbar(R.string.error_taking_picture);
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.camera_permission_title)
                .setMessage(R.string.camera_permission_rationale)
                .setPositiveButton(R.string.allow, (dialog, button) -> request.proceed())
                .setNegativeButton(R.string.deny, (dialog, button) -> request.cancel())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mCurrentPhotoUri));

            // Off to ConfirmImageSuggestionActivity!
            startActivity(ConfirmImageSuggestionActivity.newIntent(this, mCurrentPhotoUri, mTeamKey, mYear));
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        storageDir = new File(storageDir, "The Blue Alliance");
        storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", image);

        return image;
    }

    private boolean checkHasStoragePermission() {
        return PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void markMediaSnackbarAsDismissed() {
        PreferenceManager.getDefaultSharedPreferences(ViewTeamActivity.this)
                .edit().putBoolean(PREF_MEDIA_SNACKBAR_DISMISSED, true).commit();
    }
}
