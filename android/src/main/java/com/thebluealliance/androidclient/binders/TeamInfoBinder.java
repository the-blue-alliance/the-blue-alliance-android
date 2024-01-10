package com.thebluealliance.androidclient.binders;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import androidx.annotation.Nullable;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.databinding.FragmentTeamInfoBinding;
import com.thebluealliance.androidclient.helpers.PitLocationHelper;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.types.MediaType;

import java.util.Map;

import javax.inject.Inject;

public class TeamInfoBinder extends AbstractDataBinder<TeamInfoBinder.Model, FragmentTeamInfoBinding> {

    private static final int TEAM_FULL_NAME_COLLAPSED_MAX_LINES = 3;

    @Inject SocialClickListener mSocialClickListener;

    @Inject
    public TeamInfoBinder(SocialClickListener socialClickListener) {
        mSocialClickListener = socialClickListener;
    }

    @Override
    public void bindViews() {
        mBinding = FragmentTeamInfoBinding.bind(mRootView);
    }

    @Override
    public void updateData(@Nullable TeamInfoBinder.Model data) {
        if (data == null) {
            if (!isDataBound()) {
                bindNoDataView();
            }
            return;
        }
        mSocialClickListener.setModelKey(data.teamKey);

        if (data.nickname.isEmpty()) {
            mBinding.teamContainer.teamName.setText("Team " + data.teamNumber);
        } else {
            mBinding.teamContainer.teamName.setText(data.nickname);
        }

        if (data.location == null || data.location.isEmpty()) {
            // No location; hide the location view
            mBinding.teamContainer.teamLocation.setVisibility(View.GONE);
        } else {
            // Show and populate the location view
            mBinding.teamContainer.teamLocation.setText(data.location);

            // Tag is used to create an ACTION_VIEW intent for a maps application
            mBinding.teamContainer.teamLocationContainer.setTag("geo:0,0?q=" + Uri.encode(data.location));
        }
        mBinding.teamContainer.teamLocationContainer.setOnClickListener(mSocialClickListener);

        // if (data.motto.isEmpty()) {
        //     // No location; hide the location view
        //     teamMottoContainer.setVisibility(View.GONE);
        // } else {
        //     // Show and populate the location view
        //     teamMotto.setText(data.motto);
        // }
        mBinding.teamContainer.teamMottoContainer.setVisibility(View.GONE);  // it's out of fashion

        // If the team doesn't have a defined website, create a Google search for the team name
        if (data.website.isEmpty()) {
            mBinding.teamWebsiteContainer.setTag("https://www.google.com/search?q=" + Uri.encode(data.nickname));
            mBinding.teamWebsiteTitle.setText(R.string.find_event_on_google);
        } else {
            mBinding.teamWebsiteContainer.setTag(data.website);
            mBinding.teamWebsiteTitle.setText(R.string.view_team_website);
        }
        mBinding.teamWebsiteContainer.setOnClickListener(mSocialClickListener);

        if (data.socialMedia.containsKey(MediaType.TWITTER_PROFILE)) {
            String twitterKey = data.socialMedia.get(MediaType.TWITTER_PROFILE);
            mBinding.teamTwitterContainer.setVisibility(View.VISIBLE);
            mBinding.twitterDivider.setVisibility(View.VISIBLE);
            mBinding.teamTwitterContainer.setTag("https://twitter.com/" + twitterKey);
            mBinding.teamTwitterContainer.setOnClickListener(mSocialClickListener);
            mBinding.teamTwitterTitle.setText(mActivity.getString(R.string.view_team_twitter, twitterKey));
        } else {
            mBinding.teamTwitterContainer.setVisibility(View.GONE);
            mBinding.twitterDivider.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.YOUTUBE_CHANNEL)) {
            String youtubeKey = data.socialMedia.get(MediaType.YOUTUBE_CHANNEL);
            mBinding.teamYoutubeContainer.setVisibility(View.VISIBLE);
            mBinding.youtubeDivider.setVisibility(View.VISIBLE);
            mBinding.teamYoutubeContainer.setTag("https://www.youtube.com/" + youtubeKey);
            mBinding.teamYoutubeContainer.setOnClickListener(mSocialClickListener);
            mBinding.teamYoutubeTitle.setText(mActivity.getString(R.string.view_team_youtube, youtubeKey));
        } else {
            mBinding.teamYoutubeContainer.setVisibility(View.GONE);
            mBinding.youtubeDivider.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.FACEBOOK_PROFILE)) {
            String fbKey = data.socialMedia.get(MediaType.FACEBOOK_PROFILE);
            mBinding.teamFacebookContainer.setVisibility(View.VISIBLE);
            mBinding.facebookDivider.setVisibility(View.VISIBLE);
            mBinding.teamFacebookContainer.setTag("https://facebook.com/" + fbKey);
            mBinding.teamFacebookContainer.setOnClickListener(mSocialClickListener);
            mBinding.teamFacebookTitle.setText(mActivity.getString(R.string.view_team_fb, fbKey));
        } else {
            mBinding.teamFacebookContainer.setVisibility(View.GONE);
            mBinding.facebookDivider.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.GITHUB_PROFILE)) {
            String githubKey = data.socialMedia.get(MediaType.GITHUB_PROFILE);
            mBinding.teamGithubContainer.setVisibility(View.VISIBLE);
            mBinding.teamGithubContainer.setTag("https://github.com/" + githubKey);
            mBinding.teamGithubContainer.setOnClickListener(mSocialClickListener);
            mBinding.teamGithubTitle.setText(mActivity.getString(R.string.view_team_github, githubKey));
        } else {
            mBinding.teamGithubContainer.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.INSTAGRAM_PROFILE)) {
            String instaKey = data.socialMedia.get(MediaType.INSTAGRAM_PROFILE);
            mBinding.teamInstagramContainer.setVisibility(View.VISIBLE);
            mBinding.instagramDivider.setVisibility(View.VISIBLE);
            mBinding.teamInstagramContainer.setTag("https://www.instagram.com/" + instaKey);
            mBinding.teamInstagramContainer.setOnClickListener(mSocialClickListener);
            mBinding.teamInstagramTitle.setText(mActivity.getString(R.string.view_team_instagram, instaKey));
        } else {
            mBinding.teamInstagramContainer.setVisibility(View.GONE);
            mBinding.instagramDivider.setVisibility(View.GONE);
        }

        if (data.fullName.isEmpty()) {
            // No full name specified, hide the view
            mBinding.teamContainer.teamFullName.setVisibility(View.GONE);
        } else {
            // This string needs to be specially formatted
            SpannableString string = new SpannableString("aka " + data.fullName);
            string.setSpan(new TextAppearanceSpan(mActivity,
                    R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mBinding.teamContainer.teamFullName.setText(string);
            mBinding.teamContainer.teamFullName.setMaxLines(TEAM_FULL_NAME_COLLAPSED_MAX_LINES);
            mBinding.teamContainer.teamFullName.setEllipsize(TextUtils.TruncateAt.END);

            mBinding.teamContainer.teamFullNameContainer.setOnClickListener((view) -> {
                toggleFullTeamNameExpanded();
            });
        }

        mBinding.teamContainer.champsPitLocationContainer.setVisibility(View.GONE);
        if (data.showPitLocation && data.pitLocation != null) {
            mBinding.teamContainer.champsPitLocationContainer.setVisibility(View.VISIBLE);
            mBinding.teamContainer.champsPitLocation.setText(data.pitLocation.getAddressString());
        }

        mBinding.teamNextMatchLabel.setVisibility(View.GONE);
        mBinding.teamNextMatchDetails.setVisibility(View.GONE);
        mBinding.content.setVisibility(View.VISIBLE);

        mBinding.progress.setVisibility(View.GONE);

        mBinding.content.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        setDataBound(true);
    }

    @Override
    public void onComplete() {
        mBinding.progress.setVisibility(View.GONE);

        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        TbaLogger.e(throwable.toString());

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    private void bindNoDataView() {
        try {
            mBinding.content.setVisibility(View.GONE);
            mBinding.progress.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleFullTeamNameExpanded() {
        int currentMaxLines = mBinding.teamContainer.teamFullName.getMaxLines();
        if (currentMaxLines == TEAM_FULL_NAME_COLLAPSED_MAX_LINES) {
            // The text view is collapsed, expand it

            final int height = mBinding.teamContainer.teamFullName.getMeasuredHeight();

            mBinding.teamContainer.teamFullName.setMaxLines(Integer.MAX_VALUE);
            mBinding.teamContainer.teamFullName.measure(
                    View.MeasureSpec.makeMeasureSpec(mBinding.teamContainer.teamFullName.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            final int newHeight = mBinding.teamContainer.teamFullName.getMeasuredHeight();

            ObjectAnimator animation = ObjectAnimator.ofInt(mBinding.teamContainer.teamFullName, "height", height, newHeight);
            animation.setDuration(500);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    mBinding.teamContainer.teamFullName.setMaxLines(Integer.MAX_VALUE);
                    mBinding.teamContainer.teamFullName.setMinHeight(0);
                }
            });
            animation.start();
        } else {
            // We need to collapse the text view

            final int height = mBinding.teamContainer.teamFullName.getMeasuredHeight();

            // Only set max lines while we measure; max lines will be permanently
            // reduced one the animation completes
            mBinding.teamContainer.teamFullName.setMaxLines(TEAM_FULL_NAME_COLLAPSED_MAX_LINES);
            mBinding.teamContainer.teamFullName.measure(
                    View.MeasureSpec.makeMeasureSpec(mBinding.teamContainer.teamFullName.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mBinding.teamContainer.teamFullName.setMaxLines(Integer.MAX_VALUE);

            final int newHeight = mBinding.teamContainer.teamFullName.getMeasuredHeight();
            ObjectAnimator animation = ObjectAnimator.ofInt(mBinding.teamContainer.teamFullName, "height", height, newHeight);
            animation.setDuration(500);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    mBinding.teamContainer.teamFullName.setMaxLines(TEAM_FULL_NAME_COLLAPSED_MAX_LINES);
                    mBinding.teamContainer.teamFullName.setMinHeight(0);
                }
            });
            animation.start();
        }

    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            mBinding = null;
        }
    }

    public static class Model {
        public String teamKey;
        public String nickname;
        public String fullName;
        public String location;
        public String website;
        public String motto;
        public int teamNumber;
        public Map<MediaType, String> socialMedia;
        public boolean showPitLocation;
        public @Nullable PitLocationHelper.TeamPitLocation pitLocation;
    }
}
