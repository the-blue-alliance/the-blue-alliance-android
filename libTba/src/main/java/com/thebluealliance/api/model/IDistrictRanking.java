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
import com.thebluealliance.api.model.IDistrictRankingEventPoints;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nullable;


/**
 * DistrictRanking
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-02-17T11:25:52.522-05:00")
public interface IDistrictRanking   {


   /**
   * Get eventPoints
   * @return eventPoints
  **/
  @ApiModelProperty(example = "null", value = "")
  @Nullable
  public IDistrictRankingEventPoints getEventPoints();

  public void setEventPoints(IDistrictRankingEventPoints eventPoints);



   /**
   * Timestamp this model was last modified
   * @return lastModified
  **/
  @ApiModelProperty(example = "null", value = "Timestamp this model was last modified")
  @Nullable
  public Long getLastModified();

  public void setLastModified(Long lastModified);



   /**
   * Number of points earned, in total
   * @return pointTotal
  **/
  @ApiModelProperty(example = "null", required = true, value = "Number of points earned, in total")
  
  public Integer getPointTotal();

  public void setPointTotal(Integer pointTotal);



   /**
   * Ranking of this team in the district
   * @return rank
  **/
  @ApiModelProperty(example = "null", required = true, value = "Ranking of this team in the district")
  
  public Integer getRank();

  public void setRank(Integer rank);



   /**
   * Extra points based on begin a first or second year team
   * @return rookieBonus
  **/
  @ApiModelProperty(example = "null", required = true, value = "Extra points based on begin a first or second year team")
  
  public Integer getRookieBonus();

  public void setRookieBonus(Integer rookieBonus);



   /**
   * ITeam these rankings are for
   * @return teamKey
  **/
  @ApiModelProperty(example = "null", required = true, value = "Team these rankings are for")
  
  public String getTeamKey();

  public void setTeamKey(String teamKey);

}

