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

public interface Model {
  @POST("/model/setPreferences")
  ModelsMobileApiMessagesBaseResponse setPreferences(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesModelPreferenceMessage resource);
  @POST("/model/setPreferences")
  void setPreferences(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesModelPreferenceMessage resource, Callback<ModelsMobileApiMessagesBaseResponse> cb);
  @POST("/model/setPreferences")
  Observable<ModelsMobileApiMessagesBaseResponse> setPreferencesRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesModelPreferenceMessage resource);
}
