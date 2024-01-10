package com.thebluealliance.androidclient.binders;

import static com.thebluealliance.androidclient.renderers.MatchRenderer.RENDER_DEFAULT;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.databinding.FragmentEventInfoBinding;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.eventbus.EventStatsEvent;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.helpers.WebcastHelper;
import com.thebluealliance.androidclient.listeners.EventInfoContainerClickListener;
import com.thebluealliance.androidclient.listeners.SocialClickListener;
import com.thebluealliance.androidclient.listeners.WebcastClickListener;
import com.thebluealliance.androidclient.listitems.MatchListElement;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.types.WebcastType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class EventInfoBinder extends AbstractDataBinder<EventInfoBinder.Model, FragmentEventInfoBinding> {

    private LayoutInflater mInflater;
    private MatchRenderer mMatchRenderer;
    private boolean mIsLive;
    private boolean mAreViewsBound;

    @Inject SocialClickListener mSocialClickListener;
    @Inject EventInfoContainerClickListener mInfoClickListener;

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
            mBinding = FragmentEventInfoBinding.bind(mRootView);
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
        mBinding.eventName.setText(data.nameString);
        if (data.dateString == null || data.dateString.isEmpty()) {
            mBinding.eventDateContainer.setVisibility(View.GONE);
        } else {
            mBinding.eventDate.setText(data.dateString);
        }

        // Show a venue if it is available, otherwise show just the location. If neither is available, hide
        if (data.venueString != null && !data.venueString.isEmpty()) {
            mBinding.eventVenue.setText(data.venueString);
        } else if (data.locationString != null && !data.locationString.isEmpty()) {
            mBinding.eventVenue.setText(data.locationString);
        } else {
            mBinding.eventVenue.setText(R.string.no_location_available);
            mBinding.eventVenueContainer.setVisibility(View.GONE);
        }

        // setup social media intents
        // Default to showing the nav arrow in the venue view and the venue view being clickable
        // We need to set these again even though they're defined in XML in case we gain a location
        // or venue on a refresh and we're reusing the same view.
        mBinding.eventVenueContainer.setFocusable(true);
        mBinding.eventVenueContainer.setClickable(true);
        mBinding.eventVenueContainer.setOnClickListener(mSocialClickListener);

        if (data.venueString != null && !data.venueString.isEmpty()) {
            // Set the tag to the event venue if it is available
            mBinding.eventVenueContainer.setTag("geo:0,0?q=" + Uri.encode(data.venueString));
        } else if (data.locationString != null && !data.locationString.isEmpty()) {
            // Otherwise, use the location
            mBinding.eventVenueContainer.setTag("geo:0,0?q=" + Uri.encode(data.locationString));
        } else {
            // If neither location nor venue are available, hide the nav arrow, remove the tag,
            // and set the view to not clickable so the user cannot interact with it.
            // It will contain the text "No location available".
            mBinding.eventVenueContainer.setTag(null);
            mBinding.eventVenueContainer.setFocusable(false);
            mBinding.eventVenueContainer.setClickable(false);
        }

        // If the event doesn't have a defined website, create a Google search for the event name
        if (data.eventWebsite != null && data.eventWebsite.isEmpty()) {
            mBinding.eventWebsiteContainer.setTag("https://www.google.com/search?q=" + Uri.encode(data.nameString));
            mBinding.eventWebsiteTitle.setText(R.string.find_event_on_google);
        } else {
            mBinding.eventWebsiteContainer.setTag(data.eventWebsite);
            mBinding.eventWebsiteTitle.setText(R.string.view_event_website);
        }
        mBinding.eventWebsiteContainer.setOnClickListener(mSocialClickListener);

        mBinding.eventTwitterContainer.setTag("https://twitter.com/search?q=%23" + data.eventKey);
        mBinding.eventTwitterTitle.setText(mActivity.getString(R.string.view_event_twitter, data.eventKey));
        mBinding.eventTwitterContainer.setOnClickListener(mSocialClickListener);

        mBinding.eventYoutubeContainer.setTag("https://www.youtube.com/results?search_query=" + data.eventKey);
        mBinding.eventYoutubeTitle.setText(mActivity.getString(R.string.view_event_youtube, data.eventKey));
        mBinding.eventYoutubeContainer.setOnClickListener(mSocialClickListener);

        mBinding.eventCdContainer.setTag("https://www.chiefdelphi.com/search?q=category%3A11%20tags%3A" + data.eventKey);
        mBinding.eventCdTitle.setOnClickListener(mSocialClickListener);

        if (data.isLive && data.webcasts != null && data.webcasts.size() > 0) {
            if (data.webcasts.size() == 1) {
                // Only one webcast, we can link directly to that
                JsonObject eventWebcast = data.webcasts.get(0).getAsJsonObject();
                WebcastType webcastType = WebcastHelper.getType(eventWebcast.get("type")
                        .getAsString());
                mBinding.eventWebcastButton.setText(webcastType.render(mActivity));
                mBinding.eventWebcastButton.setOnClickListener(new WebcastClickListener(mActivity, data.eventKey,
                        webcastType, eventWebcast, 1));
            } else {
                mBinding.eventWebcastButton.setText(R.string.view_webcast_button);
                mBinding.eventWebcastButton.setOnClickListener(v -> {
                    Dialog chooserDialog = buildMultiWebcastDialog(data.webcasts, data.eventKey);
                    chooserDialog.show();
                });

            }
            mBinding.eventWebcastContainer.setVisibility(View.VISIBLE);
        }

        mBinding.content.setVisibility(View.VISIBLE);
        mBinding.progress.setVisibility(View.GONE);

        EventBus.getDefault().post(new ActionBarTitleEvent(data.actionBarTitle, data.actionBarSubtitle));

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
        TbaLogger.e(TbaLogger.getStackTraceString(throwable));

        // If we received valid data from the cache but get an error from the network operations,
        // don't display the "No data" message.
        if (!isDataBound()) {
            bindNoDataView();
        }
    }

    @Override
    public void unbind(boolean unbindViews) {
        super.unbind(unbindViews);
        if (unbindViews && mAreViewsBound) {
            mBinding = null;
            mAreViewsBound = false;
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
    }

    protected void showLastMatch(MatchListElement match) {
        if (!mAreViewsBound) {
            bindViews();
        }
        mBinding.lastMatchView.setVisibility(View.VISIBLE);
        mBinding.lastMatchContainer.setVisibility(View.VISIBLE);
        mBinding.lastMatchView.removeAllViews();
        mBinding.lastMatchView.addView(match.getView(mActivity, mInflater, null));
    }

    protected void showNextMatch(MatchListElement match) {
        if (!mAreViewsBound) {
            bindViews();
        }
        mBinding.nextMatchView.setVisibility(View.VISIBLE);
        mBinding.nextMatchContainer.setVisibility(View.VISIBLE);
        mBinding.nextMatchView.removeAllViews();
        mBinding.nextMatchView.addView(match.getView(mActivity, mInflater, null));
    }

    protected void hideLastMatch() {
        if (!mAreViewsBound) {
            bindViews();
        }
        mBinding.lastMatchContainer.setVisibility(View.GONE);
    }

    protected void hideNextMatch() {
        if (!mAreViewsBound) {
            bindViews();
        }
        mBinding.nextMatchContainer.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onLiveEventMatchesUpdated(LiveEventMatchUpdateEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mIsLive && event != null && event.getLastMatch() != null) {
                TbaLogger.d("showing last match");
                showLastMatch(mMatchRenderer.renderFromModel(event.getLastMatch(), RENDER_DEFAULT));
            } else {
                TbaLogger.d("hiding last match");
                hideLastMatch();
            }
            if (mIsLive && event != null && event.getNextMatch() != null) {
                TbaLogger.d("showing next match");
                showNextMatch(mMatchRenderer.renderFromModel(event.getNextMatch(), RENDER_DEFAULT));
            } else {
                TbaLogger.d("hiding next match");
                hideNextMatch();
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEventRankingsUpdated(EventRankingsEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mBinding == null) {
                return;
            }
            mBinding.topTeamsContainer.setVisibility(View.VISIBLE);
            mBinding.topTeamsContainer.setOnClickListener(mInfoClickListener);
            mBinding.topTeams.setText(Html.fromHtml(event.getRankString()));
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEventStatsUpdated(EventStatsEvent event) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mBinding == null) {
                return;
            }
            mBinding.topOprsContainer.setVisibility(View.VISIBLE);
            mBinding.topOprsContainer.setOnClickListener(mInfoClickListener);
            mBinding.topOprs.setText(Html.fromHtml(event.getStatString()));
        });
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
