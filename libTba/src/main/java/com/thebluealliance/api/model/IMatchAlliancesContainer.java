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
import com.thebluealliance.api.model.IMatchAlliance;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Nullable;


/**
 * MatchAlliancesContainer
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2017-02-18T12:56:17.757-05:00")
public interface IMatchAlliancesContainer   {


   /**
   * Get blue
   * @return blue
  **/
  @ApiModelProperty(example = "null", required = true, value = "")
  
  public IMatchAlliance getBlue();

  public void setBlue(IMatchAlliance blue);



   /**
   * Get red
   * @return red
  **/
  @ApiModelProperty(example = "null", required = true, value = "")
  
  public IMatchAlliance getRed();

  public void setRed(IMatchAlliance red);

}

