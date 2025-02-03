/*
 * The Blue Alliance API v3
 * # Overview    Information and statistics about FIRST Robotics Competition teams and events.   # Authentication   All endpoints require an Auth Key to be passed in the header `X-TBA-Auth-Key`. If you do not have an auth key yet, you can obtain one from your [Account Page](/account).
 *
 * The version of the OpenAPI document: 3.9.9
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package thebluealliance.api.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import thebluealliance.api.model.WLTRecord;

/**
 * EventRankingRankingsInner
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-02-03T11:29:24.649848-06:00[America/Chicago]", comments = "Generator version: 7.10.0")
public class EventRankingRankingsInner {
  public static final String SERIALIZED_NAME_MATCHES_PLAYED = "matches_played";
  @SerializedName(SERIALIZED_NAME_MATCHES_PLAYED)
  @javax.annotation.Nonnull
  private Integer matchesPlayed;

  public static final String SERIALIZED_NAME_QUAL_AVERAGE = "qual_average";
  @SerializedName(SERIALIZED_NAME_QUAL_AVERAGE)
  @javax.annotation.Nullable
  private Integer qualAverage;

  public static final String SERIALIZED_NAME_EXTRA_STATS = "extra_stats";
  @SerializedName(SERIALIZED_NAME_EXTRA_STATS)
  @javax.annotation.Nonnull
  private List<BigDecimal> extraStats = new ArrayList<>();

  public static final String SERIALIZED_NAME_SORT_ORDERS = "sort_orders";
  @SerializedName(SERIALIZED_NAME_SORT_ORDERS)
  @javax.annotation.Nullable
  private List<Double> sortOrders;

  public static final String SERIALIZED_NAME_RECORD = "record";
  @SerializedName(SERIALIZED_NAME_RECORD)
  @javax.annotation.Nullable
  private WLTRecord record;

  public static final String SERIALIZED_NAME_RANK = "rank";
  @SerializedName(SERIALIZED_NAME_RANK)
  @javax.annotation.Nonnull
  private Integer rank;

  public static final String SERIALIZED_NAME_DQ = "dq";
  @SerializedName(SERIALIZED_NAME_DQ)
  @javax.annotation.Nonnull
  private Integer dq;

  public static final String SERIALIZED_NAME_TEAM_KEY = "team_key";
  @SerializedName(SERIALIZED_NAME_TEAM_KEY)
  @javax.annotation.Nonnull
  private String teamKey;

  public EventRankingRankingsInner() {
  }

  public EventRankingRankingsInner matchesPlayed(@javax.annotation.Nonnull Integer matchesPlayed) {
    
    this.matchesPlayed = matchesPlayed;
    return this;
  }

  /**
   * Number of matches played by this team.
   * @return matchesPlayed
   */
  @javax.annotation.Nonnull

  public Integer getMatchesPlayed() {
    return matchesPlayed;
  }


  public void setMatchesPlayed(@javax.annotation.Nonnull Integer matchesPlayed) {
    this.matchesPlayed = matchesPlayed;
  }

  public EventRankingRankingsInner qualAverage(@javax.annotation.Nullable Integer qualAverage) {
    
    this.qualAverage = qualAverage;
    return this;
  }

  /**
   * The average match score during qualifications. Year specific. May be null if not relevant for a given year.
   * @return qualAverage
   */
  @javax.annotation.Nullable

  public Integer getQualAverage() {
    return qualAverage;
  }


  public void setQualAverage(@javax.annotation.Nullable Integer qualAverage) {
    this.qualAverage = qualAverage;
  }

  public EventRankingRankingsInner extraStats(@javax.annotation.Nonnull List<BigDecimal> extraStats) {
    
    this.extraStats = extraStats;
    return this;
  }

  public EventRankingRankingsInner addExtraStatsItem(BigDecimal extraStatsItem) {
    if (this.extraStats == null) {
      this.extraStats = new ArrayList<>();
    }
    this.extraStats.add(extraStatsItem);
    return this;
  }

  /**
   * Additional special data on the team&#39;s performance calculated by TBA.
   * @return extraStats
   */
  @javax.annotation.Nonnull

  public List<BigDecimal> getExtraStats() {
    return extraStats;
  }


  public void setExtraStats(@javax.annotation.Nonnull List<BigDecimal> extraStats) {
    this.extraStats = extraStats;
  }

  public EventRankingRankingsInner sortOrders(@javax.annotation.Nullable List<Double> sortOrders) {
    
    this.sortOrders = sortOrders;
    return this;
  }

  public EventRankingRankingsInner addSortOrdersItem(Double sortOrdersItem) {
    if (this.sortOrders == null) {
      this.sortOrders = new ArrayList<>();
    }
    this.sortOrders.add(sortOrdersItem);
    return this;
  }

  /**
   * Additional year-specific information, may be null. See parent &#x60;sort_order_info&#x60; for details.
   * @return sortOrders
   */
  @javax.annotation.Nullable

  public List<Double> getSortOrders() {
    return sortOrders;
  }


  public void setSortOrders(@javax.annotation.Nullable List<Double> sortOrders) {
    this.sortOrders = sortOrders;
  }

  public EventRankingRankingsInner record(@javax.annotation.Nullable WLTRecord record) {
    
    this.record = record;
    return this;
  }

  /**
   * Get record
   * @return record
   */
  @javax.annotation.Nullable

  public WLTRecord getRecord() {
    return record;
  }


  public void setRecord(@javax.annotation.Nullable WLTRecord record) {
    this.record = record;
  }

  public EventRankingRankingsInner rank(@javax.annotation.Nonnull Integer rank) {
    
    this.rank = rank;
    return this;
  }

  /**
   * The team&#39;s rank at the event as provided by FIRST.
   * @return rank
   */
  @javax.annotation.Nonnull

  public Integer getRank() {
    return rank;
  }


  public void setRank(@javax.annotation.Nonnull Integer rank) {
    this.rank = rank;
  }

  public EventRankingRankingsInner dq(@javax.annotation.Nonnull Integer dq) {
    
    this.dq = dq;
    return this;
  }

  /**
   * Number of times disqualified.
   * @return dq
   */
  @javax.annotation.Nonnull

  public Integer getDq() {
    return dq;
  }


  public void setDq(@javax.annotation.Nonnull Integer dq) {
    this.dq = dq;
  }

  public EventRankingRankingsInner teamKey(@javax.annotation.Nonnull String teamKey) {
    
    this.teamKey = teamKey;
    return this;
  }

  /**
   * The team with this rank.
   * @return teamKey
   */
  @javax.annotation.Nonnull

  public String getTeamKey() {
    return teamKey;
  }


  public void setTeamKey(@javax.annotation.Nonnull String teamKey) {
    this.teamKey = teamKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventRankingRankingsInner eventRankingRankingsInner = (EventRankingRankingsInner) o;
    return Objects.equals(this.matchesPlayed, eventRankingRankingsInner.matchesPlayed) &&
        Objects.equals(this.qualAverage, eventRankingRankingsInner.qualAverage) &&
        Objects.equals(this.extraStats, eventRankingRankingsInner.extraStats) &&
        Objects.equals(this.sortOrders, eventRankingRankingsInner.sortOrders) &&
        Objects.equals(this.record, eventRankingRankingsInner.record) &&
        Objects.equals(this.rank, eventRankingRankingsInner.rank) &&
        Objects.equals(this.dq, eventRankingRankingsInner.dq) &&
        Objects.equals(this.teamKey, eventRankingRankingsInner.teamKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matchesPlayed, qualAverage, extraStats, sortOrders, record, rank, dq, teamKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventRankingRankingsInner {\n");
    sb.append("    matchesPlayed: ").append(toIndentedString(matchesPlayed)).append("\n");
    sb.append("    qualAverage: ").append(toIndentedString(qualAverage)).append("\n");
    sb.append("    extraStats: ").append(toIndentedString(extraStats)).append("\n");
    sb.append("    sortOrders: ").append(toIndentedString(sortOrders)).append("\n");
    sb.append("    record: ").append(toIndentedString(record)).append("\n");
    sb.append("    rank: ").append(toIndentedString(rank)).append("\n");
    sb.append("    dq: ").append(toIndentedString(dq)).append("\n");
    sb.append("    teamKey: ").append(toIndentedString(teamKey)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

