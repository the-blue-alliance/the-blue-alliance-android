package com.appspot.tbatv_prod_hrd;

import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesBaseResponse;
import com.appspot.tbatv_prod_hrd.model.ModelsMobileApiMessagesMediaSuggestionMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface TeamMedia {
  @POST("/_ah/api/tbaMobile/v9/team/media/suggest")
  Call<ModelsMobileApiMessagesBaseResponse> suggestion(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
  @POST("/_ah/api/tbaMobile/v9/team/media/suggest")
  Call<Observable<ModelsMobileApiMessagesBaseResponse>> suggestionRx(@Header("Authorization") String authToken, @Body ModelsMobileApiMessagesMediaSuggestionMessage resource);
}
