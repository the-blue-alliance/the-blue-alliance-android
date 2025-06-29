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
import thebluealliance.api.model.Award;
import thebluealliance.api.model.Event;

/**
 * History
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class History {
  public static final String SERIALIZED_NAME_EVENTS = "events";
  @SerializedName(SERIALIZED_NAME_EVENTS)
  @javax.annotation.Nonnull
  private List<Event> events = new ArrayList<>();

  public static final String SERIALIZED_NAME_AWARDS = "awards";
  @SerializedName(SERIALIZED_NAME_AWARDS)
  @javax.annotation.Nonnull
  private List<Award> awards = new ArrayList<>();

  public History() {
  }

  public History events(@javax.annotation.Nonnull List<Event> events) {
    
    this.events = events;
    return this;
  }

  public History addEventsItem(Event eventsItem) {
    if (this.events == null) {
      this.events = new ArrayList<>();
    }
    this.events.add(eventsItem);
    return this;
  }

  /**
   * Get events
   * @return events
   */
  @javax.annotation.Nonnull

  public List<Event> getEvents() {
    return events;
  }


  public void setEvents(@javax.annotation.Nonnull List<Event> events) {
    this.events = events;
  }

  public History awards(@javax.annotation.Nonnull List<Award> awards) {
    
    this.awards = awards;
    return this;
  }

  public History addAwardsItem(Award awardsItem) {
    if (this.awards == null) {
      this.awards = new ArrayList<>();
    }
    this.awards.add(awardsItem);
    return this;
  }

  /**
   * Get awards
   * @return awards
   */
  @javax.annotation.Nonnull

  public List<Award> getAwards() {
    return awards;
  }


  public void setAwards(@javax.annotation.Nonnull List<Award> awards) {
    this.awards = awards;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    History history = (History) o;
    return Objects.equals(this.events, history.events) &&
        Objects.equals(this.awards, history.awards);
  }

  @Override
  public int hashCode() {
    return Objects.hash(events, awards);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class History {\n");
    sb.append("    events: ").append(toIndentedString(events)).append("\n");
    sb.append("    awards: ").append(toIndentedString(awards)).append("\n");
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

