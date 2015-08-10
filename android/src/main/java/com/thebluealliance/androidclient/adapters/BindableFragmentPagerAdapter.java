package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.fragments.DatafeedFragment;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * A subclass of {@link FragmentPagerAdapter} that stores each fragment in a {@link Hashtable} so
 * that they can be accessed and bound at any given time.
 *
 * Created by Nathan on 8/10/2015.
 */
public abstract class BindableFragmentPagerAdapter extends FragmentPagerAdapter implements com.thebluealliance.androidclient.interfaces.BindableFragmentPagerAdapter {

    private Hashtable<Integer, WeakReference<Fragment>> mFragments = new Hashtable<>();

    public BindableFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        mFragments.put(position, new WeakReference<>(createdFragment));
        return createdFragment;
    }

    @Override
    public void bindFragmentAtPosition(int position) {
        Fragment f = mFragments.get(position).get();
        if (f != null && f instanceof DatafeedFragment) {
            ((DatafeedFragment) f).bind();
        }
    }
}
