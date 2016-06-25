package com.thebluealliance.androidclient.adapters;

import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.interfaces.BindableAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * A subclass of {@link FragmentPagerAdapter} that stores each fragment in a {@link Hashtable} so
 * that they can be accessed and bound at any given time.
 */
public abstract class BindableFragmentPagerAdapter extends FragmentPagerAdapter implements BindableAdapter {

    private Hashtable<Long, WeakReference<Fragment>> mFragments;

    public BindableFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new Hashtable<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        mFragments.put(getItemId(position), new WeakReference<>(createdFragment));
        return createdFragment;
    }

    /**
     * Binds now (if there's data) and auto-binds until the Observable completes
     */
    @Override
    public void setAutoBindOnceAtPosition(int position, boolean autoBind) {
        WeakReference<Fragment> ref = mFragments.get(getItemId(position));
        if (ref == null) {
            return;
        }
        Fragment f = ref.get();
        if (f != null && f instanceof DatafeedFragment) {
            DatafeedFragment df = (DatafeedFragment) f;
            df.setShouldBindOnce(autoBind);
            df.bind();
        }
    }

    @Override
    public void bindFragmentAtPosition(int position) {
        WeakReference<Fragment> ref = mFragments.get(getItemId(position));
        if (ref == null) {
            return;
        }
        Fragment f = mFragments.get(getItemId(position)).get();
        if (f != null && f instanceof DatafeedFragment) {
            ((DatafeedFragment) f).bind();
        }
    }

    @Override
    public void setFragmentVisibleAtPosition(int position, boolean visible) {
        WeakReference<Fragment> ref = mFragments.get(getItemId(position));
        if (ref == null) {
            return;
        }
        Fragment f = ref.get();
        if (f != null && f instanceof DatafeedFragment) {
            ((DatafeedFragment) f).setIsCurrentlyVisible(visible);
        }
    }

    @Override
    public boolean isFragmentAtPositionBound(int position) {
        WeakReference<Fragment> ref = mFragments.get(getItemId(position));
        if (ref == null) {
            return false;
        }
        Fragment f = mFragments.get(getItemId(position)).get();
        return f != null && f instanceof DatafeedFragment && ((DatafeedFragment) f).isBound();
    }
}
