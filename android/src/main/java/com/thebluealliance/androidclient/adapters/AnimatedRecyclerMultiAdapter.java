package com.thebluealliance.androidclient.adapters;

import java.util.List;

import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.builders.BindableLayoutBuilder;
import io.nlopez.smartadapters.utils.Mapper;

/**
 * Based on https://github.com/Wrdlbrnft/Searchable-RecyclerView-Demo/blob/master/app/src/main/java/com/github/wrdlbrnft/searchablerecyclerviewdemo/ui/adapter/ExampleAdapter.java
 */
public class AnimatedRecyclerMultiAdapter extends RecyclerMultiAdapter {
    public AnimatedRecyclerMultiAdapter(Mapper mapper, List listItems) {
        super(mapper, listItems);
    }

    public AnimatedRecyclerMultiAdapter(Mapper mapper, List listItems, BindableLayoutBuilder builder) {
        super(mapper, listItems, builder);
    }

    public void animateTo(List<Object> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }


    private void applyAndAnimateRemovals(List newModels) {
        for (int i = listItems.size() - 1; i >= 0; i--) {
            final Object model = listItems.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Object model = newModels.get(i);
            if (!listItems.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Object model = newModels.get(toPosition);
            final int fromPosition = listItems.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Object removeItem(int position) {
        final Object model = listItems.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Object model) {
        listItems.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Object model = listItems.remove(fromPosition);
        listItems.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
