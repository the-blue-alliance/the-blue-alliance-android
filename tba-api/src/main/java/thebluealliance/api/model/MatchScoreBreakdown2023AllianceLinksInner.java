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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MatchScoreBreakdown2023AllianceLinksInner
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-02-03T11:29:24.649848-06:00[America/Chicago]", comments = "Generator version: 7.10.0")
public class MatchScoreBreakdown2023AllianceLinksInner {
  /**
   * Gets or Sets nodes
   */
  @JsonAdapter(NodesEnum.Adapter.class)
  public enum NodesEnum {
    NONE(String.valueOf("None")),
    
    CONE(String.valueOf("Cone")),
    
    CUBE(String.valueOf("Cube"));

    private String value;

    NodesEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static NodesEnum fromValue(String value) {
      for (NodesEnum b : NodesEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<NodesEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final NodesEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public NodesEnum read(final JsonReader jsonReader) throws IOException {
        String value =  jsonReader.nextString();
        return NodesEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_NODES = "nodes";
  @SerializedName(SERIALIZED_NAME_NODES)
  @javax.annotation.Nonnull
  private List<NodesEnum> nodes = new ArrayList<>();

  /**
   * Gets or Sets row
   */
  @JsonAdapter(RowEnum.Adapter.class)
  public enum RowEnum {
    BOTTOM(String.valueOf("Bottom")),
    
    MID(String.valueOf("Mid")),
    
    TOP(String.valueOf("Top"));

    private String value;

    RowEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static RowEnum fromValue(String value) {
      for (RowEnum b : RowEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<RowEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final RowEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public RowEnum read(final JsonReader jsonReader) throws IOException {
        String value =  jsonReader.nextString();
        return RowEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_ROW = "row";
  @SerializedName(SERIALIZED_NAME_ROW)
  @javax.annotation.Nonnull
  private RowEnum row;

  public MatchScoreBreakdown2023AllianceLinksInner() {
  }

  public MatchScoreBreakdown2023AllianceLinksInner nodes(@javax.annotation.Nonnull List<NodesEnum> nodes) {
    
    this.nodes = nodes;
    return this;
  }

  public MatchScoreBreakdown2023AllianceLinksInner addNodesItem(NodesEnum nodesItem) {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }
    this.nodes.add(nodesItem);
    return this;
  }

  /**
   * Get nodes
   * @return nodes
   */
  @javax.annotation.Nonnull

  public List<NodesEnum> getNodes() {
    return nodes;
  }


  public void setNodes(@javax.annotation.Nonnull List<NodesEnum> nodes) {
    this.nodes = nodes;
  }

  public MatchScoreBreakdown2023AllianceLinksInner row(@javax.annotation.Nonnull RowEnum row) {
    
    this.row = row;
    return this;
  }

  /**
   * Get row
   * @return row
   */
  @javax.annotation.Nonnull

  public RowEnum getRow() {
    return row;
  }


  public void setRow(@javax.annotation.Nonnull RowEnum row) {
    this.row = row;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatchScoreBreakdown2023AllianceLinksInner matchScoreBreakdown2023AllianceLinksInner = (MatchScoreBreakdown2023AllianceLinksInner) o;
    return Objects.equals(this.nodes, matchScoreBreakdown2023AllianceLinksInner.nodes) &&
        Objects.equals(this.row, matchScoreBreakdown2023AllianceLinksInner.row);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodes, row);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MatchScoreBreakdown2023AllianceLinksInner {\n");
    sb.append("    nodes: ").append(toIndentedString(nodes)).append("\n");
    sb.append("    row: ").append(toIndentedString(row)).append("\n");
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

