package com.thebluealliance.androidclient.binders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.helpers.RankingsHelper;
import com.thebluealliance.androidclient.helpers.StatsHelper;
import com.thebluealliance.androidclient.helpers.WebcastHelper;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.listeners.WebcastClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.types.WebcastType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.thebluealliance.androidclient.renderers.MatchRenderer.RENDER_DEFAULT;

public class EventInfoBinder extends AbstractDataBinder<EventInfoBinder.Model> {

    private LayoutInflater mInflater;
    private MatchRenderer mMatchRenderer;
    private boolean mIsLive;
    private boolean mAreViewsBound;

    @Inject SocialClickListener mSocialClickListener;
    @Inject EventInfoContainerClickListener mInfoClickListener;

    @Bind(R.id.content) View content;
    @Bind(R.id.event_name) TextView eventName;
    @Bind(R.id.event_date) TextView eventDate;
    @Bind(R.id.event_venue) TextView eventVenue;
    @Bind(R.id.top_teams) TextView topTeams;
    @Bind(R.id.top_oprs) TextView topOprs;
    @Bind(R.id.top_teams_container) View topTeamsContainer;
    @Bind(R.id.top_oprs_container) View topOprsContainer;
    @Bind(R.id.progress) ProgressBar progressBar;
    @Bind(R.id.event_date_container) View eventDateContainer;
    @Bind(R.id.event_venue_container) View eventVenueContainer;
    @Bind(R.id.event_website_container) View eventWebsiteContainer;
    @Bind(R.id.event_website_title) TextView eventWebsiteTitle;
    @Bind(R.id.event_twitter_container) View eventTwitterContainer;
    @Bind(R.id.event_twitter_title) TextView eventTwitterTitle;
    @Bind(R.id.event_youtube_container) View eventYoutubeContainer;
    @Bind(R.id.event_youtube_title) TextView eventYoutubeTitle;
    @Bind(R.id.event_cd_container) View eventCdContainer;
    @Bind(R.id.last_match_view) FrameLayout lastMatchView;
    @Bind(R.id.next_match_container) CardView nextMatchContainer;
    @Bind(R.id.next_match_view) FrameLayout nextMatchView;
    @Bind(R.id.last_match_container) CardView lastMatchContainer;
    @Bind(R.id.event_webcast_container) FrameLayout webcastContainer;
    @Bind((R.id.event_webcast_button)) Button webcastButton;

    @Inject
    public EventInfoBinder(MatchRenderer renderer,
      SocialClickListener socialClickListener,
      EventInfoContainerClickListener eventInfoContainerClickListener) {
        mSocialClickListener = socialClickListener;
        mInfoClickListener = eventInfoContainerClickListener;
        mMatchRenderer = renderer;
        mIsLive = false;
        mAreViewsBound = false;
    }

    public void setInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public void bindViews() {
        if (!mAreViewsBound) {
            ButterKnife.bind(this, mRootView);
            mAreViewsBound = true;
        }
    }

