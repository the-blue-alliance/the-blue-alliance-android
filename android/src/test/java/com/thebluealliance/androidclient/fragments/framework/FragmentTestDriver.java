package com.thebluealliance.androidclient.fragments.framework;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;

public class FragmentTestDriver {

    public static <F extends DatafeedFragment> DatafeedFragmentTestController<F> getController(F fragment) {
        return new DatafeedFragmentTestController<>(fragment);
    }

    public static <F extends DatafeedFragment> void testAttach(F fragment) {
        DatafeedFragmentTestController<F> controller = getController(fragment);
        controller.attach();
    }

    public static <F extends DatafeedFragment> void testLifecycle(F fragment) {
        DatafeedFragmentTestController<F> controller = getController(fragment);
        controller.attach().pause().stop();
    }

    public static <F extends DatafeedFragment<T, V, S, B>, T, V, S extends BaseAPISubscriber<T, V>,
            B extends AbstractDataBinder<V>> void testDatafeedBindings(
            F fragment, T apiData) {
        DatafeedFragmentTestController<F> controller = getController(fragment);
    }
}
