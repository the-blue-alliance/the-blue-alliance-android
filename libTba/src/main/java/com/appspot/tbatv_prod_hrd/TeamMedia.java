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

public interface TeamMedia {
  @POST("/clientapi/tbaClient/v9/team/media/suggest")
  Call<ModelsMobileApiMessagesBaseResponse> suggestion(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
  @POST("/clientapi/tbaClient/v9/team/media/suggest")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> suggestionRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
}
