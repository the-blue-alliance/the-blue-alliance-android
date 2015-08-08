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

    private LayoutInflater mInflater;
    private boolean mIsLive;

    public View mView;
    public View mContent;
    public TextView mEventName;
    public TextView mEventDate;
    public TextView mEventLoc;
    public TextView mEventVenue;
    public TextView mTopTeams;
    public TextView mTopOprs;
    public View mTopTeamsContainer;
    public View mTopOprsContainer;
    public ProgressBar mProgressBar;

    public void setInflator(LayoutInflater inflator) {
        mInflater = inflator;
    }

    //TODO this needs lots of cleanup. Move click events to their own listeners, no findviewbyid
    @Override
    public void updateData(@Nullable Model data) {
        if (data == null || mView == null) {
            setDataBound(false);
            return;
        }

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
                "https://twitter.com/search?q=%23" + data.eventKey);
        mView.findViewById(R.id.event_youtube_button).setTag(
                "https://www.youtube.com/results?search_query=" + data.eventKey);
        mView.findViewById(R.id.event_cd_button).setTag(
                "http://www.chiefdelphi.com/media/photos/tags/" + data.eventKey);

        mContent.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        EventBus.getDefault().post(new ActionBarTitleEvent(data.titleString));
        //EventBus.getDefault().post(new EventInfoLoadedEvent());

        mNoDataBinder.unbindData();
        setDataBound(true);
    }

    @Override
    public void onComplete() {
        if(mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }

        if(!isDataBound()) {
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
            mContent.setVisibility(View.GONE);
            mView.findViewById(R.id.no_data).setVisibility(View.GONE);
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
        LinearLayout lastLayout = (LinearLayout) mView.findViewById(R.id.event_last_match_container);
        lastLayout.setVisibility(View.VISIBLE);
        if (lastLayout.getChildCount() > 1) {
            lastLayout.removeViewAt(1);
        }
        lastLayout.addView(match.getView(mActivity, mInflater, null));
    }

    protected void showNextMatch(MatchListElement match) {
        LinearLayout nextLayout = (LinearLayout) mView.findViewById(R.id.event_next_match_container);
        nextLayout.setVisibility(View.VISIBLE);
        if (nextLayout.getChildCount() > 1) {
            nextLayout.removeViewAt(1);
        }
        nextLayout.addView(match.getView(mActivity, mInflater, null));
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
