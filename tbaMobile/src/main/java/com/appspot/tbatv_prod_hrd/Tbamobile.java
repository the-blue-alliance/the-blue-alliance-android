package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesRegistrationRequest;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

public interface Tbamobile {
  @POST("/_ah/api/tbaMobile/v9/register")
  Call<ModelsMobileApiMessagesBaseResponse> register(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/_ah/api/tbaMobile/v9/register")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> registerRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/_ah/api/tbaMobile/v9/unregister")
  Call<ModelsMobileApiMessagesBaseResponse> unregister(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
  @POST("/_ah/api/tbaMobile/v9/unregister")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> unregisterRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesRegistrationRequest resource);
}
