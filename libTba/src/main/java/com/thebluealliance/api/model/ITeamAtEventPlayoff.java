/**
 * The Blue Alliance APIv3
 * Access data about the FIRST Robotics Competition
 *
 * OpenAPI spec version: 3
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
import com.thebluealliance.api.model.ITeamRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nullable;


/**
 * TeamAtEventPlayoff
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-02-18T23:19:19.757-05:00")
public interface ITeamAtEventPlayoff   {


   /**
   * Get currentLevelRecord
   * @return currentLevelRecord
  **/
  @ApiModelProperty(example = "null", value = "")
  @Nullable
  public ITeamRecord getCurrentLevelRecord();

  public void setCurrentLevelRecord(ITeamRecord currentLevelRecord);



   /**
   * Get level
   * @return level
  **/
  @ApiModelProperty(example = "null", required = true, value = "")
  
  public String getLevel();

  public void setLevel(String level);



   /**
   * Get playoffAverage
   * @return playoffAverage
  **/
  @ApiModelProperty(example = "null", value = "")
  @Nullable
  public Double getPlayoffAverage();

  public void setPlayoffAverage(Double playoffAverage);



   /**
   * Get record
   * @return record
  **/
  @ApiModelProperty(example = "null", value = "")
  @Nullable
  public ITeamRecord getRecord();

  public void setRecord(ITeamRecord record);



   /**
   * Get status
   * @return status
  **/
  @ApiModelProperty(example = "null", required = true, value = "")
  
  public String getStatus();

  public void setStatus(String status);

}

