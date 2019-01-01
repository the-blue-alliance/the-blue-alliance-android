package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.retrofit.GitHubAPI;
import com.thebluealliance.androidclient.listeners.ContributorClickListener;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.ContributorListSubscriber;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

public class ContributorsFragment extends ListViewFragment<JsonElement, ContributorListSubscriber> {

    @Inject @Named("github_api") GitHubAPI mAPI;
    @Inject ContributorClickListener mContributorClickListener;

    public static ContributorsFragment newInstance() {
        return new ContributorsFragment();
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener(mContributorClickListener);
        return view;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends JsonElement> getObservable(String tbaCacheHeader) {
        return mAPI.fetchRepoContributors("the-blue-alliance", "the-blue-alliance-android");
    }

    @Override
    protected String getRefreshTag() {
        return "app-contributors";
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_info_black_48dp, R.string.no_contributors_list);
    }
}