    @Override
    public void updateData(@Nullable Model data) {
        if (data == null) {
            if (!isDataBound()) {
                bindNoDataView();
            }
            return;
        }

        mSocialClickListener.setModelKey(data.eventKey);
        mIsLive = data.isLive;
        eventName.setText(data.nameString);
        if (data.dateString == null || data.dateString.isEmpty()) {
            eventDateContainer.setVisibility(View.GONE);
        } else {
            eventDate.setText(data.dateString);
        }

        // Show a venue if it is available, otherwise show just the location. If neither is available, hide
        if (data.venueString != null && !data.venueString.isEmpty()) {
            eventVenue.setText(data.venueString);
        } else if (data.locationString != null && !data.locationString.isEmpty()) {
            eventVenue.setText(data.locationString);
        } else {
            eventVenue.setText(R.string.no_location_available);
            eventVenueContainer.setVisibility(View.GONE);
        }

        // setup social media intents
        // Default to showing the nav arrow in the venue view and the venue view being clickable
        // We need to set these again even though they're defined in XML in case we gain a location
        // or venue on a refresh and we're reusing the same view.
        eventVenueContainer.setFocusable(true);
        eventVenueContainer.setClickable(true);
        eventVenueContainer.setOnClickListener(mSocialClickListener);

        if (data.venueString != null && !data.venueString.isEmpty()) {
            // Set the tag to the event venue if it is available
            eventVenueContainer.setTag("geo:0,0?q=" + Uri.encode(data.venueString));
        } else if (data.locationString != null && !data.locationString.isEmpty()) {
            // Otherwise, use the location
            eventVenueContainer.setTag("geo:0,0?q=" + Uri.encode(data.locationString));
        } else {
            // If neither location nor venue are available, hide the nav arrow, remove the tag,
            // and set the view to not clickable so the user cannot interact with it.
            // It will contain the text "No location available".
            eventVenueContainer.setTag(null);
            eventVenueContainer.setFocusable(false);
            eventVenueContainer.setClickable(false);
        }

        // If the event doesn't have a defined website, create a Google search for the event name
        if (data.eventWebsite != null && data.eventWebsite.isEmpty()) {
            eventWebsiteContainer.setTag("https://www.google.com/search?q=" + Uri.encode(data.nameString));
            eventWebsiteTitle.setText(R.string.find_event_on_google);
        } else {
            eventWebsiteContainer.setTag(data.eventWebsite);
            eventWebsiteTitle.setText(R.string.view_event_website);
        }
        eventWebsiteContainer.setOnClickListener(mSocialClickListener);

        eventTwitterContainer.setTag("https://twitter.com/search?q=%23" + data.eventKey);
        eventTwitterTitle.setText(mActivity.getString(R.string.view_event_twitter, data.eventKey));
        eventTwitterContainer.setOnClickListener(mSocialClickListener);

        eventYoutubeContainer.setTag("https://www.youtube.com/results?search_query=" + data.eventKey);
        eventYoutubeTitle.setText(mActivity.getString(R.string.view_event_youtube, data.eventKey));
        eventYoutubeContainer.setOnClickListener(mSocialClickListener);

        eventCdContainer.setTag("http://www.chiefdelphi.com/media/photos/tags/" + data.eventKey);
        eventCdContainer.setOnClickListener(mSocialClickListener);

        if (data.isLive && data.webcasts != null && data.webcasts.size() > 0) {
            if (data.webcasts.size() == 1) {
                // Only one webcast, we can link directly to that
                JsonObject eventWebcast = data.webcasts.get(0).getAsJsonObject();
                WebcastType webcastType = WebcastHelper.getType(eventWebcast.get("type")
                        .getAsString());
                webcastButton.setText(webcastType.render(mActivity));
                webcastButton.setOnClickListener(new WebcastClickListener(mActivity, data.eventKey,
                        webcastType, eventWebcast, 1));
            } else {
                webcastButton.setText(R.string.view_webcast_button);
                webcastButton.setOnClickListener(v -> {
                    Dialog chooserDialog = buildMultiWebcastDialog(data.webcasts, data.eventKey);
                    chooserDialog.show();
                });

            }
            webcastContainer.setVisibility(View.VISIBLE);
        }

        if (data.lastMatch != null) {
            //noinspection WrongThread
            showLastMatch(mMatchRenderer.renderFromModel(data.lastMatch, RENDER_DEFAULT));
        } else {
            hideLastMatch();
        }

        if (data.nextMatch != null) {
            //noinspection WrongThread
            showNextMatch(mMatchRenderer.renderFromModel(data.nextMatch, RENDER_DEFAULT));
        } else {
            hideNextMatch();
        }

        if (topTeamsContainer != null && topTeams != null && RankingsHelper.canGenerateTopRanksString(data.rankings)) {
            topTeamsContainer.setVisibility(View.VISIBLE);
            topTeamsContainer.setOnClickListener(mInfoClickListener);
            topTeams.setText(Html.fromHtml(RankingsHelper.generateTopRanksString(data.rankings, 5)));
        }

        if (topOprsContainer != null && topOprs != null && StatsHelper.canGenerateTopOprsString(data.stats)) {
            topOprsContainer.setVisibility(View.VISIBLE);
            topOprsContainer.setOnClickListener(mInfoClickListener);
            topOprs.setText(Html.fromHtml(StatsHelper.generateTopOprsString(data.stats, 5)));
        }


        content.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        EventBus.getDefault().post(new ActionBarTitleEvent(data.actionBarTitle, data.actionBarSubtitle));

        mNoDataBinder.unbindData();
        setDataBound(true);
    }

