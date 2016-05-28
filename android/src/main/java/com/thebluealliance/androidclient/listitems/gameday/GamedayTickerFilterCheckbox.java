package com.thebluealliance.androidclient.listitems.gameday;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.ListElement;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class GamedayTickerFilterCheckbox extends ListElement {

    private int layout;
    private String text;
    private boolean checked;
    private String key;
    private CheckBox checkbox;

    public GamedayTickerFilterCheckbox(@LayoutRes int layout, String text, String key, boolean checked) {
        this.layout = layout;
        this.text = text;
        this.key = key;
        this.checked = checked;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        View view = inflater.inflate(layout, null);

        View listRow = view.findViewById(R.id.list_row);
        TextView textView = (TextView) view.findViewById(R.id.text);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);

        // Keep checkbox in this object so we can manipulate it externally
        this.checkbox = checkbox;

        listRow.setOnClickListener(v -> {
            checked = !checked;
            checkbox.setChecked(checked);
        });

        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> checked = isChecked);

        textView.setText(text);
        checkbox.setChecked(checked);

        return view;
    }

    public String getKey() {
        return key;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checkbox != null) {
            this.checkbox.setChecked(checked);
        }
    }
}
