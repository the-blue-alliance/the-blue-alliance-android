package com.thebluealliance.androidclient.fragments.framework;

import android.app.Activity;
import android.content.ComponentName;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import android.widget.ListView;

import com.thebluealliance.androidclient.binders.AbstractDataBinder;
import com.thebluealliance.androidclient.binders.ListViewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.subscribers.BaseAPISubscriber;

import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowListView;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class FragmentTestDriver {

    private FragmentTestDriver() {
        // Unused
    }

    public static <F extends Fragment> DatafeedFragmentTestController<F> getController(F fragment) {
        return new DatafeedFragmentTestController<>(fragment);
    }

    public static <F extends Fragment> void testLifecycle(F fragment) {
        DatafeedFragmentTestController<F> controller = getController(fragment);
        controller.attach().pause().stop().destroy();
    }

    public static <F extends DatafeedFragment<T, V, S, B>, T, V, S extends BaseAPISubscriber<T, V>, B extends AbstractDataBinder<V>> Activity
    bindData(F fragment, V bindData) {
        DatafeedFragmentTestController<F> controller = getController(fragment);
        controller.makeTestActivityController().makeActivity();

        controller.attach();

        B binder = fragment.getBinder();
        assertNotNull(binder);

        // Bind given data(binder);
        binder.bindViews();
        binder.updateData(bindData);

        return controller.getActivity();
    }

    public static ListView getListView(ListViewFragment fragment) {
        ListViewBinder binder = (ListViewBinder) fragment.getBinder();
        assertNotNull(binder);
        return binder.getListView();
    }

    public static void testListViewClick(ListViewFragment fragment, List<ListItem> items, Class expectedActivity) {
        Activity activity = FragmentTestDriver.bindData(fragment, items);

        ListView listView = FragmentTestDriver.getListView(fragment);
        ShadowListView shadowList = Shadows.shadowOf(listView);
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);

        shadowList.performItemClick(0);
        assertNotNull(shadowActivity.peekNextStartedActivity());
        assertEquals(shadowActivity.peekNextStartedActivity().getComponent(),
                new ComponentName(activity, expectedActivity));

        activity.finish();
    }

    public static <F extends DatafeedFragment<T, V, S, B>, T, V, S extends BaseAPISubscriber<T, V>,
            B extends AbstractDataBinder<V>> void testNoDataBindings(F fragment, @IdRes int noDataViewRes) {
        /**
         * Still having OOM issues
        DatafeedFragmentTestController<F> controller = getController(fragment);
        controller.makeTestActivityController().makeActivity().attach();

        NoDataBinder noDataBinder = (NoDataBinder) Whitebox.getInternalState(fragment, "mNoDataBinder");
        B binder = (B) Whitebox.getInternalState(fragment, "mBinder");
        assertNotNull(noDataBinder);
        assertNotNull(binder);

        NoDataView noDataView = (NoDataView) fragment.getView().findViewById(noDataViewRes);
        assertNotNull(noDataView);
        verify(binder).setNoDataView(noDataView);

        NoDataViewParams params = fragment.getNoDataParams();
        assertNotNull(params);

        verify(binder).setNoDataParams(params);
        controller.getActivity().finish();
        controller.pause().stop().destroy();
        */
    }
}
