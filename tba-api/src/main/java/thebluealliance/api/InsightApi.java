package thebluealliance.api;

import thebluealliance.api.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import thebluealliance.api.model.GetStatus401Response;
import thebluealliance.api.model.LeaderboardInsight;
import thebluealliance.api.model.NotablesInsight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InsightApi {
  /**
   * 
   * Gets a list of &#x60;LeaderboardInsight&#x60; objects from a specific year. Use year&#x3D;0 for overall.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;LeaderboardInsight&gt;&gt;
   */
  @GET("insights/leaderboards/{year}")
  Call<List<LeaderboardInsight>> getInsightsLeaderboardsYear(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;NotablesInsight&#x60; objects from a specific year. Use year&#x3D;0 for overall.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;NotablesInsight&gt;&gt;
   */
  @GET("insights/notables/{year}")
  Call<List<NotablesInsight>> getInsightsNotablesYear(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

}
