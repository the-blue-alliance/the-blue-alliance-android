package com.thebluealliance.androidclient.fragments.mytba;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.MyTbaDatafeed;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.FavoriteListSubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class MyFavoritesFragment extends ListViewFragment<List<Favorite>, FavoriteListSubscriber> {

    @Inject MyTbaDatafeed mMyTbaDatafeed;

    public static MyFavoritesFragment newInstance() {
        return new MyFavoritesFragment();
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Favorite>> getObservable(String tbaCacheHeader) {
        return mMyTbaDatafeed.fetchLocalFavorites();
    }

    @Override
    protected String getRefreshTag() {
        return "myFavorites";
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_star_black_48dp, R.string.no_favorites_data);
    }
}
