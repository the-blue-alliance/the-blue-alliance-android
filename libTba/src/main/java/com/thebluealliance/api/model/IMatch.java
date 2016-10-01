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
 * Match
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-10-01T11:39:13.235-04:00")
public interface IMatch   {


   /**
   * A list of alliances, the teams on the alliances, and their score.
   * @return alliances
  **/
  @ApiModelProperty(example = "null", value = "A list of alliances, the teams on the alliances, and their score.")
  @Nullable
  public String getAlliances();

  public void setAlliances(String alliances);



   /**
   * The competition level the match was played at.
   * @return compLevel
  **/
  @ApiModelProperty(example = "null", required = true, value = "The competition level the match was played at.")
  
  public String getCompLevel();

  public void setCompLevel(String compLevel);



   /**
   * Event key of the event the match was played at.
   * @return eventKey
  **/
  @ApiModelProperty(example = "null", required = true, value = "Event key of the event the match was played at.")
  
  public String getEventKey();

  public void setEventKey(String eventKey);



   /**
   * TBA event key with the format yyyy[EVENT_CODE]_[COMP_LEVEL]m[MATCH_NUMBER], where yyyy is the year, and EVENT_CODE is the event code of the event, COMP_LEVEL is (qm, ef, qf, sf, f), and MATCH_NUMBER is the match number in the competition level. A set number may append the competition level if more than one match in required per set .
   * @return key
  **/
  @ApiModelProperty(example = "null", required = true, value = "TBA event key with the format yyyy[EVENT_CODE]_[COMP_LEVEL]m[MATCH_NUMBER], where yyyy is the year, and EVENT_CODE is the event code of the event, COMP_LEVEL is (qm, ef, qf, sf, f), and MATCH_NUMBER is the match number in the competition level. A set number may append the competition level if more than one match in required per set .")
  
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
   * The match number of the match in the competition level.
   * @return matchNumber
  **/
  @ApiModelProperty(example = "null", required = true, value = "The match number of the match in the competition level.")
  
  public Integer getMatchNumber();

  public void setMatchNumber(Integer matchNumber);



   /**
   * Score breakdown for auto, teleop, etc. points. Varies from year to year. May be null.
   * @return scoreBreakdown
  **/
  @ApiModelProperty(example = "null", value = "Score breakdown for auto, teleop, etc. points. Varies from year to year. May be null.")
  @Nullable
  public String getScoreBreakdown();

  public void setScoreBreakdown(String scoreBreakdown);



   /**
   * The set number in a series of matches where more than one match is required in the match series.
   * @return setNumber
  **/
  @ApiModelProperty(example = "null", required = true, value = "The set number in a series of matches where more than one match is required in the match series.")
  
  public Integer getSetNumber();

  public void setSetNumber(Integer setNumber);



   /**
   * UNIX timestamp of match time, as taken from the published schedule
   * @return time
  **/
  @ApiModelProperty(example = "null", value = "UNIX timestamp of match time, as taken from the published schedule")
  @Nullable
  public Long getTime();

  public void setTime(Long time);



   /**
   * Time string for this match, as published on the official schedule. Of course, this may or may not be accurate, as events often run ahead or behind schedule
   * @return timeString
  **/
  @ApiModelProperty(example = "null", value = "Time string for this match, as published on the official schedule. Of course, this may or may not be accurate, as events often run ahead or behind schedule")
  @Nullable
  public String getTimeString();

  public void setTimeString(String timeString);



   /**
   * JSON array of videos associated with this match and corresponding information
   * @return videos
  **/
  @ApiModelProperty(example = "null", value = "JSON array of videos associated with this match and corresponding information")
  @Nullable
  public String getVideos();

  public void setVideos(String videos);

}

