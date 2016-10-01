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
import javax.annotation.Nullable;


/**
 * Award
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-10-01T11:39:13.235-04:00")
public interface IAward   {


   /**
   * An integer that represents the award type as a constant.
   * @return awardType
  **/
  @ApiModelProperty(example = "null", required = true, value = "An integer that represents the award type as a constant.")
  
  public Integer getAwardType();

  public void setAwardType(Integer awardType);



   /**
   * The event_key of the event the award was won at.
   * @return eventKey
  **/
  @ApiModelProperty(example = "null", required = true, value = "The event_key of the event the award was won at.")
  
  public String getEventKey();

  public void setEventKey(String eventKey);



   /**
   * Unique key for this Award, formatted like <event key>:<type enum>
   * @return key
  **/
  @ApiModelProperty(example = "null", value = "Unique key for this Award, formatted like <event key>:<type enum>")
  @Nullable
  public String getKey();

  public void setKey(String key);



   /**
   * Timestamp this model was last modified
   * @return lastModified
  **/
  @ApiModelProperty(example = "null", value = "Timestamp this model was last modified")
  @Nullable
  public Long getLastModified();

  public void setLastModified(Long lastModified);



   /**
   * The name of the award as provided by FIRST. May vary for the same award type.
   * @return name
  **/
  @ApiModelProperty(example = "null", required = true, value = "The name of the award as provided by FIRST. May vary for the same award type.")
  
  public String getName();

  public void setName(String name);



   /**
   * A list of recipients of the award at the event. Either team_number or awardee for individual awards.
   * @return recipientList
  **/
  @ApiModelProperty(example = "null", value = "A list of recipients of the award at the event. Either team_number or awardee for individual awards.")
  @Nullable
  public String getRecipientList();

  public void setRecipientList(String recipientList);



   /**
   * The year this award was won.
   * @return year
  **/
  @ApiModelProperty(example = "null", required = true, value = "The year this award was won.")
  
  public Integer getYear();

  public void setYear(Integer year);

}

