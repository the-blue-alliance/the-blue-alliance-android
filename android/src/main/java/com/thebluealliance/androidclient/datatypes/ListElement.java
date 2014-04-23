package com.thebluealliance.androidclient.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;

/**
 * File created by phil on 4/20/14.
 */
public class ListElement implements ListItem {
    protected final String texts[],key;
    protected View view;
    protected boolean selected=false;

    public ListElement(){
        texts = new String[0];
        key = "";
    }

    public ListElement(String key, String... text) {
        this.texts = text;
        this.key = key;
    }

    @Override
    public int getViewType() {
        return ListViewAdapter.ItemType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if(view == null){
            view = (View) inflater.inflate(android.R.layout.simple_list_item_1, null);
            view.setTag(key);
            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            text1.setText(texts[0]);
            text1.setSelected(selected);
            if(text1.isSelected()){
                text1.setBackgroundResource(android.R.color.holo_blue_light);
            }else{
                text1.setBackgroundResource(android.R.color.transparent);
            }
        }
        return view;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public String getKey(){
        return key;
    }
}
