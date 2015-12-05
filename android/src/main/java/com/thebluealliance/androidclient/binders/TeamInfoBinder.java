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

public class TeamInfoBinder extends AbstractDataBinder<TeamInfoBinder.Model> {

    public TextView teamName;
    public TextView teamLocation;
    public TextView teamMotto;

    public View view;
    public View content;
    public View teamLocationContainer;
    public View teamMottoContainer;

    @Override
    public void updateData(@Nullable TeamInfoBinder.Model data) {
        if (data == null || view == null) {
            setDataBound(false);
            return;
        }

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

        if (data.motto.isEmpty()) {
            // No location; hide the location view
            teamMottoContainer.setVisibility(View.GONE);
        } else {
            // Show and populate the location view
            teamMotto.setText(data.motto);
        }

        // If the team doesn't have a defined website, create a Google search for the team name
        if (data.website.isEmpty()) {
            view.findViewById(R.id.team_website_container).setTag("https://www.google.com/search?q=" + Uri.encode(data.nickname));
            ((TextView) view.findViewById(R.id.team_website_title)).setText(R.string.find_event_on_google);
        } else {
            view.findViewById(R.id.team_website_container).setTag(data.website);
            ((TextView) view.findViewById(R.id.team_website_title)).setText(R.string.view_team_website);
        }

        view.findViewById(R.id.team_twitter_container).setTag("https://twitter.com/search?q=%23" + data.teamKey);
        ((TextView) view.findViewById(R.id.team_twitter_title)).setText(mActivity.getString(R.string.view_team_twitter, data.teamKey));

        view.findViewById(R.id.team_youtube_container).setTag("https://www.youtube.com/results?search_query=" + data.teamKey);
        ((TextView) view.findViewById(R.id.team_youtube_title)).setText(mActivity.getString(R.string.view_team_youtube, data.teamKey));

        view.findViewById(R.id.team_cd_container).setTag("http://www.chiefdelphi.com/media/photos/tags/" + data.teamKey);

        if (data.fullName.isEmpty()) {
            // No full name specified, hide the view
            view.findViewById(R.id.team_full_name_container).setVisibility(View.GONE);
        } else {
            // This string needs to be specially formatted
            SpannableString string = new SpannableString("aka " + data.fullName);
            string.setSpan(new TextAppearanceSpan(mActivity,
                    R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ((TextView) view.findViewById(R.id.team_full_name)).setText(string);
        }

        view.findViewById(R.id.team_next_match_label).setVisibility(View.GONE);
        view.findViewById(R.id.team_next_match_details).setVisibility(View.GONE);
        view.findViewById(R.id.content).setVisibility(View.VISIBLE);

        view.findViewById(R.id.progress).setVisibility(View.GONE);

        view.findViewById(R.id.content).setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        setDataBound(true);

    }

    @Override
    public void onComplete() {
        View progressBar = view.findViewById(R.id.progress);
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
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            mNoDataBinder.bindData(mNoDataParams);
        } catch (Exception e) {
            e.printStackTrace();
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
