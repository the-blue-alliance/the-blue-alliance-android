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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The &#x60;Media&#x60; object contains a reference for most any media associated with a team or event on TBA.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class Media {
  /**
   * String type of the media element.
   */
  @JsonAdapter(TypeEnum.Adapter.class)
  public enum TypeEnum {
    YOUTUBE(String.valueOf("youtube")),
    
    CDPHOTOTHREAD(String.valueOf("cdphotothread")),
    
    IMGUR(String.valueOf("imgur")),
    
    FACEBOOK_PROFILE(String.valueOf("facebook-profile")),
    
    YOUTUBE_CHANNEL(String.valueOf("youtube-channel")),
    
    TWITTER_PROFILE(String.valueOf("twitter-profile")),
    
    GITHUB_PROFILE(String.valueOf("github-profile")),
    
    INSTAGRAM_PROFILE(String.valueOf("instagram-profile")),
    
    PERISCOPE_PROFILE(String.valueOf("periscope-profile")),
    
    GITLAB_PROFILE(String.valueOf("gitlab-profile")),
    
    GRABCAD(String.valueOf("grabcad")),
    
    INSTAGRAM_IMAGE(String.valueOf("instagram-image")),
    
    EXTERNAL_LINK(String.valueOf("external-link")),
    
    AVATAR(String.valueOf("avatar")),
    
    ONSHAPE(String.valueOf("onshape"));

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<TypeEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TypeEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TypeEnum read(final JsonReader jsonReader) throws IOException {
        String value =  jsonReader.nextString();
        return TypeEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_TYPE = "type";
  @SerializedName(SERIALIZED_NAME_TYPE)
  @javax.annotation.Nonnull
  private TypeEnum type;

  public static final String SERIALIZED_NAME_FOREIGN_KEY = "foreign_key";
  @SerializedName(SERIALIZED_NAME_FOREIGN_KEY)
  @javax.annotation.Nonnull
  private String foreignKey;

  public static final String SERIALIZED_NAME_DETAILS = "details";
  @SerializedName(SERIALIZED_NAME_DETAILS)
  @javax.annotation.Nullable
  private Map<String, Object> details = new HashMap<>();

  public static final String SERIALIZED_NAME_PREFERRED = "preferred";
  @SerializedName(SERIALIZED_NAME_PREFERRED)
  @javax.annotation.Nullable
  private Boolean preferred;

  public static final String SERIALIZED_NAME_TEAM_KEYS = "team_keys";
  @SerializedName(SERIALIZED_NAME_TEAM_KEYS)
  @javax.annotation.Nonnull
  private List<String> teamKeys = new ArrayList<>();

  public static final String SERIALIZED_NAME_DIRECT_URL = "direct_url";
  @SerializedName(SERIALIZED_NAME_DIRECT_URL)
  @javax.annotation.Nullable
  private String directUrl;

  public static final String SERIALIZED_NAME_VIEW_URL = "view_url";
  @SerializedName(SERIALIZED_NAME_VIEW_URL)
  @javax.annotation.Nullable
  private String viewUrl;

  public Media() {
  }

  public Media type(@javax.annotation.Nonnull TypeEnum type) {
    
    this.type = type;
    return this;
  }

  /**
   * String type of the media element.
   * @return type
   */
  @javax.annotation.Nonnull

  public TypeEnum getType() {
    return type;
  }


  public void setType(@javax.annotation.Nonnull TypeEnum type) {
    this.type = type;
  }

  public Media foreignKey(@javax.annotation.Nonnull String foreignKey) {
    
    this.foreignKey = foreignKey;
    return this;
  }

  /**
   * The key used to identify this media on the media site.
   * @return foreignKey
   */
  @javax.annotation.Nonnull

  public String getForeignKey() {
    return foreignKey;
  }


  public void setForeignKey(@javax.annotation.Nonnull String foreignKey) {
    this.foreignKey = foreignKey;
  }

  public Media details(@javax.annotation.Nullable Map<String, Object> details) {
    
    this.details = details;
    return this;
  }

  public Media putDetailsItem(String key, Object detailsItem) {
    if (this.details == null) {
      this.details = new HashMap<>();
    }
    this.details.put(key, detailsItem);
    return this;
  }

  /**
   * If required, a JSON dict of additional media information.
   * @return details
   */
  @javax.annotation.Nullable

  public Map<String, Object> getDetails() {
    return details;
  }


  public void setDetails(@javax.annotation.Nullable Map<String, Object> details) {
    this.details = details;
  }

  public Media preferred(@javax.annotation.Nullable Boolean preferred) {
    
    this.preferred = preferred;
    return this;
  }

  /**
   * True if the media is of high quality.
   * @return preferred
   */
  @javax.annotation.Nullable

  public Boolean getPreferred() {
    return preferred;
  }


  public void setPreferred(@javax.annotation.Nullable Boolean preferred) {
    this.preferred = preferred;
  }

  public Media teamKeys(@javax.annotation.Nonnull List<String> teamKeys) {
    
    this.teamKeys = teamKeys;
    return this;
  }

  public Media addTeamKeysItem(String teamKeysItem) {
    if (this.teamKeys == null) {
      this.teamKeys = new ArrayList<>();
    }
    this.teamKeys.add(teamKeysItem);
    return this;
  }

  /**
   * List of teams that this media belongs to. Most likely length 1.
   * @return teamKeys
   */
  @javax.annotation.Nonnull

  public List<String> getTeamKeys() {
    return teamKeys;
  }


  public void setTeamKeys(@javax.annotation.Nonnull List<String> teamKeys) {
    this.teamKeys = teamKeys;
  }

  public Media directUrl(@javax.annotation.Nullable String directUrl) {
    
    this.directUrl = directUrl;
    return this;
  }

  /**
   * Direct URL to the media.
   * @return directUrl
   */
  @javax.annotation.Nullable

  public String getDirectUrl() {
    return directUrl;
  }


  public void setDirectUrl(@javax.annotation.Nullable String directUrl) {
    this.directUrl = directUrl;
  }

  public Media viewUrl(@javax.annotation.Nullable String viewUrl) {
    
    this.viewUrl = viewUrl;
    return this;
  }

  /**
   * The URL that leads to the full web page for the media, if one exists.
   * @return viewUrl
   */
  @javax.annotation.Nullable

  public String getViewUrl() {
    return viewUrl;
  }


  public void setViewUrl(@javax.annotation.Nullable String viewUrl) {
    this.viewUrl = viewUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Media media = (Media) o;
    return Objects.equals(this.type, media.type) &&
        Objects.equals(this.foreignKey, media.foreignKey) &&
        Objects.equals(this.details, media.details) &&
        Objects.equals(this.preferred, media.preferred) &&
        Objects.equals(this.teamKeys, media.teamKeys) &&
        Objects.equals(this.directUrl, media.directUrl) &&
        Objects.equals(this.viewUrl, media.viewUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, foreignKey, details, preferred, teamKeys, directUrl, viewUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Media {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    foreignKey: ").append(toIndentedString(foreignKey)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    preferred: ").append(toIndentedString(preferred)).append("\n");
    sb.append("    teamKeys: ").append(toIndentedString(teamKeys)).append("\n");
    sb.append("    directUrl: ").append(toIndentedString(directUrl)).append("\n");
    sb.append("    viewUrl: ").append(toIndentedString(viewUrl)).append("\n");
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

