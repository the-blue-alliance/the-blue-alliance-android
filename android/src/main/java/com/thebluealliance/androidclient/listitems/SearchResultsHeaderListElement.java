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
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_see_more, null);

            holder = new ViewHolder();
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.moreButton = (TextView) convertView.findViewById(R.id.more_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.label.setText(label);

        if (showMoreButton) {
            holder.moreButton.setVisibility(View.VISIBLE);
            holder.moreButton.setText(String.format(context.getString(R.string.more_results), moreCount));
        } else {
            holder.moreButton.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView label;
        TextView moreButton;
    }
}
