package com.thebluealliance.androidclient.fragments.mytba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AddRemoveUserFavorite;
import com.thebluealliance.androidclient.accounts.AddUpdateUserSubscription;
import com.thebluealliance.androidclient.accounts.RemoveUserSubscription;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.mytba.CreateSubscriptionPanel;
import com.thebluealliance.androidclient.helpers.ModelHelper;
import com.thebluealliance.androidclient.listitems.NotificationTypeListElement;
import com.thebluealliance.androidclient.views.FloatingActionButton;

import java.util.ArrayList;

/**
 * File created by phil on 8/18/14.
 */
public class NotificationSettingsFragment extends Fragment {

    public static final String MODEL_KEY = "model_key";
    private String modelKey;
    private ModelHelper.MODELS modelType;
    private SlidingUpPanelLayout panel;
    private ListView list;

    public static NotificationSettingsFragment newInstance(String modelKey){
        NotificationSettingsFragment fragment = new NotificationSettingsFragment();
        Bundle args = new Bundle();
        args.putString(MODEL_KEY, modelKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() == null || !getArguments().containsKey(MODEL_KEY)){
            throw new IllegalArgumentException("NotificationSettingsFragment must be constructed with a model key");
        }
        modelKey = getArguments().getString(MODEL_KEY);
        modelType = ModelHelper.getModelFromKey(modelKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.notification_panel, container, false);
        panel = (SlidingUpPanelLayout) getActivity().findViewById(R.id.sliding_panel);
        panel.setPanelSlideListener(new PanelListener());
        final FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.favorite_button);
        list = (ListView)v.findViewById(R.id.notification_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddRemoveUserFavorite(getActivity(), fab).execute(modelKey);
            }
        });
        new CreateSubscriptionPanel(getActivity(), fab, list).execute(modelKey);
        return v;
    }

    class PanelListener implements SlidingUpPanelLayout.PanelSlideListener {

        @Override
        public void onPanelSlide(View view, float v) {

        }

        @Override
        public void onPanelCollapsed(View view) {
            //save subscription settings
            Log.d(Constants.LOG_TAG, "Panel Collapsed.");

            ArrayList<String> subscribed = new ArrayList<>();
            subscribed.add(modelKey);
            String[] modelNotifications = ModelHelper.getNotificationTypes(modelType);
            ListViewAdapter adapter = ((ListViewAdapter)list.getAdapter());
            for(int i=0; i<adapter.values.size(); i++){
                NotificationTypeListElement element = ((NotificationTypeListElement)adapter.values.get(i));
                Log.d(Constants.LOG_TAG, "Pos: "+i+": "+element.isEnabled());
                if(element.isEnabled()){
                    subscribed.add(modelNotifications[element.getPosition()]);
                }
            }
            Log.d(Constants.LOG_TAG, "notifications: "+ subscribed);

            if(subscribed.size() == 1){
                new RemoveUserSubscription(getActivity()).execute(modelKey);
            }else {
                new AddUpdateUserSubscription(getActivity()).execute(subscribed.toArray(new String[subscribed.size()]));
            }
        }

        @Override
        public void onPanelExpanded(View view) {

        }

        @Override
        public void onPanelAnchored(View view) {

        }

        @Override
        public void onPanelHidden(View view) {

        }
    }
}
