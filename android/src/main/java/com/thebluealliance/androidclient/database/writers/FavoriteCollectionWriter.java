package com.thebluealliance.androidclient.database.writers;

import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesFavoriteCollection;
import com.thebluealliance.androidclient.database.Database;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteCollectionWriter implements Callback<ModelsMobileApiMessagesFavoriteCollection> {

    public FavoriteCollectionWriter(Database db) {

    }

    @Override
    public void onResponse(
            Call<ModelsMobileApiMessagesFavoriteCollection> call,
            Response<ModelsMobileApiMessagesFavoriteCollection> response) {

    }

    @Override
    public void onFailure(Call<ModelsMobileApiMessagesFavoriteCollection> call, Throwable t) {

    }
}
