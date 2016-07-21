/**
 * The Blue Alliance API
 * Access data about the FIRST Robotics Competition
 *
 * OpenAPI spec version: 2
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.thebluealliance.api.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * Media
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-07-20T22:10:55.016-04:00")
public class Media   {
  @SerializedName("type")
  private String type = null;

  @SerializedName("foreign_key")
  private String foreignKey = null;

  @SerializedName("details")
  private String details = null;

  public Media type(String type) {
    this.type = type;
    return this;
  }

   /**
   * The string type of the media element
   * @return type
  **/
  @ApiModelProperty(example = "null", value = "The string type of the media element")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Media foreignKey(String foreignKey) {
    this.foreignKey = foreignKey;
    return this;
  }

   /**
   * The key used to indentify this media element on the remote site (e.g YouTube video key)
   * @return foreignKey
  **/
  @ApiModelProperty(example = "null", value = "The key used to indentify this media element on the remote site (e.g YouTube video key)")
  public String getForeignKey() {
    return foreignKey;
  }

  public void setForeignKey(String foreignKey) {
    this.foreignKey = foreignKey;
  }

  public Media details(String details) {
    this.details = details;
    return this;
  }

   /**
   * If the media requires it, a json dict of additional information
   * @return details
  **/
  @ApiModelProperty(example = "null", value = "If the media requires it, a json dict of additional information")
  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Media media = (Media) o;
    return Objects.equals(this.type, media.type) &&
        Objects.equals(this.foreignKey, media.foreignKey) &&
        Objects.equals(this.details, media.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, foreignKey, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Media {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    foreignKey: ").append(toIndentedString(foreignKey)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

