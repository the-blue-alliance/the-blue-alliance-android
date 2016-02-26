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

public interface TeamMedia {
  @POST("/team/media/suggest")
  ModelsMobileApiMessagesBaseResponse suggestion(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
  @POST("/team/media/suggest")
  void suggestion(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource, Callback<ModelsMobileApiMessagesBaseResponse> cb);
  @POST("/team/media/suggest")
  Observable<ModelsMobileApiMessagesBaseResponse> suggestionRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
}
