package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datatypes.ListGroup;

/**
 * File created by phil on 4/22/14.
 */
public abstract class ExpandableListAdapter extends BaseExpandableListAdapter {
    public final SparseArray<ListGroup> groups;
    public LayoutInflater inflater;
    public Activity activity;
    private boolean mIsChildSelectable = false;

    public ExpandableListAdapter(Activity act, SparseArray<ListGroup> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    public Object getChildKey(int groupPosition, int childPosition) {
        return groups.get(groupPosition).childrenKeys.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groups == null || groups.get(groupPosition) == null)
            return 0;
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.expandable_list_group, null);
        }
        ListGroup group = (ListGroup) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return mIsChildSelectable;
    }

    public void setChildSelectable(boolean isSelectable) {
        mIsChildSelectable = isSelectable;
    }
}
