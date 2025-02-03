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

/**
 * LeaderboardInsightDataRankingsInner
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-02-03T11:29:24.649848-06:00[America/Chicago]", comments = "Generator version: 7.10.0")
public class LeaderboardInsightDataRankingsInner {
  public static final String SERIALIZED_NAME_VALUE = "value";
  @SerializedName(SERIALIZED_NAME_VALUE)
  @javax.annotation.Nonnull
  private BigDecimal value;

  public static final String SERIALIZED_NAME_KEYS = "keys";
  @SerializedName(SERIALIZED_NAME_KEYS)
  @javax.annotation.Nonnull
  private List<String> keys = new ArrayList<>();

  public LeaderboardInsightDataRankingsInner() {
  }

  public LeaderboardInsightDataRankingsInner value(@javax.annotation.Nonnull BigDecimal value) {
    
    this.value = value;
    return this;
  }

  /**
   * Value of the insight that the corresponding team/event/matches have, e.g. number of blue banners, or number of matches played.
   * @return value
   */
  @javax.annotation.Nonnull

  public BigDecimal getValue() {
    return value;
  }


  public void setValue(@javax.annotation.Nonnull BigDecimal value) {
    this.value = value;
  }

  public LeaderboardInsightDataRankingsInner keys(@javax.annotation.Nonnull List<String> keys) {
    
    this.keys = keys;
    return this;
  }

  public LeaderboardInsightDataRankingsInner addKeysItem(String keysItem) {
    if (this.keys == null) {
      this.keys = new ArrayList<>();
    }
    this.keys.add(keysItem);
    return this;
  }

  /**
   * Team/Event/Match keys that have the corresponding value.
   * @return keys
   */
  @javax.annotation.Nonnull

  public List<String> getKeys() {
    return keys;
  }


  public void setKeys(@javax.annotation.Nonnull List<String> keys) {
    this.keys = keys;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LeaderboardInsightDataRankingsInner leaderboardInsightDataRankingsInner = (LeaderboardInsightDataRankingsInner) o;
    return Objects.equals(this.value, leaderboardInsightDataRankingsInner.value) &&
        Objects.equals(this.keys, leaderboardInsightDataRankingsInner.keys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, keys);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LeaderboardInsightDataRankingsInner {\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
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