    @Override
    public void onComplete() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(Constants.LOG_TAG, Log.getStackTraceString(throwable));

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews) {
            ButterKnife.unbind(this);
            mAreViewsBound = false;
        }
    }

    private void bindNoDataView() {
        try {
            content.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Model {
        public String eventKey;
        public String actionBarTitle;
        public String actionBarSubtitle;
        public String nameString;
        public String dateString;
        public String venueString;
        public String locationString;
        public String eventWebsite;
        public boolean isLive;
        public JsonArray webcasts;
        public JsonArray rankings;
        public JsonObject stats;
        public Match lastMatch;
        public Match nextMatch;
    }

    protected void showLastMatch(MatchListElement match) {
        if (!mAreViewsBound) {
            bindViews();
        }
        lastMatchView.setVisibility(View.VISIBLE);
        lastMatchContainer.setVisibility(View.VISIBLE);
        lastMatchView.removeAllViews();
        lastMatchView.addView(match.getView(mActivity, mInflater, null));
    }

    protected void showNextMatch(MatchListElement match) {
        if (!mAreViewsBound) {
            bindViews();
        }
        nextMatchView.setVisibility(View.VISIBLE);
        nextMatchContainer.setVisibility(View.VISIBLE);
        nextMatchView.removeAllViews();
        nextMatchView.addView(match.getView(mActivity, mInflater, null));
    }

    protected void hideLastMatch() {
        if (!mAreViewsBound) {
            bindViews();
        }
        lastMatchContainer.setVisibility(View.GONE);
    }

    protected void hideNextMatch() {
        if (!mAreViewsBound) {
            bindViews();
        }
        nextMatchContainer.setVisibility(View.GONE);
    }

    private Dialog buildMultiWebcastDialog(final JsonArray webcasts, final String eventKey) {
        String[] choices = new String[webcasts.size()];
        for (int i = 0; i < webcasts.size(); i++) {
            JsonObject webcastDetails = webcasts.get(i).getAsJsonObject();
            WebcastType webcastType = WebcastHelper.getType(webcastDetails.get("type")
                    .getAsString());
            choices[i] = mActivity.getString(R.string.select_multi_webcast_stream_format, i + 1,
                    webcastType.render(mActivity));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.select_multi_webcast)
                .setItems(choices, (dialog, which) -> {
                    JsonObject details = webcasts.get(which).getAsJsonObject();
                    WebcastType type = WebcastHelper.getType(details.get("type").getAsString());
                    Intent intent = WebcastHelper.getIntentForWebcast(mActivity, eventKey, type,
                            details, which + 1);
                    try {
                        mActivity.startActivity(intent);
                        dialog.dismiss();
                    } catch (ActivityNotFoundException ex) {
                        // Unable to find an activity to handle the webcast
                        // Fall back by just opening Gameday in browser
                        String url = mActivity.getString(R.string.webcast_gameday_pattern,
                                eventKey, which + 1);
                        Intent gamedayIntent = WebcastHelper.getWebIntentForUrl(url);
                        mActivity.startActivity(gamedayIntent);
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
