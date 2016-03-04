package com.thebluealliance.androidclient.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    public final List<ListGroup> mGroups;
    public LayoutInflater mInflater;
    public Activity mActivity;

    private boolean mIsChildSelectable = true;
    protected ModelRendererSupplier mRendererSupplier;

    public ExpandableListViewAdapter() {
        mGroups = new ArrayList<>();
    }

    public ExpandableListViewAdapter(Activity activity, ModelRendererSupplier supplier, List<ListGroup> groups) {
        mActivity = activity;
        mRendererSupplier = supplier;
        mGroups = groups;
        mInflater = activity.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mGroups == null || mGroups.get(groupPosition) == null)
            return 0;
        return mGroups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    public void addGroup(int position, ListGroup group) {
        mGroups.add(position, group);
    }

    public void addGroup(ListGroup group) {
        mGroups.add(group);
    }

    public void addAllGroups(List<ListGroup> groups) {
        mGroups.addAll(groups);
    }

    public void removeAllGroups() {
        mGroups.clear();
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
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
            convertView = mInflater.inflate(R.layout.expandable_list_group, null);
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
        return mGroups.get(groupPosition).children.get(childPosition)
                .render(mRendererSupplier).getView(mActivity, mInflater, convertView);
    }
}

