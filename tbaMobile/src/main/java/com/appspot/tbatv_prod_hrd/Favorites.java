package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.*;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface Favorites {
  @POST("/favorites/list")
  ModelsMobileApiMessagesFavoriteCollection list(@Header("Authorization") String authToken);
  @POST("/favorites/list")
  void list(@Header("Authorization") String authToken, Callback<ModelsMobileApiMessagesFavoriteCollection> cb);
  @POST("/favorites/list")
  Observable<ModelsMobileApiMessagesFavoriteCollection> listRx(@Header("Authorization") String authToken);
}
