package thebluealliance.api;

import thebluealliance.api.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import thebluealliance.api.model.InlineObject;
import thebluealliance.api.model.SearchIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DefaultApi {
  /**
   * 
   * Gets a large blob of data that is used on the frontend for searching. May change without notice.
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;SearchIndex&gt;
   */
  @GET("search_index")
  Call<SearchIndex> getSearchIndex(
    @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

}
