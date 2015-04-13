package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.interfaces.RefreshableHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * A class that manages all datafeed requests for an activity's fragments
 * Child fragments will request endpoints, and they'll be notified when
 * the data is available
 * Created by phil on 4/7/15.
 */
public class DatafeedAdapter {
    RefreshableHost host;

    /* Map an endpoint to its query type */
    HashMap<String, APIHelper.QUERY> endpoints;

    /* Track fragment hashcode -> its subscriptions (api url) */
    HashMap<String, List<Subscriber<String>>> subscriptions;

    /* Reference to Retroactive API */
    APIv2 apiv2;

    public DatafeedAdapter(RefreshableHost host){
        this.host = host;
        endpoints = new HashMap<>();
        subscriptions = new HashMap<>();
        apiv2 = APIHelper.getAPI();
    }

    /**
     * Allow children to register a subscription to a datafeed endpoint
     * @param child The child updates will be sent to
     * @param endpoint The API URL we're subscribing to (e.g. /api/v2/team/frc1124)
     * @param type Type of this endpoint so it can be properly processed (e.g. QUERY.TEAM)
     */
    public synchronized void registerEndpoint(Subscriber<String> child, String endpoint, APIHelper.QUERY type){
        /* Store the endpoint to be fetched later */
        endpoints.put(endpoint, type);

        /* Store the relation for subscriptions */
        if(!subscriptions.containsKey(endpoint)){
            subscriptions.put(endpoint, new ArrayList<Subscriber<String>>());
        }
        if(!subscriptions.get(endpoint).contains(child)){
            subscriptions.get(endpoint).add(child);
        }
    }

    /**
     * Called by the host to fetch all the requested data
     */
    public void fetchData(){
        for(Map.Entry<String, APIHelper.QUERY> endpoint:endpoints.entrySet()){
            Observable<String> result = apiv2.endpoint(endpoint.getKey(), null); //TODO send If-Modified-Since header
            for(Subscriber<String> sub: subscriptions.get(endpoint.getKey())){
                result.subscribe(sub);
            }
        }
    }


}
