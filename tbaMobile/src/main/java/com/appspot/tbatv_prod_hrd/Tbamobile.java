package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.*;

import retrofit.Call;
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
  Call<ModelsMobileApiMessagesBaseResponse> register(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/register")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> registerRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/unregister")
  Call<ModelsMobileApiMessagesBaseResponse> unregister(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/unregister")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> unregisterRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
}
