package com.thebluealliance.androidclient.binders;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.helpers.PitLocationHelper;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.types.MediaType;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TeamInfoBinder extends AbstractDataBinder<TeamInfoBinder.Model> {

    private static final int TEAM_FULL_NAME_COLLAPSED_MAX_LINES = 3;

    @Inject SocialClickListener mSocialClickListener;

    @Bind(R.id.content) View content;
    @Bind(R.id.team_name) TextView teamName;
    @Bind(R.id.team_location_container) View teamLocationContainer;
    @Bind(R.id.team_location) TextView teamLocation;

    @Bind(R.id.team_website_container) View teamWebsiteContainer;
    @Bind(R.id.team_website_title) TextView teamWebsiteTitle;
    @Bind(R.id.team_twitter_container) View teamTwitterContainer;
    @Bind(R.id.team_twitter_title) TextView teamTwitterTitle;
    @Bind(R.id.twitter_divider) View twitterDivider;
    @Bind(R.id.team_youtube_container) View teamYoutubeContainer;
    @Bind(R.id.team_youtube_title) TextView teamYoutubeTitle;
    @Bind(R.id.youtube_divider) View youtubeDivider;
    @Bind(R.id.team_facebook_container) View teamFbContainer;
    @Bind(R.id.team_facebook_title) TextView teamFbTitle;
    @Bind(R.id.facebook_divider) View facebookDivider;
    @Bind(R.id.team_github_container) View teamGitHubContainer;
    @Bind(R.id.team_github_title) TextView teamGitHubTitle;
    @Bind(R.id.team_instagram_container) View teamInstaContainer;
    @Bind(R.id.team_instagram_title) TextView teamInstaTitle;
    @Bind(R.id.instagram_divider) View instagramDivider;
    @Bind(R.id.team_full_name_container) View teamFullNameContainer;
    @Bind(R.id.team_full_name) TextView teamFullName;
    @Bind(R.id.team_next_match_label) View teamNextMatchLabel;
    @Bind(R.id.team_next_match_details) View teamNextMatchDetails;
    @Bind(R.id.progress) View progress;
    @Bind(R.id.team_motto_container) View teamMottoContainer;
    @Bind(R.id.team_motto) TextView teamMotto;
    @Bind(R.id.champs_pit_location_container) View champsPitLocationContainer;
    @Bind(R.id.champs_pit_location) TextView champsPitLocation;

    @Inject
    public TeamInfoBinder(SocialClickListener socialClickListener) {
        mSocialClickListener = socialClickListener;
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
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
            teamName.setText("Team " + data.teamNumber);
        } else {
            teamName.setText(data.nickname);
        }

        if (data.location.isEmpty()) {
            // No location; hide the location view
            teamLocationContainer.setVisibility(View.GONE);
        } else {
            // Show and populate the location view
            teamLocation.setText(data.location);

            // Tag is used to create an ACTION_VIEW intent for a maps application
            teamLocationContainer.setTag("geo:0,0?q=" + Uri.encode(data.location));
        }
        teamLocationContainer.setOnClickListener(mSocialClickListener);

        if (data.motto.isEmpty()) {
            // No location; hide the location view
            teamMottoContainer.setVisibility(View.GONE);
        } else {
            // Show and populate the location view
            teamMotto.setText(data.motto);
        }

        // If the team doesn't have a defined website, create a Google search for the team name
        if (data.website.isEmpty()) {
            teamWebsiteContainer.setTag("https://www.google.com/search?q=" + Uri.encode(data.nickname));
            teamWebsiteTitle.setText(R.string.find_event_on_google);
        } else {
            teamWebsiteContainer.setTag(data.website);
            teamWebsiteTitle.setText(R.string.view_team_website);
        }
        teamWebsiteContainer.setOnClickListener(mSocialClickListener);

        if (data.socialMedia.containsKey(MediaType.TWITTER_PROFILE)) {
            String twitterKey = data.socialMedia.get(MediaType.TWITTER_PROFILE);
            teamTwitterContainer.setVisibility(View.VISIBLE);
            twitterDivider.setVisibility(View.VISIBLE);
            teamTwitterContainer.setTag("https://twitter.com/" + twitterKey);
            teamTwitterContainer.setOnClickListener(mSocialClickListener);
            teamTwitterTitle.setText(mActivity.getString(R.string.view_team_twitter, twitterKey));
        } else {
            teamTwitterContainer.setVisibility(View.GONE);
            twitterDivider.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.YOUTUBE_CHANNEL)) {
            String youtubeKey = data.socialMedia.get(MediaType.YOUTUBE_CHANNEL);
            teamYoutubeContainer.setVisibility(View.VISIBLE);
            youtubeDivider.setVisibility(View.VISIBLE);
            teamYoutubeContainer.setTag("https://www.youtube.com/" + youtubeKey);
            teamYoutubeContainer.setOnClickListener(mSocialClickListener);
            teamYoutubeTitle.setText(mActivity.getString(R.string.view_team_youtube, youtubeKey));
        } else {
            teamYoutubeContainer.setVisibility(View.GONE);
            youtubeDivider.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.FACEBOOK_PROFILE)) {
            String fbKey = data.socialMedia.get(MediaType.FACEBOOK_PROFILE);
            teamFbContainer.setVisibility(View.VISIBLE);
            facebookDivider.setVisibility(View.VISIBLE);
            teamFbContainer.setTag("https://facebook.com/" + fbKey);
            teamFbContainer.setOnClickListener(mSocialClickListener);
            teamFbTitle.setText(mActivity.getString(R.string.view_team_fb, fbKey));
        } else {
            teamFbContainer.setVisibility(View.GONE);
            facebookDivider.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.GITHUB_PROFILE)) {
            String githubKey = data.socialMedia.get(MediaType.GITHUB_PROFILE);
            teamGitHubContainer.setVisibility(View.VISIBLE);
            teamGitHubContainer.setTag("https://github.com/" + githubKey);
            teamGitHubContainer.setOnClickListener(mSocialClickListener);
            teamGitHubTitle.setText(mActivity.getString(R.string.view_team_github, githubKey));
        } else {
            teamGitHubContainer.setVisibility(View.GONE);
        }

        if (data.socialMedia.containsKey(MediaType.INSTAGRAM_PROFILE)) {
            String instaKey = data.socialMedia.get(MediaType.INSTAGRAM_PROFILE);
            teamInstaContainer.setVisibility(View.VISIBLE);
            instagramDivider.setVisibility(View.VISIBLE);
            teamInstaContainer.setTag("https://www.instagram.com/" + instaKey);
            teamInstaContainer.setOnClickListener(mSocialClickListener);
            teamInstaTitle.setText(mActivity.getString(R.string.view_team_instagram, instaKey));
        } else {
            teamInstaContainer.setVisibility(View.GONE);
            instagramDivider.setVisibility(View.GONE);
        }

        if (data.fullName.isEmpty()) {
            // No full name specified, hide the view
            teamFullNameContainer.setVisibility(View.GONE);
        } else {
            // This string needs to be specially formatted
            SpannableString string = new SpannableString("aka " + data.fullName);
            string.setSpan(new TextAppearanceSpan(mActivity,
                    R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            teamFullName.setText(string);
            teamFullName.setMaxLines(TEAM_FULL_NAME_COLLAPSED_MAX_LINES);
            teamFullName.setEllipsize(TextUtils.TruncateAt.END);

            teamFullNameContainer.setOnClickListener((view) -> {
                toggleFullTeamNameExpanded();
            });
        }

        champsPitLocationContainer.setVisibility(View.GONE);
        if (PitLocationHelper.shouldShowPitLocation(mActivity, data.teamKey)) {
            PitLocationHelper.TeamPitLocation loc = PitLocationHelper.getPitLocation(mActivity, data.teamKey);
            if (loc != null){
                champsPitLocationContainer.setVisibility(View.VISIBLE);
                champsPitLocation.setText(loc.getAddressString());
            }
        }

        teamNextMatchLabel.setVisibility(View.GONE);
        teamNextMatchDetails.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);

        progress.setVisibility(View.GONE);

        content.setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        setDataBound(true);
    }

    @Override
    public void onComplete() {
        View progressBar = progress;
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

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
            content.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleFullTeamNameExpanded() {
        int currentMaxLines = teamFullName.getMaxLines();
        if (currentMaxLines == TEAM_FULL_NAME_COLLAPSED_MAX_LINES) {
            // The text view is collapsed, expand it

            final int height = teamFullName.getMeasuredHeight();

            teamFullName.setMaxLines(Integer.MAX_VALUE);
            teamFullName.measure(
                    View.MeasureSpec.makeMeasureSpec(teamFullName.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            final int newHeight = teamFullName.getMeasuredHeight();

            ObjectAnimator animation = ObjectAnimator.ofInt(teamFullName, "height", height, newHeight);
            animation.setDuration(500);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    teamFullName.setMaxLines(Integer.MAX_VALUE);
                    teamFullName.setMinHeight(0);
                }
            });
            animation.start();
        } else {
            // We need to collapse the text view

            final int height = teamFullName.getMeasuredHeight();

            // Only set max lines while we measure; max lines will be permanently
            // reduced one the animation completes
            teamFullName.setMaxLines(TEAM_FULL_NAME_COLLAPSED_MAX_LINES);
            teamFullName.measure(
                    View.MeasureSpec.makeMeasureSpec(teamFullName.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            teamFullName.setMaxLines(Integer.MAX_VALUE);

            final int newHeight = teamFullName.getMeasuredHeight();
            ObjectAnimator animation = ObjectAnimator.ofInt(teamFullName, "height", height, newHeight);
            animation.setDuration(500);
            animation.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    teamFullName.setMaxLines(TEAM_FULL_NAME_COLLAPSED_MAX_LINES);
                    teamFullName.setMinHeight(0);
                }
            });
            animation.start();
        }

    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            ButterKnife.unbind(this);
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
    }
}
