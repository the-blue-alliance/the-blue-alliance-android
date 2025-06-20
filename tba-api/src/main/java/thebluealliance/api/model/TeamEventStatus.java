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

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * TeamEventStatus
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class TeamEventStatus {
  public static final String SERIALIZED_NAME_QUAL = "qual";
  @SerializedName(SERIALIZED_NAME_QUAL)
  @javax.annotation.Nullable
  private TeamEventStatusRank qual;

  public static final String SERIALIZED_NAME_ALLIANCE = "alliance";
  @SerializedName(SERIALIZED_NAME_ALLIANCE)
  @javax.annotation.Nullable
  private TeamEventStatusAlliance alliance;

  public static final String SERIALIZED_NAME_PLAYOFF = "playoff";
  @SerializedName(SERIALIZED_NAME_PLAYOFF)
  @javax.annotation.Nullable
  private TeamEventStatusPlayoff playoff = null;

  public static final String SERIALIZED_NAME_ALLIANCE_STATUS_STR = "alliance_status_str";
  @SerializedName(SERIALIZED_NAME_ALLIANCE_STATUS_STR)
  @javax.annotation.Nullable
  private String allianceStatusStr;

  public static final String SERIALIZED_NAME_PLAYOFF_STATUS_STR = "playoff_status_str";
  @SerializedName(SERIALIZED_NAME_PLAYOFF_STATUS_STR)
  @javax.annotation.Nullable
  private String playoffStatusStr;

  public static final String SERIALIZED_NAME_OVERALL_STATUS_STR = "overall_status_str";
  @SerializedName(SERIALIZED_NAME_OVERALL_STATUS_STR)
  @javax.annotation.Nullable
  private String overallStatusStr;

  public static final String SERIALIZED_NAME_NEXT_MATCH_KEY = "next_match_key";
  @SerializedName(SERIALIZED_NAME_NEXT_MATCH_KEY)
  @javax.annotation.Nullable
  private String nextMatchKey;

  public static final String SERIALIZED_NAME_LAST_MATCH_KEY = "last_match_key";
  @SerializedName(SERIALIZED_NAME_LAST_MATCH_KEY)
  @javax.annotation.Nullable
  private String lastMatchKey;

  public TeamEventStatus() {
  }

  public TeamEventStatus qual(@javax.annotation.Nullable TeamEventStatusRank qual) {
    
    this.qual = qual;
    return this;
  }

  /**
   * Get qual
   * @return qual
   */
  @javax.annotation.Nullable

  public TeamEventStatusRank getQual() {
    return qual;
  }


  public void setQual(@javax.annotation.Nullable TeamEventStatusRank qual) {
    this.qual = qual;
  }

  public TeamEventStatus alliance(@javax.annotation.Nullable TeamEventStatusAlliance alliance) {
    
    this.alliance = alliance;
    return this;
  }

  /**
   * Get alliance
   * @return alliance
   */
  @javax.annotation.Nullable

  public TeamEventStatusAlliance getAlliance() {
    return alliance;
  }


  public void setAlliance(@javax.annotation.Nullable TeamEventStatusAlliance alliance) {
    this.alliance = alliance;
  }

  public TeamEventStatus playoff(@javax.annotation.Nullable TeamEventStatusPlayoff playoff) {
    
    this.playoff = playoff;
    return this;
  }

  /**
   * Playoff status for this team, may be null if the team did not make playoffs, or playoffs have not begun.
   * @return playoff
   */
  @javax.annotation.Nullable

  public TeamEventStatusPlayoff getPlayoff() {
    return playoff;
  }


  public void setPlayoff(@javax.annotation.Nullable TeamEventStatusPlayoff playoff) {
    this.playoff = playoff;
  }

  public TeamEventStatus allianceStatusStr(@javax.annotation.Nullable String allianceStatusStr) {
    
    this.allianceStatusStr = allianceStatusStr;
    return this;
  }

  /**
   * An HTML formatted string suitable for display to the user containing the team&#39;s alliance pick status.
   * @return allianceStatusStr
   */
  @javax.annotation.Nullable

  public String getAllianceStatusStr() {
    return allianceStatusStr;
  }


  public void setAllianceStatusStr(@javax.annotation.Nullable String allianceStatusStr) {
    this.allianceStatusStr = allianceStatusStr;
  }

  public TeamEventStatus playoffStatusStr(@javax.annotation.Nullable String playoffStatusStr) {
    
    this.playoffStatusStr = playoffStatusStr;
    return this;
  }

  /**
   * An HTML formatter string suitable for display to the user containing the team&#39;s playoff status.
   * @return playoffStatusStr
   */
  @javax.annotation.Nullable

  public String getPlayoffStatusStr() {
    return playoffStatusStr;
  }


  public void setPlayoffStatusStr(@javax.annotation.Nullable String playoffStatusStr) {
    this.playoffStatusStr = playoffStatusStr;
  }

  public TeamEventStatus overallStatusStr(@javax.annotation.Nullable String overallStatusStr) {
    
    this.overallStatusStr = overallStatusStr;
    return this;
  }

  /**
   * An HTML formatted string suitable for display to the user containing the team&#39;s overall status summary of the event.
   * @return overallStatusStr
   */
  @javax.annotation.Nullable

  public String getOverallStatusStr() {
    return overallStatusStr;
  }


  public void setOverallStatusStr(@javax.annotation.Nullable String overallStatusStr) {
    this.overallStatusStr = overallStatusStr;
  }

  public TeamEventStatus nextMatchKey(@javax.annotation.Nullable String nextMatchKey) {
    
    this.nextMatchKey = nextMatchKey;
    return this;
  }

  /**
   * TBA match key for the next match the team is scheduled to play in at this event, or null.
   * @return nextMatchKey
   */
  @javax.annotation.Nullable

  public String getNextMatchKey() {
    return nextMatchKey;
  }


  public void setNextMatchKey(@javax.annotation.Nullable String nextMatchKey) {
    this.nextMatchKey = nextMatchKey;
  }

  public TeamEventStatus lastMatchKey(@javax.annotation.Nullable String lastMatchKey) {
    
    this.lastMatchKey = lastMatchKey;
    return this;
  }

  /**
   * TBA match key for the last match the team played in at this event, or null.
   * @return lastMatchKey
   */
  @javax.annotation.Nullable

  public String getLastMatchKey() {
    return lastMatchKey;
  }


  public void setLastMatchKey(@javax.annotation.Nullable String lastMatchKey) {
    this.lastMatchKey = lastMatchKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TeamEventStatus teamEventStatus = (TeamEventStatus) o;
    return Objects.equals(this.qual, teamEventStatus.qual) &&
        Objects.equals(this.alliance, teamEventStatus.alliance) &&
        Objects.equals(this.playoff, teamEventStatus.playoff) &&
        Objects.equals(this.allianceStatusStr, teamEventStatus.allianceStatusStr) &&
        Objects.equals(this.playoffStatusStr, teamEventStatus.playoffStatusStr) &&
        Objects.equals(this.overallStatusStr, teamEventStatus.overallStatusStr) &&
        Objects.equals(this.nextMatchKey, teamEventStatus.nextMatchKey) &&
        Objects.equals(this.lastMatchKey, teamEventStatus.lastMatchKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qual, alliance, playoff, allianceStatusStr, playoffStatusStr, overallStatusStr, nextMatchKey, lastMatchKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TeamEventStatus {\n");
    sb.append("    qual: ").append(toIndentedString(qual)).append("\n");
    sb.append("    alliance: ").append(toIndentedString(alliance)).append("\n");
    sb.append("    playoff: ").append(toIndentedString(playoff)).append("\n");
    sb.append("    allianceStatusStr: ").append(toIndentedString(allianceStatusStr)).append("\n");
    sb.append("    playoffStatusStr: ").append(toIndentedString(playoffStatusStr)).append("\n");
    sb.append("    overallStatusStr: ").append(toIndentedString(overallStatusStr)).append("\n");
    sb.append("    nextMatchKey: ").append(toIndentedString(nextMatchKey)).append("\n");
    sb.append("    lastMatchKey: ").append(toIndentedString(lastMatchKey)).append("\n");
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

