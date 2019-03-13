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

public interface Model {
  @POST("/clientapi/tbaClient/v9/model/setPreferences")
  Call<ModelsMobileApiMessagesBaseResponse> setPreferences(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesModelPreferenceMessage resource);
  @POST("/clientapi/tbaClient/v9/model/setPreferences")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> setPreferencesRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesModelPreferenceMessage resource);
}
