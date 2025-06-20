/*
 * The Blue Alliance API v3
 * # Overview    Information and statistics about FIRST Robotics Competition teams and events.   # Authentication   All endpoints require an Auth Key to be passed in the header `X-TBA-Auth-Key`. If you do not have an auth key yet, you can obtain one from your [Account Page](/account).
 *
 * The version of the OpenAPI document: 3.10.0
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MatchAlliance
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class MatchAlliance {
  public static final String SERIALIZED_NAME_SCORE = "score";
  @SerializedName(SERIALIZED_NAME_SCORE)
  @javax.annotation.Nonnull
  private Integer score;

  public static final String SERIALIZED_NAME_TEAM_KEYS = "team_keys";
  @SerializedName(SERIALIZED_NAME_TEAM_KEYS)
  @javax.annotation.Nonnull
  private List<String> teamKeys = new ArrayList<>();

  public static final String SERIALIZED_NAME_SURROGATE_TEAM_KEYS = "surrogate_team_keys";
  @SerializedName(SERIALIZED_NAME_SURROGATE_TEAM_KEYS)
  @javax.annotation.Nonnull
  private List<String> surrogateTeamKeys = new ArrayList<>();

  public static final String SERIALIZED_NAME_DQ_TEAM_KEYS = "dq_team_keys";
  @SerializedName(SERIALIZED_NAME_DQ_TEAM_KEYS)
  @javax.annotation.Nonnull
  private List<String> dqTeamKeys = new ArrayList<>();

  public MatchAlliance() {
  }

  public MatchAlliance score(@javax.annotation.Nonnull Integer score) {
    
    this.score = score;
    return this;
  }

  /**
   * Score for this alliance. Will be null or -1 for an unplayed match.
   * @return score
   */
  @javax.annotation.Nonnull

  public Integer getScore() {
    return score;
  }


  public void setScore(@javax.annotation.Nonnull Integer score) {
    this.score = score;
  }

  public MatchAlliance teamKeys(@javax.annotation.Nonnull List<String> teamKeys) {
    
    this.teamKeys = teamKeys;
    return this;
  }

  public MatchAlliance addTeamKeysItem(String teamKeysItem) {
    if (this.teamKeys == null) {
      this.teamKeys = new ArrayList<>();
    }
    this.teamKeys.add(teamKeysItem);
    return this;
  }

  /**
   * Get teamKeys
   * @return teamKeys
   */
  @javax.annotation.Nonnull

  public List<String> getTeamKeys() {
    return teamKeys;
  }


  public void setTeamKeys(@javax.annotation.Nonnull List<String> teamKeys) {
    this.teamKeys = teamKeys;
  }

  public MatchAlliance surrogateTeamKeys(@javax.annotation.Nonnull List<String> surrogateTeamKeys) {
    
    this.surrogateTeamKeys = surrogateTeamKeys;
    return this;
  }

  public MatchAlliance addSurrogateTeamKeysItem(String surrogateTeamKeysItem) {
    if (this.surrogateTeamKeys == null) {
      this.surrogateTeamKeys = new ArrayList<>();
    }
    this.surrogateTeamKeys.add(surrogateTeamKeysItem);
    return this;
  }

  /**
   * TBA team keys (eg &#x60;frc254&#x60;) of any teams playing as a surrogate.
   * @return surrogateTeamKeys
   */
  @javax.annotation.Nonnull

  public List<String> getSurrogateTeamKeys() {
    return surrogateTeamKeys;
  }


  public void setSurrogateTeamKeys(@javax.annotation.Nonnull List<String> surrogateTeamKeys) {
    this.surrogateTeamKeys = surrogateTeamKeys;
  }

  public MatchAlliance dqTeamKeys(@javax.annotation.Nonnull List<String> dqTeamKeys) {
    
    this.dqTeamKeys = dqTeamKeys;
    return this;
  }

  public MatchAlliance addDqTeamKeysItem(String dqTeamKeysItem) {
    if (this.dqTeamKeys == null) {
      this.dqTeamKeys = new ArrayList<>();
    }
    this.dqTeamKeys.add(dqTeamKeysItem);
    return this;
  }

  /**
   * TBA team keys (eg &#x60;frc254&#x60;) of any disqualified teams.
   * @return dqTeamKeys
   */
  @javax.annotation.Nonnull

  public List<String> getDqTeamKeys() {
    return dqTeamKeys;
  }


  public void setDqTeamKeys(@javax.annotation.Nonnull List<String> dqTeamKeys) {
    this.dqTeamKeys = dqTeamKeys;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatchAlliance matchAlliance = (MatchAlliance) o;
    return Objects.equals(this.score, matchAlliance.score) &&
        Objects.equals(this.teamKeys, matchAlliance.teamKeys) &&
        Objects.equals(this.surrogateTeamKeys, matchAlliance.surrogateTeamKeys) &&
        Objects.equals(this.dqTeamKeys, matchAlliance.dqTeamKeys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(score, teamKeys, surrogateTeamKeys, dqTeamKeys);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MatchAlliance {\n");
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
    sb.append("    teamKeys: ").append(toIndentedString(teamKeys)).append("\n");
    sb.append("    surrogateTeamKeys: ").append(toIndentedString(surrogateTeamKeys)).append("\n");
    sb.append("    dqTeamKeys: ").append(toIndentedString(dqTeamKeys)).append("\n");
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

