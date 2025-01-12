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
import thebluealliance.api.model.MatchScoreBreakdown2024Alliance;

/**
 * See the 2024 FMS API documentation for a description of each value. https://frc-api-docs.firstinspires.org
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-01-12T10:04:04.013721-06:00[America/Chicago]", comments = "Generator version: 7.10.0")
public class MatchScoreBreakdown2024 {
  public static final String SERIALIZED_NAME_BLUE = "blue";
  @SerializedName(SERIALIZED_NAME_BLUE)
  @javax.annotation.Nonnull
  private MatchScoreBreakdown2024Alliance blue;

  public static final String SERIALIZED_NAME_RED = "red";
  @SerializedName(SERIALIZED_NAME_RED)
  @javax.annotation.Nonnull
  private MatchScoreBreakdown2024Alliance red;

  public MatchScoreBreakdown2024() {
  }

  public MatchScoreBreakdown2024 blue(@javax.annotation.Nonnull MatchScoreBreakdown2024Alliance blue) {
    
    this.blue = blue;
    return this;
  }

  /**
   * Get blue
   * @return blue
   */
  @javax.annotation.Nonnull

  public MatchScoreBreakdown2024Alliance getBlue() {
    return blue;
  }


  public void setBlue(@javax.annotation.Nonnull MatchScoreBreakdown2024Alliance blue) {
    this.blue = blue;
  }

  public MatchScoreBreakdown2024 red(@javax.annotation.Nonnull MatchScoreBreakdown2024Alliance red) {
    
    this.red = red;
    return this;
  }

  /**
   * Get red
   * @return red
   */
  @javax.annotation.Nonnull

  public MatchScoreBreakdown2024Alliance getRed() {
    return red;
  }


  public void setRed(@javax.annotation.Nonnull MatchScoreBreakdown2024Alliance red) {
    this.red = red;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatchScoreBreakdown2024 matchScoreBreakdown2024 = (MatchScoreBreakdown2024) o;
    return Objects.equals(this.blue, matchScoreBreakdown2024.blue) &&
        Objects.equals(this.red, matchScoreBreakdown2024.red);
  }

  @Override
  public int hashCode() {
    return Objects.hash(blue, red);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MatchScoreBreakdown2024 {\n");
    sb.append("    blue: ").append(toIndentedString(blue)).append("\n");
    sb.append("    red: ").append(toIndentedString(red)).append("\n");
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

