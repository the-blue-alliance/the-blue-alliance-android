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
import thebluealliance.api.model.AwardRecipient;

/**
 * Award
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-01-12T10:04:04.013721-06:00[America/Chicago]", comments = "Generator version: 7.10.0")
public class Award {
  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  @javax.annotation.Nonnull
  private String name;

  public static final String SERIALIZED_NAME_AWARD_TYPE = "award_type";
  @SerializedName(SERIALIZED_NAME_AWARD_TYPE)
  @javax.annotation.Nonnull
  private Integer awardType;

  public static final String SERIALIZED_NAME_EVENT_KEY = "event_key";
  @SerializedName(SERIALIZED_NAME_EVENT_KEY)
  @javax.annotation.Nonnull
  private String eventKey;

  public static final String SERIALIZED_NAME_RECIPIENT_LIST = "recipient_list";
  @SerializedName(SERIALIZED_NAME_RECIPIENT_LIST)
  @javax.annotation.Nonnull
  private List<AwardRecipient> recipientList = new ArrayList<>();

  public static final String SERIALIZED_NAME_YEAR = "year";
  @SerializedName(SERIALIZED_NAME_YEAR)
  @javax.annotation.Nonnull
  private Integer year;

  public Award() {
  }

  public Award name(@javax.annotation.Nonnull String name) {
    
    this.name = name;
    return this;
  }

  /**
   * The name of the award as provided by FIRST. May vary for the same award type.
   * @return name
   */
  @javax.annotation.Nonnull

  public String getName() {
    return name;
  }


  public void setName(@javax.annotation.Nonnull String name) {
    this.name = name;
  }

  public Award awardType(@javax.annotation.Nonnull Integer awardType) {
    
    this.awardType = awardType;
    return this;
  }

  /**
   * Type of award given. See https://github.com/the-blue-alliance/the-blue-alliance/blob/master/consts/award_type.py#L6
   * @return awardType
   */
  @javax.annotation.Nonnull

  public Integer getAwardType() {
    return awardType;
  }


  public void setAwardType(@javax.annotation.Nonnull Integer awardType) {
    this.awardType = awardType;
  }

  public Award eventKey(@javax.annotation.Nonnull String eventKey) {
    
    this.eventKey = eventKey;
    return this;
  }

  /**
   * The event_key of the event the award was won at.
   * @return eventKey
   */
  @javax.annotation.Nonnull

  public String getEventKey() {
    return eventKey;
  }


  public void setEventKey(@javax.annotation.Nonnull String eventKey) {
    this.eventKey = eventKey;
  }

  public Award recipientList(@javax.annotation.Nonnull List<AwardRecipient> recipientList) {
    
    this.recipientList = recipientList;
    return this;
  }

  public Award addRecipientListItem(AwardRecipient recipientListItem) {
    if (this.recipientList == null) {
      this.recipientList = new ArrayList<>();
    }
    this.recipientList.add(recipientListItem);
    return this;
  }

  /**
   * A list of recipients of the award at the event. May have either a team_key or an awardee, both, or neither (in the case the award wasn&#39;t awarded at the event).
   * @return recipientList
   */
  @javax.annotation.Nonnull

  public List<AwardRecipient> getRecipientList() {
    return recipientList;
  }


  public void setRecipientList(@javax.annotation.Nonnull List<AwardRecipient> recipientList) {
    this.recipientList = recipientList;
  }

  public Award year(@javax.annotation.Nonnull Integer year) {
    
    this.year = year;
    return this;
  }

  /**
   * The year this award was won.
   * @return year
   */
  @javax.annotation.Nonnull

  public Integer getYear() {
    return year;
  }


  public void setYear(@javax.annotation.Nonnull Integer year) {
    this.year = year;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Award award = (Award) o;
    return Objects.equals(this.name, award.name) &&
        Objects.equals(this.awardType, award.awardType) &&
        Objects.equals(this.eventKey, award.eventKey) &&
        Objects.equals(this.recipientList, award.recipientList) &&
        Objects.equals(this.year, award.year);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, awardType, eventKey, recipientList, year);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Award {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    awardType: ").append(toIndentedString(awardType)).append("\n");
    sb.append("    eventKey: ").append(toIndentedString(eventKey)).append("\n");
    sb.append("    recipientList: ").append(toIndentedString(recipientList)).append("\n");
    sb.append("    year: ").append(toIndentedString(year)).append("\n");
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

