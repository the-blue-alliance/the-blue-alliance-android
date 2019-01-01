package com.thebluealliance.androidclient.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.thebluealliance.androidclient.R;

import java.util.Arrays;
import java.util.List;

public class DialogListWithIconsAdapter extends ArrayAdapter<String> {

    private List<Integer> images;

    public DialogListWithIconsAdapter(Context context, List<String> items, List<Integer> images) {
        super(context, R.layout.list_item_dialog_with_icon, R.id.text, items);
        this.images = images;
    }

    public DialogListWithIconsAdapter(Context context, String[] items, Integer[] images) {
        super(context, R.layout.list_item_dialog_with_icon, R.id.text, items);
        this.images = Arrays.asList(images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(images.get(position));
        return view;
    }

}
