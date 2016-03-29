package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesFavoriteCollection;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface Favorites {
  @POST("/_ah/api/tbaMobile/v9/favorites/list")
  Call<ModelsMobileApiMessagesFavoriteCollection> list(@Header("Authorization") String authToken);
  @POST("/_ah/api/tbaMobile/v9/favorites/list")
  Call<Observable<ModelsMobileApiMessagesFavoriteCollection>> listRx(@Header("Authorization") String authToken);
}
