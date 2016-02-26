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

public interface Tbamobile {
  @POST("/register")
  ModelsMobileApiMessagesBaseResponse register(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/register")
  void register(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource, Callback<ModelsMobileApiMessagesBaseResponse> cb);
  @POST("/register")
  Observable<ModelsMobileApiMessagesBaseResponse> registerRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/unregister")
  ModelsMobileApiMessagesBaseResponse unregister(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/unregister")
  void unregister(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource, Callback<ModelsMobileApiMessagesBaseResponse> cb);
  @POST("/unregister")
  Observable<ModelsMobileApiMessagesBaseResponse> unregisterRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
}
