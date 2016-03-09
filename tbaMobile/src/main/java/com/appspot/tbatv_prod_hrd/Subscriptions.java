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

public interface Subscriptions {
  @POST("/_ah/api/tbaMobile/v9/subscriptions/list")
  Call<ModelsMobileApiMessagesSubscriptionCollection> list(@Header("Authorization") String authToken);
  @POST("/_ah/api/tbaMobile/v9/subscriptions/list")
  Call<Observable<ModelsMobileApiMessagesSubscriptionCollection>> listRx(@Header("Authorization") String authToken);
}
