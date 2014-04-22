package com.thebluealliance.androidclient.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;

/**
 * File created by phil on 4/20/14.
 */
public class ListElement implements ListItem {
    private final String str1,key;
    private View view;
    private boolean selected=false;

    public ListElement(){
        str1 = "";
        key = "";
    }

    public ListElement(String text1,String key) {
        this.str1 = text1;
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

            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            text1.setText(str1);
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
