package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.ListGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * File created by phil on 4/22/14.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public final List<ListGroup> groups;
    public LayoutInflater inflater;
    public Activity mActivity;
    private boolean mIsChildSelectable = true;

    public ExpandableListAdapter() {
        groups = new ArrayList<>();
    }

    public ExpandableListAdapter(Activity activity, List<ListGroup> groups) {
        mActivity = activity;
        this.groups = groups;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
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

    public void addGroup(int position, ListGroup group) {
        groups.add(position, group);
    }

    public void addGroup(ListGroup group) {
        groups.add(group);
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
        ((TextView) convertView.findViewById(R.id.group_name)).setText(group.string);

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

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return groups.get(groupPosition).children.get(childPosition).render().getView(mActivity, inflater, convertView);
    }
}

