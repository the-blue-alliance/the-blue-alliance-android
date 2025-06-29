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

/**
 * A year-specific event insight object expressed as a JSON string, separated in to &#x60;qual&#x60; and &#x60;playoff&#x60; fields. See also Event_Insights_2016, Event_Insights_2017, etc.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class EventInsights {
  public static final String SERIALIZED_NAME_QUAL = "qual";
  @SerializedName(SERIALIZED_NAME_QUAL)
  @javax.annotation.Nullable
  private Object qual;

  public static final String SERIALIZED_NAME_PLAYOFF = "playoff";
  @SerializedName(SERIALIZED_NAME_PLAYOFF)
  @javax.annotation.Nullable
  private Object playoff;

  public EventInsights() {
  }

  public EventInsights qual(@javax.annotation.Nullable Object qual) {
    
    this.qual = qual;
    return this;
  }

  /**
   * Inights for the qualification round of an event
   * @return qual
   */
  @javax.annotation.Nullable

  public Object getQual() {
    return qual;
  }


  public void setQual(@javax.annotation.Nullable Object qual) {
    this.qual = qual;
  }

  public EventInsights playoff(@javax.annotation.Nullable Object playoff) {
    
    this.playoff = playoff;
    return this;
  }

  /**
   * Insights for the playoff round of an event
   * @return playoff
   */
  @javax.annotation.Nullable

  public Object getPlayoff() {
    return playoff;
  }


  public void setPlayoff(@javax.annotation.Nullable Object playoff) {
    this.playoff = playoff;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventInsights eventInsights = (EventInsights) o;
    return Objects.equals(this.qual, eventInsights.qual) &&
        Objects.equals(this.playoff, eventInsights.playoff);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qual, playoff);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventInsights {\n");
    sb.append("    qual: ").append(toIndentedString(qual)).append("\n");
    sb.append("    playoff: ").append(toIndentedString(playoff)).append("\n");
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

