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
import com.thebluealliance.androidclient.listitems.MatchListElement;

import javax.annotation.Nullable;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;

public class EventInfoBinder extends AbstractDataBinder<EventInfoBinder.Model> {

    private LayoutInflater mInflater;
    private boolean mIsLive;

    public View view;
    public View content;
    public TextView eventName;
    public TextView eventDate;
    public TextView eventLoc;
    public TextView eventVenue;
    public TextView topTeams;
    public TextView topOprs;
    public View topTeamsContainer;
    public View topOprsContainer;
    public ProgressBar progressBar;

    public void setInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }

    //TODO this needs lots of cleanup. Move click events to their own listeners, no findviewbyid
    @Override
    public void updateData(@Nullable Model data) {
        if (data == null || view == null) {
            setDataBound(false);
            return;
        }

        mIsLive = data.isLive;
        eventName.setText(data.nameString);
        if (data.dateString == null || data.dateString.isEmpty()) {
            view.findViewById(R.id.event_date_container).setVisibility(View.GONE);
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
            view.findViewById(R.id.event_venue_container).setVisibility(View.GONE);
        }

        // setup social media intents
        // Default to showing the nav arrow in the venue view and the venue view being clickable
        // We need to set these again even though they're defined in XML in case we gain a location
        // or venue on a refresh and we're reusing the same view.

        View eventVenueContainer = view.findViewById(R.id.event_venue_container);
        eventVenueContainer.setFocusable(true);
        eventVenueContainer.setClickable(true);

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
            view.findViewById(R.id.event_website_container).setTag("https://www.google.com/search?q=" + Uri.encode(data.nameString));
            ((TextView) view.findViewById(R.id.event_website_title)).setText(R.string.find_event_on_google);
        } else {
            view.findViewById(R.id.event_website_container).setTag(data.eventWebsite);
            ((TextView) view.findViewById(R.id.event_website_title)).setText(R.string.view_event_website);
        }

        view.findViewById(R.id.event_twitter_container).setTag("https://twitter.com/search?q=%23" + data.eventKey);
        ((TextView) view.findViewById(R.id.event_twitter_title)).setText(mActivity.getString(R.string.view_event_twitter, data.eventKey));

        view.findViewById(R.id.event_youtube_container).setTag("https://www.youtube.com/results?search_query=" + data.eventKey);
        ((TextView) view.findViewById(R.id.event_youtube_title)).setText(mActivity.getString(R.string.view_event_youtube, data.eventKey));

        view.findViewById(R.id.event_cd_container).setTag("http://www.chiefdelphi.com/media/photos/tags/" + data.eventKey);

        content.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        EventBus.getDefault().post(new ActionBarTitleEvent(data.titleString));
        //EventBus.getDefault().post(new EventInfoLoadedEvent());

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
        FrameLayout matchView = (FrameLayout) view.findViewById(R.id.last_match_view);
        matchView.setVisibility(View.VISIBLE);
        matchView.removeAllViews();
        matchView.addView(match.getView(mActivity, mInflater, null));
    }

    protected void showNextMatch(MatchListElement match) {
        FrameLayout matchView = (FrameLayout) view.findViewById(R.id.next_match_view);
        matchView.setVisibility(View.VISIBLE);
        matchView.removeAllViews();
        matchView.addView(match.getView(mActivity, mInflater, null));
    }

    public void onEvent(LiveEventMatchUpdateEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mIsLive && event.getLastMatch() != null) {
                Log.d(Constants.LOG_TAG, "showing last match");
                showLastMatch(event.getLastMatch().render());
            }
            if (mIsLive && event.getNextMatch() != null) {
                Log.d(Constants.LOG_TAG, "showing next match");
                showNextMatch(event.getNextMatch().render());
            }
        });
    }

    public void onEvent(EventRankingsEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            topTeamsContainer.setVisibility(View.VISIBLE);
            topTeams.setText(Html.fromHtml(event.getRankString()));
        });
    }

    public void onEvent(EventStatsEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            topOprsContainer.setVisibility(View.VISIBLE);
            topOprs.setText(Html.fromHtml(event.getStatString()));
        });
    }
}
