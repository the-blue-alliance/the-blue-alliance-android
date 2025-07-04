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
import thebluealliance.api.model.EliminationAllianceBackup;
import thebluealliance.api.model.EliminationAllianceStatus;

/**
 * EliminationAlliance
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class EliminationAlliance {
  public static final String SERIALIZED_NAME_NAME = "name";
  @SerializedName(SERIALIZED_NAME_NAME)
  @javax.annotation.Nullable
  private String name;

  public static final String SERIALIZED_NAME_BACKUP = "backup";
  @SerializedName(SERIALIZED_NAME_BACKUP)
  @javax.annotation.Nullable
  private EliminationAllianceBackup backup;

  public static final String SERIALIZED_NAME_DECLINES = "declines";
  @SerializedName(SERIALIZED_NAME_DECLINES)
  @javax.annotation.Nonnull
  private List<String> declines = new ArrayList<>();

  public static final String SERIALIZED_NAME_PICKS = "picks";
  @SerializedName(SERIALIZED_NAME_PICKS)
  @javax.annotation.Nonnull
  private List<String> picks = new ArrayList<>();

  public static final String SERIALIZED_NAME_STATUS = "status";
  @SerializedName(SERIALIZED_NAME_STATUS)
  @javax.annotation.Nullable
  private EliminationAllianceStatus status;

  public EliminationAlliance() {
  }

  public EliminationAlliance name(@javax.annotation.Nullable String name) {
    
    this.name = name;
    return this;
  }

  /**
   * Alliance name, may be null.
   * @return name
   */
  @javax.annotation.Nullable

  public String getName() {
    return name;
  }


  public void setName(@javax.annotation.Nullable String name) {
    this.name = name;
  }

  public EliminationAlliance backup(@javax.annotation.Nullable EliminationAllianceBackup backup) {
    
    this.backup = backup;
    return this;
  }

  /**
   * Get backup
   * @return backup
   */
  @javax.annotation.Nullable

  public EliminationAllianceBackup getBackup() {
    return backup;
  }


  public void setBackup(@javax.annotation.Nullable EliminationAllianceBackup backup) {
    this.backup = backup;
  }

  public EliminationAlliance declines(@javax.annotation.Nonnull List<String> declines) {
    
    this.declines = declines;
    return this;
  }

  public EliminationAlliance addDeclinesItem(String declinesItem) {
    if (this.declines == null) {
      this.declines = new ArrayList<>();
    }
    this.declines.add(declinesItem);
    return this;
  }

  /**
   * List of teams that declined the alliance.
   * @return declines
   */
  @javax.annotation.Nonnull

  public List<String> getDeclines() {
    return declines;
  }


  public void setDeclines(@javax.annotation.Nonnull List<String> declines) {
    this.declines = declines;
  }

  public EliminationAlliance picks(@javax.annotation.Nonnull List<String> picks) {
    
    this.picks = picks;
    return this;
  }

  public EliminationAlliance addPicksItem(String picksItem) {
    if (this.picks == null) {
      this.picks = new ArrayList<>();
    }
    this.picks.add(picksItem);
    return this;
  }

  /**
   * List of team keys picked for the alliance. First pick is captain.
   * @return picks
   */
  @javax.annotation.Nonnull

  public List<String> getPicks() {
    return picks;
  }


  public void setPicks(@javax.annotation.Nonnull List<String> picks) {
    this.picks = picks;
  }

  public EliminationAlliance status(@javax.annotation.Nullable EliminationAllianceStatus status) {
    
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @javax.annotation.Nullable

  public EliminationAllianceStatus getStatus() {
    return status;
  }


  public void setStatus(@javax.annotation.Nullable EliminationAllianceStatus status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EliminationAlliance eliminationAlliance = (EliminationAlliance) o;
    return Objects.equals(this.name, eliminationAlliance.name) &&
        Objects.equals(this.backup, eliminationAlliance.backup) &&
        Objects.equals(this.declines, eliminationAlliance.declines) &&
        Objects.equals(this.picks, eliminationAlliance.picks) &&
        Objects.equals(this.status, eliminationAlliance.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, backup, declines, picks, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EliminationAlliance {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    backup: ").append(toIndentedString(backup)).append("\n");
    sb.append("    declines: ").append(toIndentedString(declines)).append("\n");
    sb.append("    picks: ").append(toIndentedString(picks)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

