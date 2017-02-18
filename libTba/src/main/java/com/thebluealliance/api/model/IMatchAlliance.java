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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;


/**
 * MatchAlliance
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-02-18T16:02:48.764-05:00")
public interface IMatchAlliance   {


   /**
   * Get score
   * @return score
  **/
  @ApiModelProperty(example = "null", required = true, value = "")
  
  public Integer getScore();

  public void setScore(Integer score);





   /**
   * Get surrogateTeamKeys
   * @return surrogateTeamKeys
  **/
  @ApiModelProperty(example = "null", value = "")
  @Nullable
  public List<String> getSurrogateTeamKeys();

  public void setSurrogateTeamKeys(List<String> surrogateTeamKeys);





   /**
   * Get teamKeys
   * @return teamKeys
  **/
  @ApiModelProperty(example = "null", required = true, value = "")
  
  public List<String> getTeamKeys();

  public void setTeamKeys(List<String> teamKeys);

}

