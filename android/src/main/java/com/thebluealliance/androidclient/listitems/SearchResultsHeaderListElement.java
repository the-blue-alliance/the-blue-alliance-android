package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * Created by Nathan on 6/14/2014.
 */
public class SearchResultsHeaderListElement extends ListElement {

    private String label;
    private int moreCount;
    private boolean showMoreButton;

    public SearchResultsHeaderListElement(String label) {
        this.label = label;
    }

    public void showMoreButton(boolean show) {
        showMoreButton = show;
    }

    public boolean isShowingMoreButton() {
        return showMoreButton;
    }

    public void setMoreCount(int more) {
        moreCount = more;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_see_more, null);

            ((TextView) view.findViewById(R.id.label)).setText(label);

            TextView moreButton = ((TextView) view.findViewById(R.id.more_button));
            if (showMoreButton) {
                moreButton.setVisibility(View.VISIBLE);
                moreButton.setText(String.format(context.getString(R.string.more_results), moreCount));
                view.setBackgroundResource(R.drawable.search_results_header_background);
            } else {
                moreButton.setVisibility(View.GONE);
                view.setBackgroundColor(context.getResources().getColor(R.color.more_results_default));
            }
        }
        return view;
    }
}
