package thebluealliance.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import thebluealliance.api.model.RegionalRanking;

public interface RegionalAdvancementApi {
  /**
   * 
   * Gets the team rankings in the regional pool for a specific year.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;RegionalRanking&gt;&gt;
   */
  @GET("regional_advancement/{year}/rankings")
  Call<List<RegionalRanking>> getRegionalRankings(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

}
