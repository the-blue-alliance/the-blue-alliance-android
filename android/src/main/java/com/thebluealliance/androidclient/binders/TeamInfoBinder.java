package com.thebluealliance.androidclient.binders;

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

    public TextView mTeamName;
    public TextView mTeamLocation;

    public View mView;
    public View mContent;
    public View mTeamLocationContainer;

    @Override
    public void updateData(@Nullable TeamInfoBinder.Model data) {
        if (data == null || mView == null) {
            setDataBound(false);
            return;
        }

        if (data.nickname.isEmpty()) {
            mTeamName.setText("Team " + data.teamNumber);
        } else {
            mTeamName.setText(data.nickname);
        }

        if (data.location.isEmpty()) {
            // No location; hide the location view
            mTeamLocationContainer.setVisibility(View.GONE);
        } else {
            // Show and populate the location view
            mTeamLocation.setText(data.location);

            // Tag is used to create an ACTION_VIEW intent for a maps application
            mTeamLocationContainer
                    .setTag("geo:0,0?q=" + data.location.replace(" ", "+"));
        }

        mView.findViewById(R.id.team_twitter_button)
                .setTag("https://twitter" + ".com/search?q=%23" + data.teamKey);
        mView.findViewById(R.id.team_youtube_button)
                .setTag("https://www.youtube" + ".com/results?search_query=" + data.teamKey);
        mView.findViewById(R.id.team_cd_button)
                .setTag("http://www.chiefdelphi" + ".com/media/photos/tags/" + data.teamKey);
        mView.findViewById(R.id.team_website_button)
                .setTag(!data.website.isEmpty() ? data.website :
                        "https://www.google" + ".com/search?q=" + data.teamKey);
        if (data.fullName.isEmpty()) {
            // No full name specified, hide the view
            mView.findViewById(R.id.team_full_name_container).setVisibility(View.GONE);
        } else {
            // This string needs to be specially formatted
            SpannableString string = new SpannableString("aka " + data.fullName);
            string.setSpan(new TextAppearanceSpan(mActivity,
                    R.style.InfoItemLabelStyle), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ((TextView) mView.findViewById(R.id.team_full_name)).setText(string);
        }

        mView.findViewById(R.id.team_next_match_label).setVisibility(View.GONE);
        mView.findViewById(R.id.team_next_match_details).setVisibility(View.GONE);
        mView.findViewById(R.id.content).setVisibility(View.VISIBLE);

        mView.findViewById(R.id.progress).setVisibility(View.GONE);

        mView.findViewById(R.id.content).setVisibility(View.VISIBLE);
        mNoDataBinder.unbindData();
        setDataBound(true);

    }

    @Override
    public void onComplete() {
        View progressBar = mView.findViewById(R.id.progress);
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if(!isDataBound()) {
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
            mContent.setVisibility(View.GONE);
            mView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
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
        public int teamNumber;
    }
}
