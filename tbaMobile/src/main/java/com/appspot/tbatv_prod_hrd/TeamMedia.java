package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesMediaSuggestionMessage;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

public interface TeamMedia {
  @POST("/_ah/api/tbaMobile/v9/team/media/suggest")
  Call<ModelsMobileApiMessagesBaseResponse> suggestion(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
  @POST("/_ah/api/tbaMobile/v9/team/media/suggest")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> suggestionRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
}
