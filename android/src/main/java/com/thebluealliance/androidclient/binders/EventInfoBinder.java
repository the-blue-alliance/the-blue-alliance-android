package com.thebluealliance.androidclient.binders;

import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.eventbus.EventStatsEvent;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import javax.annotation.Nullable;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;

import static com.thebluealliance.androidclient.renderers.MatchRenderer.RENDER_DEFAULT;

public class EventInfoBinder extends AbstractDataBinder<EventInfoBinder.Model> {

    private LayoutInflater mInflater;
    private MatchRenderer mMatchRenderer;
    private boolean mIsLive;

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
    @Bind(R.id.next_match_view) FrameLayout nextMatchView;

    @Inject
    public EventInfoBinder(MatchRenderer renderer,
      SocialClickListener socialClickListener,
      EventInfoContainerClickListener eventInfoContainerClickListener) {
        mSocialClickListener = socialClickListener;
        mInfoClickListener = eventInfoContainerClickListener;
        mMatchRenderer = renderer;
    }

    public void setInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }

    @Override
    public void bindViews() {
        ButterKnife.bind(this, mRootView);
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

        content.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        EventBus.getDefault().post(new ActionBarTitleEvent(data.titleString));

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
        public String nameString;
        public String dateString;
        public String venueString;
        public String locationString;
        public String eventWebsite;
        public String titleString;
        public boolean isLive;
    }

    protected void showLastMatch(MatchListElement match) {
        FrameLayout matchView = lastMatchView;
        matchView.setVisibility(View.VISIBLE);
        matchView.removeAllViews();
        matchView.addView(match.getView(mActivity, mInflater, null));
    }

    protected void showNextMatch(MatchListElement match) {
        FrameLayout matchView = nextMatchView;
        matchView.setVisibility(View.VISIBLE);
        matchView.removeAllViews();
        matchView.addView(match.getView(mActivity, mInflater, null));
    }

    @SuppressWarnings("unused")
    public void onEvent(LiveEventMatchUpdateEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mIsLive && event.getLastMatch() != null) {
                Log.d(Constants.LOG_TAG, "showing last match");
                showLastMatch(mMatchRenderer.renderFromModel(event.getLastMatch(), RENDER_DEFAULT));
            }
            if (mIsLive && event.getNextMatch() != null) {
                Log.d(Constants.LOG_TAG, "showing next match");
                showNextMatch(mMatchRenderer.renderFromModel(event.getNextMatch(), RENDER_DEFAULT));
            }
        });
    }

    @SuppressWarnings("unused")
    public void onEvent(EventRankingsEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            topTeamsContainer.setVisibility(View.VISIBLE);
            topTeamsContainer.setOnClickListener(mInfoClickListener);
            topTeams.setText(Html.fromHtml(event.getRankString()));
        });
    }

    @SuppressWarnings("unused")
    public void onEvent(EventStatsEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            topOprsContainer.setVisibility(View.VISIBLE);
            topOprsContainer.setOnClickListener(mInfoClickListener);
            topOprs.setText(Html.fromHtml(event.getStatString()));
        });
    }
}
