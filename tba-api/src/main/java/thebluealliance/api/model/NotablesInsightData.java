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
import thebluealliance.api.model.NotablesInsightDataEntriesInner;

/**
 * NotablesInsightData
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class NotablesInsightData {
  public static final String SERIALIZED_NAME_ENTRIES = "entries";
  @SerializedName(SERIALIZED_NAME_ENTRIES)
  @javax.annotation.Nonnull
  private List<NotablesInsightDataEntriesInner> entries = new ArrayList<>();

  public NotablesInsightData() {
  }

  public NotablesInsightData entries(@javax.annotation.Nonnull List<NotablesInsightDataEntriesInner> entries) {
    
    this.entries = entries;
    return this;
  }

  public NotablesInsightData addEntriesItem(NotablesInsightDataEntriesInner entriesItem) {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    this.entries.add(entriesItem);
    return this;
  }

  /**
   * Get entries
   * @return entries
   */
  @javax.annotation.Nonnull

  public List<NotablesInsightDataEntriesInner> getEntries() {
    return entries;
  }


  public void setEntries(@javax.annotation.Nonnull List<NotablesInsightDataEntriesInner> entries) {
    this.entries = entries;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotablesInsightData notablesInsightData = (NotablesInsightData) o;
    return Objects.equals(this.entries, notablesInsightData.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotablesInsightData {\n");
    sb.append("    entries: ").append(toIndentedString(entries)).append("\n");
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

