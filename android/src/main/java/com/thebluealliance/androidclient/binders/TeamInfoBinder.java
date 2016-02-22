package com.thebluealliance.androidclient.binders;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.SocialClickListener;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TeamInfoBinder extends AbstractDataBinder<TeamInfoBinder.Model> {

    @Inject SocialClickListener mSocialClickListener;

    @Bind(R.id.content) View content;
    @Bind(R.id.team_name) TextView teamName;
    @Bind(R.id.team_location_container) View teamLocationContainer;
    @Bind(R.id.team_location) TextView teamLocation;

    @Bind(R.id.team_website_container) View teamWebsiteContainer;
    @Bind(R.id.team_website_title) TextView teamWebsiteTitle;
    @Bind(R.id.team_twitter_container) View teamTwitterContainer;
    @Bind(R.id.team_twitter_title) TextView teamTwitterTitle;
    @Bind(R.id.team_youtube_container) View teamYoutubeContainer;
    @Bind(R.id.team_youtube_title) TextView teamYoutubeTitle;
    @Bind(R.id.team_cd_container) View teamCdContainer;
    @Bind(R.id.team_full_name_container) View teamFullNameContainer;
    @Bind(R.id.team_full_name) TextView teamFullName;
    @Bind(R.id.team_next_match_label) View teamNextMatchLabel;
    @Bind(R.id.team_next_match_details) View teamNextMatchDetails;
    @Bind(R.id.progress) View progress;
    @Bind(R.id.team_motto_container) View teamMottoContainer;
    @Bind(R.id.team_motto) TextView teamMotto;

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

        teamTwitterContainer.setTag("https://twitter.com/search?q=%23" + data.teamKey);
        teamTwitterContainer.setOnClickListener(mSocialClickListener);
        teamTwitterTitle.setText(mActivity.getString(R.string.view_team_twitter, data.teamKey));

        teamYoutubeContainer.setTag("https://www.youtube.com/results?search_query=" + data.teamKey);
        teamYoutubeContainer.setOnClickListener(mSocialClickListener);
        teamYoutubeTitle.setText(mActivity.getString(R.string.view_team_youtube, data.teamKey));

        teamCdContainer.setTag("http://www.chiefdelphi.com/media/photos/tags/" + data.teamKey);
        teamCdContainer.setOnClickListener(mSocialClickListener);

        if (data.fullName.isEmpty()) {
            // No full name specified, hide the view
            teamFullNameContainer.setVisibility(View.GONE);
        } else {
            // This string needs to be specially formatted
            SpannableString string = new SpannableString("aka " + data.fullName);
            string.setSpan(new TextAppearanceSpan(mActivity,
                    R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            teamFullName.setText(string);
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
        Log.e(Constants.LOG_TAG, throwable.toString());

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
    }
}
