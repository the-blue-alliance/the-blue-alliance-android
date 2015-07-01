package com.thebluealliance.androidclient.binders;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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

    private LayoutInflater mInflator;
    private boolean mIsLive;

    public View mView;
    public TextView mEventName;
    public TextView  mEventDate;
    public TextView  mEventLoc;
    public TextView  mEventVenue;
    public TextView  mTopTeams;
    public TextView  mTopOprs;
    public View  mTopTeamsContainer;
    public View  mTopOprsContainer;
    public ProgressBar mProgressBar;

    public void setInflator(LayoutInflater inflator) {
        mInflator = inflator;
    }

    //TODO this needs lots of cleanup. Move click events to their own listeners, no findviewbyid
    @Override
    public void updateData(@Nullable Model data) {
        TextView noDataText = (TextView) mView.findViewById(R.id.no_data);
        View infoContainer = mView.findViewById(R.id.event_info_container);
        if (data == null) {
            noDataText.setText(R.string.no_data);
            noDataText.setVisibility(View.VISIBLE);
            infoContainer.setVisibility(View.GONE);
        } else {
            noDataText.setVisibility(View.GONE);

            mIsLive = data.isLive;
            mEventName.setText(data.nameString);
            if (data.dateString.isEmpty()) {
                mView.findViewById(R.id.event_date_container).setVisibility(View.GONE);
            } else {
                mEventDate.setText(data.dateString);
            }

            // Show a venue if it is available, otherwise show just the location. If neither is available, hide
            if (!data.venueString.isEmpty()) {
                mEventVenue.setText(data.venueString);
            } else if (!data.locationString.isEmpty()) {
                mEventVenue.setText(data.locationString);
            } else {
                mEventVenue.setText(R.string.no_location_available);
                mView.findViewById(R.id.event_venue_container).setVisibility(View.GONE);
            }

            // setup social media intents
            // Default to showing the nav arrow in the venue view and the venue view being clickable
            // We need to set these again even though they're defined in XML in case we gain a location
            // or venue on a refresh and we're reusing the same view.
            mView.findViewById(R.id.event_venue_nav_arrow).setVisibility(View.VISIBLE);
            mView.setFocusable(true);
            mView.setClickable(true);

            if (!data.venueString.isEmpty()) {
                // Set the tag to the event venue if it is available
                mView.findViewById(R.id.event_venue_container).setTag(
                  "geo:0,0?q=" + data.venueString.replace(" ", "+"));
            } else if (!data.locationString.isEmpty()) {
                // Otherwise, use the location
                mView.findViewById(R.id.event_venue_container).setTag(
                  "geo:0,0?q=" + data.locationString.replace(" ", "+"));
            } else {
                // If neither location nor venue are available, hide the nav arrow, remove the tag,
                // and set the view to not clickable so the user cannot interact with it.
                // It will contain the text "No location available".
                mView.findViewById(R.id.event_venue_container).setTag(null);
                mView.findViewById(R.id.event_venue_nav_arrow).setVisibility(View.GONE);
                mView.setFocusable(false);
                mView.setClickable(false);
            }

            mView.findViewById(R.id.event_website_button).setTag(
              !data.eventWebsite.isEmpty()
                ? data.eventWebsite
                : "https://www.google.com/search?q=" + data.nameString);
            mView.findViewById(R.id.event_twitter_button).setTag(
              "https://twitter.com/search?q=%23" + data.eventKey );
            mView.findViewById(R.id.event_youtube_button).setTag(
              "https://www.youtube.com/results?search_query=" + data.eventKey);
            mView.findViewById(R.id.event_cd_button).setTag(
              "http://www.chiefdelphi.com/media/photos/tags/" + data.eventKey);

            infoContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            EventBus.getDefault().post(new ActionBarTitleEvent(data.titleString));
            //EventBus.getDefault().post(new EventInfoLoadedEvent());
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable throwable) {
        Log.e(Constants.LOG_TAG, Log.getStackTraceString(throwable));
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
        LinearLayout lastLayout = (LinearLayout) mView.findViewById(R.id.event_last_match_container);
        lastLayout.setVisibility(View.VISIBLE);
        if (lastLayout.getChildCount() > 1) {
            lastLayout.removeViewAt(1);
        }
        lastLayout.addView(match.getView(mActivity, mInflator, null));
    }

    protected void showNextMatch(MatchListElement match) {
        LinearLayout nextLayout = (LinearLayout) mView.findViewById(R.id.event_next_match_container);
        nextLayout.setVisibility(View.VISIBLE);
        if (nextLayout.getChildCount() > 1) {
            nextLayout.removeViewAt(1);
        }
        nextLayout.addView(match.getView(mActivity, mInflator, null));
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
            mTopTeamsContainer.setVisibility(View.VISIBLE);
            mTopTeams.setText(Html.fromHtml(event.getRankString()));
        });
    }

    public void onEvent(EventStatsEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            mTopOprsContainer.setVisibility(View.VISIBLE);
            mTopOprs.setText(Html.fromHtml(event.getStatString()));
        });
    }
}
