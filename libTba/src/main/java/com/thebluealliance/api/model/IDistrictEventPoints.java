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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nullable;


/**
 * DistrictEventPoints
 */

public interface IDistrictEventPoints   {


   /**
   * Number of points from alliance selection
   * @return alliancePoints
  **/
  @ApiModelProperty(example = "null", required = true, value = "Number of points from alliance selection")
  
  public Integer getAlliancePoints();

  public void setAlliancePoints(Integer alliancePoints);



   /**
   * Number of points from awards
   * @return awardPoints
  **/
  @ApiModelProperty(example = "null", required = true, value = "Number of points from awards")
  
  public Integer getAwardPoints();

  public void setAwardPoints(Integer awardPoints);



   /**
   * Does this event get the IDistrict CMP multiplier?
   * @return districtCmp
  **/
  @ApiModelProperty(example = "null", required = true, value = "Does this event get the IDistrict CMP multiplier?")
  
  public Boolean getDistrictCmp();

  public void setDistrictCmp(Boolean districtCmp);



   /**
   * Number of points from playoff matches
   * @return elimPoints
  **/
  @ApiModelProperty(example = "null", required = true, value = "Number of points from playoff matches")
  
  public Integer getElimPoints();

  public void setElimPoints(Integer elimPoints);



   /**
   * IEvent where these points were earned
   * @return eventKey
  **/
  @ApiModelProperty(example = "null", required = true, value = "Event where these points were earned")
  
  public String getEventKey();

  public void setEventKey(String eventKey);



   /**
   * Number of points from qualification matches
   * @return qualPoints
  **/
  @ApiModelProperty(example = "null", required = true, value = "Number of points from qualification matches")
  
  public Integer getQualPoints();

  public void setQualPoints(Integer qualPoints);



   /**
   * Total number of points from this event
   * @return total
  **/
  @ApiModelProperty(example = "null", required = true, value = "Total number of points from this event")
  
  public Integer getTotal();

  public void setTotal(Integer total);

}

