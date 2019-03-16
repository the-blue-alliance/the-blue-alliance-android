package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface Tbamobile {
  @POST("/clientapi/tbaClient/v9/register")
  Call<ModelsMobileApiMessagesBaseResponse> register(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/clientapi/tbaClient/v9/register")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> registerRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/clientapi/tbaClient/v9/unregister")
  Call<ModelsMobileApiMessagesBaseResponse> unregister(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/clientapi/tbaClient/v9/unregister")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> unregisterRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
}
