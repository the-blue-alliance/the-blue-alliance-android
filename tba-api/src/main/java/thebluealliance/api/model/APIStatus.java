/*
 * The Blue Alliance API v3
 * # Overview    Information and statistics about FIRST Robotics Competition teams and events.   # Authentication   All endpoints require an Auth Key to be passed in the header `X-TBA-Auth-Key`. If you do not have an auth key yet, you can obtain one from your [Account Page](/account).
 *
 * The version of the OpenAPI document: 3.9.7
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
import thebluealliance.api.model.APIStatusAppVersion;

/**
 * APIStatus
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-01-12T10:04:04.013721-06:00[America/Chicago]", comments = "Generator version: 7.10.0")
public class APIStatus {
  public static final String SERIALIZED_NAME_CURRENT_SEASON = "current_season";
  @SerializedName(SERIALIZED_NAME_CURRENT_SEASON)
  @javax.annotation.Nonnull
  private Integer currentSeason;

  public static final String SERIALIZED_NAME_MAX_SEASON = "max_season";
  @SerializedName(SERIALIZED_NAME_MAX_SEASON)
  @javax.annotation.Nonnull
  private Integer maxSeason;

  public static final String SERIALIZED_NAME_IS_DATAFEED_DOWN = "is_datafeed_down";
  @SerializedName(SERIALIZED_NAME_IS_DATAFEED_DOWN)
  @javax.annotation.Nonnull
  private Boolean isDatafeedDown;

  public static final String SERIALIZED_NAME_DOWN_EVENTS = "down_events";
  @SerializedName(SERIALIZED_NAME_DOWN_EVENTS)
  @javax.annotation.Nonnull
  private List<String> downEvents = new ArrayList<>();

  public static final String SERIALIZED_NAME_IOS = "ios";
  @SerializedName(SERIALIZED_NAME_IOS)
  @javax.annotation.Nonnull
  private APIStatusAppVersion ios;

  public static final String SERIALIZED_NAME_ANDROID = "android";
  @SerializedName(SERIALIZED_NAME_ANDROID)
  @javax.annotation.Nonnull
  private APIStatusAppVersion android;

  public APIStatus() {
  }

  public APIStatus currentSeason(@javax.annotation.Nonnull Integer currentSeason) {
    
    this.currentSeason = currentSeason;
    return this;
  }

  /**
   * Year of the current FRC season.
   * @return currentSeason
   */
  @javax.annotation.Nonnull

  public Integer getCurrentSeason() {
    return currentSeason;
  }


  public void setCurrentSeason(@javax.annotation.Nonnull Integer currentSeason) {
    this.currentSeason = currentSeason;
  }

  public APIStatus maxSeason(@javax.annotation.Nonnull Integer maxSeason) {
    
    this.maxSeason = maxSeason;
    return this;
  }

  /**
   * Maximum FRC season year for valid queries.
   * @return maxSeason
   */
  @javax.annotation.Nonnull

  public Integer getMaxSeason() {
    return maxSeason;
  }


  public void setMaxSeason(@javax.annotation.Nonnull Integer maxSeason) {
    this.maxSeason = maxSeason;
  }

  public APIStatus isDatafeedDown(@javax.annotation.Nonnull Boolean isDatafeedDown) {
    
    this.isDatafeedDown = isDatafeedDown;
    return this;
  }

  /**
   * True if the entire FMS API provided by FIRST is down.
   * @return isDatafeedDown
   */
  @javax.annotation.Nonnull

  public Boolean getIsDatafeedDown() {
    return isDatafeedDown;
  }


  public void setIsDatafeedDown(@javax.annotation.Nonnull Boolean isDatafeedDown) {
    this.isDatafeedDown = isDatafeedDown;
  }

  public APIStatus downEvents(@javax.annotation.Nonnull List<String> downEvents) {
    
    this.downEvents = downEvents;
    return this;
  }

  public APIStatus addDownEventsItem(String downEventsItem) {
    if (this.downEvents == null) {
      this.downEvents = new ArrayList<>();
    }
    this.downEvents.add(downEventsItem);
    return this;
  }

  /**
   * An array of strings containing event keys of any active events that are no longer updating.
   * @return downEvents
   */
  @javax.annotation.Nonnull

  public List<String> getDownEvents() {
    return downEvents;
  }


  public void setDownEvents(@javax.annotation.Nonnull List<String> downEvents) {
    this.downEvents = downEvents;
  }

  public APIStatus ios(@javax.annotation.Nonnull APIStatusAppVersion ios) {
    
    this.ios = ios;
    return this;
  }

  /**
   * Get ios
   * @return ios
   */
  @javax.annotation.Nonnull

  public APIStatusAppVersion getIos() {
    return ios;
  }


  public void setIos(@javax.annotation.Nonnull APIStatusAppVersion ios) {
    this.ios = ios;
  }

  public APIStatus android(@javax.annotation.Nonnull APIStatusAppVersion android) {
    
    this.android = android;
    return this;
  }

  /**
   * Get android
   * @return android
   */
  @javax.annotation.Nonnull

  public APIStatusAppVersion getAndroid() {
    return android;
  }


  public void setAndroid(@javax.annotation.Nonnull APIStatusAppVersion android) {
    this.android = android;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    APIStatus apIStatus = (APIStatus) o;
    return Objects.equals(this.currentSeason, apIStatus.currentSeason) &&
        Objects.equals(this.maxSeason, apIStatus.maxSeason) &&
        Objects.equals(this.isDatafeedDown, apIStatus.isDatafeedDown) &&
        Objects.equals(this.downEvents, apIStatus.downEvents) &&
        Objects.equals(this.ios, apIStatus.ios) &&
        Objects.equals(this.android, apIStatus.android);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currentSeason, maxSeason, isDatafeedDown, downEvents, ios, android);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIStatus {\n");
    sb.append("    currentSeason: ").append(toIndentedString(currentSeason)).append("\n");
    sb.append("    maxSeason: ").append(toIndentedString(maxSeason)).append("\n");
    sb.append("    isDatafeedDown: ").append(toIndentedString(isDatafeedDown)).append("\n");
    sb.append("    downEvents: ").append(toIndentedString(downEvents)).append("\n");
    sb.append("    ios: ").append(toIndentedString(ios)).append("\n");
    sb.append("    android: ").append(toIndentedString(android)).append("\n");
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

