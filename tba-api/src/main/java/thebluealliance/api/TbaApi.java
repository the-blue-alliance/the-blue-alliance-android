package thebluealliance.api;

import thebluealliance.api.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import thebluealliance.api.model.APIStatus;
import thebluealliance.api.model.GetStatus401Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TbaApi {
  /**
   * 
   * Returns API status, and TBA status information.
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;APIStatus&gt;
   */
  @GET("status")
  Call<APIStatus> getStatus(
    @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

}
