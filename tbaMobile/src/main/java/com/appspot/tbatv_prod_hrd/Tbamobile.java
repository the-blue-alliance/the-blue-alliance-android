package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesRegistrationRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
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
