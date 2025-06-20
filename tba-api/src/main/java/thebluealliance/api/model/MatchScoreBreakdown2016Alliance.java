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

/**
 * MatchScoreBreakdown2016Alliance
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class MatchScoreBreakdown2016Alliance {
  public static final String SERIALIZED_NAME_AUTO_POINTS = "autoPoints";
  @SerializedName(SERIALIZED_NAME_AUTO_POINTS)
  @javax.annotation.Nullable
  private Integer autoPoints;

  public static final String SERIALIZED_NAME_TELEOP_POINTS = "teleopPoints";
  @SerializedName(SERIALIZED_NAME_TELEOP_POINTS)
  @javax.annotation.Nullable
  private Integer teleopPoints;

  public static final String SERIALIZED_NAME_BREACH_POINTS = "breachPoints";
  @SerializedName(SERIALIZED_NAME_BREACH_POINTS)
  @javax.annotation.Nullable
  private Integer breachPoints;

  public static final String SERIALIZED_NAME_FOUL_POINTS = "foulPoints";
  @SerializedName(SERIALIZED_NAME_FOUL_POINTS)
  @javax.annotation.Nullable
  private Integer foulPoints;

  public static final String SERIALIZED_NAME_CAPTURE_POINTS = "capturePoints";
  @SerializedName(SERIALIZED_NAME_CAPTURE_POINTS)
  @javax.annotation.Nullable
  private Integer capturePoints;

  public static final String SERIALIZED_NAME_ADJUST_POINTS = "adjustPoints";
  @SerializedName(SERIALIZED_NAME_ADJUST_POINTS)
  @javax.annotation.Nullable
  private Integer adjustPoints;

  public static final String SERIALIZED_NAME_TOTAL_POINTS = "totalPoints";
  @SerializedName(SERIALIZED_NAME_TOTAL_POINTS)
  @javax.annotation.Nullable
  private Integer totalPoints;

  /**
   * Gets or Sets robot1Auto
   */
  @JsonAdapter(Robot1AutoEnum.Adapter.class)
  public enum Robot1AutoEnum {
    CROSSED(String.valueOf("Crossed")),
    
    REACHED(String.valueOf("Reached")),
    
    NONE(String.valueOf("None"));

    private String value;

    Robot1AutoEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static Robot1AutoEnum fromValue(String value) {
      for (Robot1AutoEnum b : Robot1AutoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<Robot1AutoEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final Robot1AutoEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public Robot1AutoEnum read(final JsonReader jsonReader) throws IOException {
        String value =  jsonReader.nextString();
        return Robot1AutoEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_ROBOT1_AUTO = "robot1Auto";
  @SerializedName(SERIALIZED_NAME_ROBOT1_AUTO)
  @javax.annotation.Nullable
  private Robot1AutoEnum robot1Auto;

  /**
   * Gets or Sets robot2Auto
   */
  @JsonAdapter(Robot2AutoEnum.Adapter.class)
  public enum Robot2AutoEnum {
    CROSSED(String.valueOf("Crossed")),
    
    REACHED(String.valueOf("Reached")),
    
    NONE(String.valueOf("None"));

    private String value;

    Robot2AutoEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static Robot2AutoEnum fromValue(String value) {
      for (Robot2AutoEnum b : Robot2AutoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<Robot2AutoEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final Robot2AutoEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public Robot2AutoEnum read(final JsonReader jsonReader) throws IOException {
        String value =  jsonReader.nextString();
        return Robot2AutoEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_ROBOT2_AUTO = "robot2Auto";
  @SerializedName(SERIALIZED_NAME_ROBOT2_AUTO)
  @javax.annotation.Nullable
  private Robot2AutoEnum robot2Auto;

  /**
   * Gets or Sets robot3Auto
   */
  @JsonAdapter(Robot3AutoEnum.Adapter.class)
  public enum Robot3AutoEnum {
    CROSSED(String.valueOf("Crossed")),
    
    REACHED(String.valueOf("Reached")),
    
    NONE(String.valueOf("None"));

    private String value;

    Robot3AutoEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static Robot3AutoEnum fromValue(String value) {
      for (Robot3AutoEnum b : Robot3AutoEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<Robot3AutoEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final Robot3AutoEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public Robot3AutoEnum read(final JsonReader jsonReader) throws IOException {
        String value =  jsonReader.nextString();
        return Robot3AutoEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_ROBOT3_AUTO = "robot3Auto";
  @SerializedName(SERIALIZED_NAME_ROBOT3_AUTO)
  @javax.annotation.Nullable
  private Robot3AutoEnum robot3Auto;

  public static final String SERIALIZED_NAME_AUTO_REACH_POINTS = "autoReachPoints";
  @SerializedName(SERIALIZED_NAME_AUTO_REACH_POINTS)
  @javax.annotation.Nullable
  private Integer autoReachPoints;

  public static final String SERIALIZED_NAME_AUTO_CROSSING_POINTS = "autoCrossingPoints";
  @SerializedName(SERIALIZED_NAME_AUTO_CROSSING_POINTS)
  @javax.annotation.Nullable
  private Integer autoCrossingPoints;

  public static final String SERIALIZED_NAME_AUTO_BOULDERS_LOW = "autoBouldersLow";
  @SerializedName(SERIALIZED_NAME_AUTO_BOULDERS_LOW)
  @javax.annotation.Nullable
  private Integer autoBouldersLow;

  public static final String SERIALIZED_NAME_AUTO_BOULDERS_HIGH = "autoBouldersHigh";
  @SerializedName(SERIALIZED_NAME_AUTO_BOULDERS_HIGH)
  @javax.annotation.Nullable
  private Integer autoBouldersHigh;

  public static final String SERIALIZED_NAME_AUTO_BOULDER_POINTS = "autoBoulderPoints";
  @SerializedName(SERIALIZED_NAME_AUTO_BOULDER_POINTS)
  @javax.annotation.Nullable
  private Integer autoBoulderPoints;

  public static final String SERIALIZED_NAME_TELEOP_CROSSING_POINTS = "teleopCrossingPoints";
  @SerializedName(SERIALIZED_NAME_TELEOP_CROSSING_POINTS)
  @javax.annotation.Nullable
  private Integer teleopCrossingPoints;

  public static final String SERIALIZED_NAME_TELEOP_BOULDERS_LOW = "teleopBouldersLow";
  @SerializedName(SERIALIZED_NAME_TELEOP_BOULDERS_LOW)
  @javax.annotation.Nullable
  private Integer teleopBouldersLow;

  public static final String SERIALIZED_NAME_TELEOP_BOULDERS_HIGH = "teleopBouldersHigh";
  @SerializedName(SERIALIZED_NAME_TELEOP_BOULDERS_HIGH)
  @javax.annotation.Nullable
  private Integer teleopBouldersHigh;

  public static final String SERIALIZED_NAME_TELEOP_BOULDER_POINTS = "teleopBoulderPoints";
  @SerializedName(SERIALIZED_NAME_TELEOP_BOULDER_POINTS)
  @javax.annotation.Nullable
  private Integer teleopBoulderPoints;

  public static final String SERIALIZED_NAME_TELEOP_DEFENSES_BREACHED = "teleopDefensesBreached";
  @SerializedName(SERIALIZED_NAME_TELEOP_DEFENSES_BREACHED)
  @javax.annotation.Nullable
  private Boolean teleopDefensesBreached;

  public static final String SERIALIZED_NAME_TELEOP_CHALLENGE_POINTS = "teleopChallengePoints";
  @SerializedName(SERIALIZED_NAME_TELEOP_CHALLENGE_POINTS)
  @javax.annotation.Nullable
  private Integer teleopChallengePoints;

  public static final String SERIALIZED_NAME_TELEOP_SCALE_POINTS = "teleopScalePoints";
  @SerializedName(SERIALIZED_NAME_TELEOP_SCALE_POINTS)
  @javax.annotation.Nullable
  private Integer teleopScalePoints;

  public static final String SERIALIZED_NAME_TELEOP_TOWER_CAPTURED = "teleopTowerCaptured";
  @SerializedName(SERIALIZED_NAME_TELEOP_TOWER_CAPTURED)
  @javax.annotation.Nullable
  private Boolean teleopTowerCaptured;

  public static final String SERIALIZED_NAME_TOWER_FACE_A = "towerFaceA";
  @SerializedName(SERIALIZED_NAME_TOWER_FACE_A)
  @javax.annotation.Nullable
  private String towerFaceA;

  public static final String SERIALIZED_NAME_TOWER_FACE_B = "towerFaceB";
  @SerializedName(SERIALIZED_NAME_TOWER_FACE_B)
  @javax.annotation.Nullable
  private String towerFaceB;

  public static final String SERIALIZED_NAME_TOWER_FACE_C = "towerFaceC";
  @SerializedName(SERIALIZED_NAME_TOWER_FACE_C)
  @javax.annotation.Nullable
  private String towerFaceC;

  public static final String SERIALIZED_NAME_TOWER_END_STRENGTH = "towerEndStrength";
  @SerializedName(SERIALIZED_NAME_TOWER_END_STRENGTH)
  @javax.annotation.Nullable
  private Integer towerEndStrength;

  public static final String SERIALIZED_NAME_TECH_FOUL_COUNT = "techFoulCount";
  @SerializedName(SERIALIZED_NAME_TECH_FOUL_COUNT)
  @javax.annotation.Nullable
  private Integer techFoulCount;

  public static final String SERIALIZED_NAME_FOUL_COUNT = "foulCount";
  @SerializedName(SERIALIZED_NAME_FOUL_COUNT)
  @javax.annotation.Nullable
  private Integer foulCount;

  public static final String SERIALIZED_NAME_POSITION2 = "position2";
  @SerializedName(SERIALIZED_NAME_POSITION2)
  @javax.annotation.Nullable
  private String position2;

  public static final String SERIALIZED_NAME_POSITION3 = "position3";
  @SerializedName(SERIALIZED_NAME_POSITION3)
  @javax.annotation.Nullable
  private String position3;

  public static final String SERIALIZED_NAME_POSITION4 = "position4";
  @SerializedName(SERIALIZED_NAME_POSITION4)
  @javax.annotation.Nullable
  private String position4;

  public static final String SERIALIZED_NAME_POSITION5 = "position5";
  @SerializedName(SERIALIZED_NAME_POSITION5)
  @javax.annotation.Nullable
  private String position5;

  public static final String SERIALIZED_NAME_POSITION1CROSSINGS = "position1crossings";
  @SerializedName(SERIALIZED_NAME_POSITION1CROSSINGS)
  @javax.annotation.Nullable
  private Integer position1crossings;

  public static final String SERIALIZED_NAME_POSITION2CROSSINGS = "position2crossings";
  @SerializedName(SERIALIZED_NAME_POSITION2CROSSINGS)
  @javax.annotation.Nullable
  private Integer position2crossings;

  public static final String SERIALIZED_NAME_POSITION3CROSSINGS = "position3crossings";
  @SerializedName(SERIALIZED_NAME_POSITION3CROSSINGS)
  @javax.annotation.Nullable
  private Integer position3crossings;

  public static final String SERIALIZED_NAME_POSITION4CROSSINGS = "position4crossings";
  @SerializedName(SERIALIZED_NAME_POSITION4CROSSINGS)
  @javax.annotation.Nullable
  private Integer position4crossings;

  public static final String SERIALIZED_NAME_POSITION5CROSSINGS = "position5crossings";
  @SerializedName(SERIALIZED_NAME_POSITION5CROSSINGS)
  @javax.annotation.Nullable
  private Integer position5crossings;

  public MatchScoreBreakdown2016Alliance() {
  }

  public MatchScoreBreakdown2016Alliance autoPoints(@javax.annotation.Nullable Integer autoPoints) {
    
    this.autoPoints = autoPoints;
    return this;
  }

  /**
   * Get autoPoints
   * @return autoPoints
   */
  @javax.annotation.Nullable

  public Integer getAutoPoints() {
    return autoPoints;
  }


  public void setAutoPoints(@javax.annotation.Nullable Integer autoPoints) {
    this.autoPoints = autoPoints;
  }

  public MatchScoreBreakdown2016Alliance teleopPoints(@javax.annotation.Nullable Integer teleopPoints) {
    
    this.teleopPoints = teleopPoints;
    return this;
  }

  /**
   * Get teleopPoints
   * @return teleopPoints
   */
  @javax.annotation.Nullable

  public Integer getTeleopPoints() {
    return teleopPoints;
  }


  public void setTeleopPoints(@javax.annotation.Nullable Integer teleopPoints) {
    this.teleopPoints = teleopPoints;
  }

  public MatchScoreBreakdown2016Alliance breachPoints(@javax.annotation.Nullable Integer breachPoints) {
    
    this.breachPoints = breachPoints;
    return this;
  }

  /**
   * Get breachPoints
   * @return breachPoints
   */
  @javax.annotation.Nullable

  public Integer getBreachPoints() {
    return breachPoints;
  }


  public void setBreachPoints(@javax.annotation.Nullable Integer breachPoints) {
    this.breachPoints = breachPoints;
  }

  public MatchScoreBreakdown2016Alliance foulPoints(@javax.annotation.Nullable Integer foulPoints) {
    
    this.foulPoints = foulPoints;
    return this;
  }

  /**
   * Get foulPoints
   * @return foulPoints
   */
  @javax.annotation.Nullable

  public Integer getFoulPoints() {
    return foulPoints;
  }


  public void setFoulPoints(@javax.annotation.Nullable Integer foulPoints) {
    this.foulPoints = foulPoints;
  }

  public MatchScoreBreakdown2016Alliance capturePoints(@javax.annotation.Nullable Integer capturePoints) {
    
    this.capturePoints = capturePoints;
    return this;
  }

  /**
   * Get capturePoints
   * @return capturePoints
   */
  @javax.annotation.Nullable

  public Integer getCapturePoints() {
    return capturePoints;
  }


  public void setCapturePoints(@javax.annotation.Nullable Integer capturePoints) {
    this.capturePoints = capturePoints;
  }

  public MatchScoreBreakdown2016Alliance adjustPoints(@javax.annotation.Nullable Integer adjustPoints) {
    
    this.adjustPoints = adjustPoints;
    return this;
  }

  /**
   * Get adjustPoints
   * @return adjustPoints
   */
  @javax.annotation.Nullable

  public Integer getAdjustPoints() {
    return adjustPoints;
  }


  public void setAdjustPoints(@javax.annotation.Nullable Integer adjustPoints) {
    this.adjustPoints = adjustPoints;
  }

  public MatchScoreBreakdown2016Alliance totalPoints(@javax.annotation.Nullable Integer totalPoints) {
    
    this.totalPoints = totalPoints;
    return this;
  }

  /**
   * Get totalPoints
   * @return totalPoints
   */
  @javax.annotation.Nullable

  public Integer getTotalPoints() {
    return totalPoints;
  }


  public void setTotalPoints(@javax.annotation.Nullable Integer totalPoints) {
    this.totalPoints = totalPoints;
  }

  public MatchScoreBreakdown2016Alliance robot1Auto(@javax.annotation.Nullable Robot1AutoEnum robot1Auto) {
    
    this.robot1Auto = robot1Auto;
    return this;
  }

  /**
   * Get robot1Auto
   * @return robot1Auto
   */
  @javax.annotation.Nullable

  public Robot1AutoEnum getRobot1Auto() {
    return robot1Auto;
  }


  public void setRobot1Auto(@javax.annotation.Nullable Robot1AutoEnum robot1Auto) {
    this.robot1Auto = robot1Auto;
  }

  public MatchScoreBreakdown2016Alliance robot2Auto(@javax.annotation.Nullable Robot2AutoEnum robot2Auto) {
    
    this.robot2Auto = robot2Auto;
    return this;
  }

  /**
   * Get robot2Auto
   * @return robot2Auto
   */
  @javax.annotation.Nullable

  public Robot2AutoEnum getRobot2Auto() {
    return robot2Auto;
  }


  public void setRobot2Auto(@javax.annotation.Nullable Robot2AutoEnum robot2Auto) {
    this.robot2Auto = robot2Auto;
  }

  public MatchScoreBreakdown2016Alliance robot3Auto(@javax.annotation.Nullable Robot3AutoEnum robot3Auto) {
    
    this.robot3Auto = robot3Auto;
    return this;
  }

  /**
   * Get robot3Auto
   * @return robot3Auto
   */
  @javax.annotation.Nullable

  public Robot3AutoEnum getRobot3Auto() {
    return robot3Auto;
  }


  public void setRobot3Auto(@javax.annotation.Nullable Robot3AutoEnum robot3Auto) {
    this.robot3Auto = robot3Auto;
  }

  public MatchScoreBreakdown2016Alliance autoReachPoints(@javax.annotation.Nullable Integer autoReachPoints) {
    
    this.autoReachPoints = autoReachPoints;
    return this;
  }

  /**
   * Get autoReachPoints
   * @return autoReachPoints
   */
  @javax.annotation.Nullable

  public Integer getAutoReachPoints() {
    return autoReachPoints;
  }


  public void setAutoReachPoints(@javax.annotation.Nullable Integer autoReachPoints) {
    this.autoReachPoints = autoReachPoints;
  }

  public MatchScoreBreakdown2016Alliance autoCrossingPoints(@javax.annotation.Nullable Integer autoCrossingPoints) {
    
    this.autoCrossingPoints = autoCrossingPoints;
    return this;
  }

  /**
   * Get autoCrossingPoints
   * @return autoCrossingPoints
   */
  @javax.annotation.Nullable

  public Integer getAutoCrossingPoints() {
    return autoCrossingPoints;
  }


  public void setAutoCrossingPoints(@javax.annotation.Nullable Integer autoCrossingPoints) {
    this.autoCrossingPoints = autoCrossingPoints;
  }

  public MatchScoreBreakdown2016Alliance autoBouldersLow(@javax.annotation.Nullable Integer autoBouldersLow) {
    
    this.autoBouldersLow = autoBouldersLow;
    return this;
  }

  /**
   * Get autoBouldersLow
   * @return autoBouldersLow
   */
  @javax.annotation.Nullable

  public Integer getAutoBouldersLow() {
    return autoBouldersLow;
  }


  public void setAutoBouldersLow(@javax.annotation.Nullable Integer autoBouldersLow) {
    this.autoBouldersLow = autoBouldersLow;
  }

  public MatchScoreBreakdown2016Alliance autoBouldersHigh(@javax.annotation.Nullable Integer autoBouldersHigh) {
    
    this.autoBouldersHigh = autoBouldersHigh;
    return this;
  }

  /**
   * Get autoBouldersHigh
   * @return autoBouldersHigh
   */
  @javax.annotation.Nullable

  public Integer getAutoBouldersHigh() {
    return autoBouldersHigh;
  }


  public void setAutoBouldersHigh(@javax.annotation.Nullable Integer autoBouldersHigh) {
    this.autoBouldersHigh = autoBouldersHigh;
  }

  public MatchScoreBreakdown2016Alliance autoBoulderPoints(@javax.annotation.Nullable Integer autoBoulderPoints) {
    
    this.autoBoulderPoints = autoBoulderPoints;
    return this;
  }

  /**
   * Get autoBoulderPoints
   * @return autoBoulderPoints
   */
  @javax.annotation.Nullable

  public Integer getAutoBoulderPoints() {
    return autoBoulderPoints;
  }


  public void setAutoBoulderPoints(@javax.annotation.Nullable Integer autoBoulderPoints) {
    this.autoBoulderPoints = autoBoulderPoints;
  }

  public MatchScoreBreakdown2016Alliance teleopCrossingPoints(@javax.annotation.Nullable Integer teleopCrossingPoints) {
    
    this.teleopCrossingPoints = teleopCrossingPoints;
    return this;
  }

  /**
   * Get teleopCrossingPoints
   * @return teleopCrossingPoints
   */
  @javax.annotation.Nullable

  public Integer getTeleopCrossingPoints() {
    return teleopCrossingPoints;
  }


  public void setTeleopCrossingPoints(@javax.annotation.Nullable Integer teleopCrossingPoints) {
    this.teleopCrossingPoints = teleopCrossingPoints;
  }

  public MatchScoreBreakdown2016Alliance teleopBouldersLow(@javax.annotation.Nullable Integer teleopBouldersLow) {
    
    this.teleopBouldersLow = teleopBouldersLow;
    return this;
  }

  /**
   * Get teleopBouldersLow
   * @return teleopBouldersLow
   */
  @javax.annotation.Nullable

  public Integer getTeleopBouldersLow() {
    return teleopBouldersLow;
  }


  public void setTeleopBouldersLow(@javax.annotation.Nullable Integer teleopBouldersLow) {
    this.teleopBouldersLow = teleopBouldersLow;
  }

  public MatchScoreBreakdown2016Alliance teleopBouldersHigh(@javax.annotation.Nullable Integer teleopBouldersHigh) {
    
    this.teleopBouldersHigh = teleopBouldersHigh;
    return this;
  }

  /**
   * Get teleopBouldersHigh
   * @return teleopBouldersHigh
   */
  @javax.annotation.Nullable

  public Integer getTeleopBouldersHigh() {
    return teleopBouldersHigh;
  }


  public void setTeleopBouldersHigh(@javax.annotation.Nullable Integer teleopBouldersHigh) {
    this.teleopBouldersHigh = teleopBouldersHigh;
  }

  public MatchScoreBreakdown2016Alliance teleopBoulderPoints(@javax.annotation.Nullable Integer teleopBoulderPoints) {
    
    this.teleopBoulderPoints = teleopBoulderPoints;
    return this;
  }

  /**
   * Get teleopBoulderPoints
   * @return teleopBoulderPoints
   */
  @javax.annotation.Nullable

  public Integer getTeleopBoulderPoints() {
    return teleopBoulderPoints;
  }


  public void setTeleopBoulderPoints(@javax.annotation.Nullable Integer teleopBoulderPoints) {
    this.teleopBoulderPoints = teleopBoulderPoints;
  }

  public MatchScoreBreakdown2016Alliance teleopDefensesBreached(@javax.annotation.Nullable Boolean teleopDefensesBreached) {
    
    this.teleopDefensesBreached = teleopDefensesBreached;
    return this;
  }

  /**
   * Get teleopDefensesBreached
   * @return teleopDefensesBreached
   */
  @javax.annotation.Nullable

  public Boolean getTeleopDefensesBreached() {
    return teleopDefensesBreached;
  }


  public void setTeleopDefensesBreached(@javax.annotation.Nullable Boolean teleopDefensesBreached) {
    this.teleopDefensesBreached = teleopDefensesBreached;
  }

  public MatchScoreBreakdown2016Alliance teleopChallengePoints(@javax.annotation.Nullable Integer teleopChallengePoints) {
    
    this.teleopChallengePoints = teleopChallengePoints;
    return this;
  }

  /**
   * Get teleopChallengePoints
   * @return teleopChallengePoints
   */
  @javax.annotation.Nullable

  public Integer getTeleopChallengePoints() {
    return teleopChallengePoints;
  }


  public void setTeleopChallengePoints(@javax.annotation.Nullable Integer teleopChallengePoints) {
    this.teleopChallengePoints = teleopChallengePoints;
  }

  public MatchScoreBreakdown2016Alliance teleopScalePoints(@javax.annotation.Nullable Integer teleopScalePoints) {
    
    this.teleopScalePoints = teleopScalePoints;
    return this;
  }

  /**
   * Get teleopScalePoints
   * @return teleopScalePoints
   */
  @javax.annotation.Nullable

  public Integer getTeleopScalePoints() {
    return teleopScalePoints;
  }


  public void setTeleopScalePoints(@javax.annotation.Nullable Integer teleopScalePoints) {
    this.teleopScalePoints = teleopScalePoints;
  }

  public MatchScoreBreakdown2016Alliance teleopTowerCaptured(@javax.annotation.Nullable Boolean teleopTowerCaptured) {
    
    this.teleopTowerCaptured = teleopTowerCaptured;
    return this;
  }

  /**
   * Get teleopTowerCaptured
   * @return teleopTowerCaptured
   */
  @javax.annotation.Nullable

  public Boolean getTeleopTowerCaptured() {
    return teleopTowerCaptured;
  }


  public void setTeleopTowerCaptured(@javax.annotation.Nullable Boolean teleopTowerCaptured) {
    this.teleopTowerCaptured = teleopTowerCaptured;
  }

  public MatchScoreBreakdown2016Alliance towerFaceA(@javax.annotation.Nullable String towerFaceA) {
    
    this.towerFaceA = towerFaceA;
    return this;
  }

  /**
   * Get towerFaceA
   * @return towerFaceA
   */
  @javax.annotation.Nullable

  public String getTowerFaceA() {
    return towerFaceA;
  }


  public void setTowerFaceA(@javax.annotation.Nullable String towerFaceA) {
    this.towerFaceA = towerFaceA;
  }

  public MatchScoreBreakdown2016Alliance towerFaceB(@javax.annotation.Nullable String towerFaceB) {
    
    this.towerFaceB = towerFaceB;
    return this;
  }

  /**
   * Get towerFaceB
   * @return towerFaceB
   */
  @javax.annotation.Nullable

  public String getTowerFaceB() {
    return towerFaceB;
  }


  public void setTowerFaceB(@javax.annotation.Nullable String towerFaceB) {
    this.towerFaceB = towerFaceB;
  }

  public MatchScoreBreakdown2016Alliance towerFaceC(@javax.annotation.Nullable String towerFaceC) {
    
    this.towerFaceC = towerFaceC;
    return this;
  }

  /**
   * Get towerFaceC
   * @return towerFaceC
   */
  @javax.annotation.Nullable

  public String getTowerFaceC() {
    return towerFaceC;
  }


  public void setTowerFaceC(@javax.annotation.Nullable String towerFaceC) {
    this.towerFaceC = towerFaceC;
  }

  public MatchScoreBreakdown2016Alliance towerEndStrength(@javax.annotation.Nullable Integer towerEndStrength) {
    
    this.towerEndStrength = towerEndStrength;
    return this;
  }

  /**
   * Get towerEndStrength
   * @return towerEndStrength
   */
  @javax.annotation.Nullable

  public Integer getTowerEndStrength() {
    return towerEndStrength;
  }


  public void setTowerEndStrength(@javax.annotation.Nullable Integer towerEndStrength) {
    this.towerEndStrength = towerEndStrength;
  }

  public MatchScoreBreakdown2016Alliance techFoulCount(@javax.annotation.Nullable Integer techFoulCount) {
    
    this.techFoulCount = techFoulCount;
    return this;
  }

  /**
   * Get techFoulCount
   * @return techFoulCount
   */
  @javax.annotation.Nullable

  public Integer getTechFoulCount() {
    return techFoulCount;
  }


  public void setTechFoulCount(@javax.annotation.Nullable Integer techFoulCount) {
    this.techFoulCount = techFoulCount;
  }

  public MatchScoreBreakdown2016Alliance foulCount(@javax.annotation.Nullable Integer foulCount) {
    
    this.foulCount = foulCount;
    return this;
  }

  /**
   * Get foulCount
   * @return foulCount
   */
  @javax.annotation.Nullable

  public Integer getFoulCount() {
    return foulCount;
  }


  public void setFoulCount(@javax.annotation.Nullable Integer foulCount) {
    this.foulCount = foulCount;
  }

  public MatchScoreBreakdown2016Alliance position2(@javax.annotation.Nullable String position2) {
    
    this.position2 = position2;
    return this;
  }

  /**
   * Get position2
   * @return position2
   */
  @javax.annotation.Nullable

  public String getPosition2() {
    return position2;
  }


  public void setPosition2(@javax.annotation.Nullable String position2) {
    this.position2 = position2;
  }

  public MatchScoreBreakdown2016Alliance position3(@javax.annotation.Nullable String position3) {
    
    this.position3 = position3;
    return this;
  }

  /**
   * Get position3
   * @return position3
   */
  @javax.annotation.Nullable

  public String getPosition3() {
    return position3;
  }


  public void setPosition3(@javax.annotation.Nullable String position3) {
    this.position3 = position3;
  }

  public MatchScoreBreakdown2016Alliance position4(@javax.annotation.Nullable String position4) {
    
    this.position4 = position4;
    return this;
  }

  /**
   * Get position4
   * @return position4
   */
  @javax.annotation.Nullable

  public String getPosition4() {
    return position4;
  }


  public void setPosition4(@javax.annotation.Nullable String position4) {
    this.position4 = position4;
  }

  public MatchScoreBreakdown2016Alliance position5(@javax.annotation.Nullable String position5) {
    
    this.position5 = position5;
    return this;
  }

  /**
   * Get position5
   * @return position5
   */
  @javax.annotation.Nullable

  public String getPosition5() {
    return position5;
  }


  public void setPosition5(@javax.annotation.Nullable String position5) {
    this.position5 = position5;
  }

  public MatchScoreBreakdown2016Alliance position1crossings(@javax.annotation.Nullable Integer position1crossings) {
    
    this.position1crossings = position1crossings;
    return this;
  }

  /**
   * Get position1crossings
   * @return position1crossings
   */
  @javax.annotation.Nullable

  public Integer getPosition1crossings() {
    return position1crossings;
  }


  public void setPosition1crossings(@javax.annotation.Nullable Integer position1crossings) {
    this.position1crossings = position1crossings;
  }

  public MatchScoreBreakdown2016Alliance position2crossings(@javax.annotation.Nullable Integer position2crossings) {
    
    this.position2crossings = position2crossings;
    return this;
  }

  /**
   * Get position2crossings
   * @return position2crossings
   */
  @javax.annotation.Nullable

  public Integer getPosition2crossings() {
    return position2crossings;
  }


  public void setPosition2crossings(@javax.annotation.Nullable Integer position2crossings) {
    this.position2crossings = position2crossings;
  }

  public MatchScoreBreakdown2016Alliance position3crossings(@javax.annotation.Nullable Integer position3crossings) {
    
    this.position3crossings = position3crossings;
    return this;
  }

  /**
   * Get position3crossings
   * @return position3crossings
   */
  @javax.annotation.Nullable

  public Integer getPosition3crossings() {
    return position3crossings;
  }


  public void setPosition3crossings(@javax.annotation.Nullable Integer position3crossings) {
    this.position3crossings = position3crossings;
  }

  public MatchScoreBreakdown2016Alliance position4crossings(@javax.annotation.Nullable Integer position4crossings) {
    
    this.position4crossings = position4crossings;
    return this;
  }

  /**
   * Get position4crossings
   * @return position4crossings
   */
  @javax.annotation.Nullable

  public Integer getPosition4crossings() {
    return position4crossings;
  }


  public void setPosition4crossings(@javax.annotation.Nullable Integer position4crossings) {
    this.position4crossings = position4crossings;
  }

  public MatchScoreBreakdown2016Alliance position5crossings(@javax.annotation.Nullable Integer position5crossings) {
    
    this.position5crossings = position5crossings;
    return this;
  }

  /**
   * Get position5crossings
   * @return position5crossings
   */
  @javax.annotation.Nullable

  public Integer getPosition5crossings() {
    return position5crossings;
  }


  public void setPosition5crossings(@javax.annotation.Nullable Integer position5crossings) {
    this.position5crossings = position5crossings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MatchScoreBreakdown2016Alliance matchScoreBreakdown2016Alliance = (MatchScoreBreakdown2016Alliance) o;
    return Objects.equals(this.autoPoints, matchScoreBreakdown2016Alliance.autoPoints) &&
        Objects.equals(this.teleopPoints, matchScoreBreakdown2016Alliance.teleopPoints) &&
        Objects.equals(this.breachPoints, matchScoreBreakdown2016Alliance.breachPoints) &&
        Objects.equals(this.foulPoints, matchScoreBreakdown2016Alliance.foulPoints) &&
        Objects.equals(this.capturePoints, matchScoreBreakdown2016Alliance.capturePoints) &&
        Objects.equals(this.adjustPoints, matchScoreBreakdown2016Alliance.adjustPoints) &&
        Objects.equals(this.totalPoints, matchScoreBreakdown2016Alliance.totalPoints) &&
        Objects.equals(this.robot1Auto, matchScoreBreakdown2016Alliance.robot1Auto) &&
        Objects.equals(this.robot2Auto, matchScoreBreakdown2016Alliance.robot2Auto) &&
        Objects.equals(this.robot3Auto, matchScoreBreakdown2016Alliance.robot3Auto) &&
        Objects.equals(this.autoReachPoints, matchScoreBreakdown2016Alliance.autoReachPoints) &&
        Objects.equals(this.autoCrossingPoints, matchScoreBreakdown2016Alliance.autoCrossingPoints) &&
        Objects.equals(this.autoBouldersLow, matchScoreBreakdown2016Alliance.autoBouldersLow) &&
        Objects.equals(this.autoBouldersHigh, matchScoreBreakdown2016Alliance.autoBouldersHigh) &&
        Objects.equals(this.autoBoulderPoints, matchScoreBreakdown2016Alliance.autoBoulderPoints) &&
        Objects.equals(this.teleopCrossingPoints, matchScoreBreakdown2016Alliance.teleopCrossingPoints) &&
        Objects.equals(this.teleopBouldersLow, matchScoreBreakdown2016Alliance.teleopBouldersLow) &&
        Objects.equals(this.teleopBouldersHigh, matchScoreBreakdown2016Alliance.teleopBouldersHigh) &&
        Objects.equals(this.teleopBoulderPoints, matchScoreBreakdown2016Alliance.teleopBoulderPoints) &&
        Objects.equals(this.teleopDefensesBreached, matchScoreBreakdown2016Alliance.teleopDefensesBreached) &&
        Objects.equals(this.teleopChallengePoints, matchScoreBreakdown2016Alliance.teleopChallengePoints) &&
        Objects.equals(this.teleopScalePoints, matchScoreBreakdown2016Alliance.teleopScalePoints) &&
        Objects.equals(this.teleopTowerCaptured, matchScoreBreakdown2016Alliance.teleopTowerCaptured) &&
        Objects.equals(this.towerFaceA, matchScoreBreakdown2016Alliance.towerFaceA) &&
        Objects.equals(this.towerFaceB, matchScoreBreakdown2016Alliance.towerFaceB) &&
        Objects.equals(this.towerFaceC, matchScoreBreakdown2016Alliance.towerFaceC) &&
        Objects.equals(this.towerEndStrength, matchScoreBreakdown2016Alliance.towerEndStrength) &&
        Objects.equals(this.techFoulCount, matchScoreBreakdown2016Alliance.techFoulCount) &&
        Objects.equals(this.foulCount, matchScoreBreakdown2016Alliance.foulCount) &&
        Objects.equals(this.position2, matchScoreBreakdown2016Alliance.position2) &&
        Objects.equals(this.position3, matchScoreBreakdown2016Alliance.position3) &&
        Objects.equals(this.position4, matchScoreBreakdown2016Alliance.position4) &&
        Objects.equals(this.position5, matchScoreBreakdown2016Alliance.position5) &&
        Objects.equals(this.position1crossings, matchScoreBreakdown2016Alliance.position1crossings) &&
        Objects.equals(this.position2crossings, matchScoreBreakdown2016Alliance.position2crossings) &&
        Objects.equals(this.position3crossings, matchScoreBreakdown2016Alliance.position3crossings) &&
        Objects.equals(this.position4crossings, matchScoreBreakdown2016Alliance.position4crossings) &&
        Objects.equals(this.position5crossings, matchScoreBreakdown2016Alliance.position5crossings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(autoPoints, teleopPoints, breachPoints, foulPoints, capturePoints, adjustPoints, totalPoints, robot1Auto, robot2Auto, robot3Auto, autoReachPoints, autoCrossingPoints, autoBouldersLow, autoBouldersHigh, autoBoulderPoints, teleopCrossingPoints, teleopBouldersLow, teleopBouldersHigh, teleopBoulderPoints, teleopDefensesBreached, teleopChallengePoints, teleopScalePoints, teleopTowerCaptured, towerFaceA, towerFaceB, towerFaceC, towerEndStrength, techFoulCount, foulCount, position2, position3, position4, position5, position1crossings, position2crossings, position3crossings, position4crossings, position5crossings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MatchScoreBreakdown2016Alliance {\n");
    sb.append("    autoPoints: ").append(toIndentedString(autoPoints)).append("\n");
    sb.append("    teleopPoints: ").append(toIndentedString(teleopPoints)).append("\n");
    sb.append("    breachPoints: ").append(toIndentedString(breachPoints)).append("\n");
    sb.append("    foulPoints: ").append(toIndentedString(foulPoints)).append("\n");
    sb.append("    capturePoints: ").append(toIndentedString(capturePoints)).append("\n");
    sb.append("    adjustPoints: ").append(toIndentedString(adjustPoints)).append("\n");
    sb.append("    totalPoints: ").append(toIndentedString(totalPoints)).append("\n");
    sb.append("    robot1Auto: ").append(toIndentedString(robot1Auto)).append("\n");
    sb.append("    robot2Auto: ").append(toIndentedString(robot2Auto)).append("\n");
    sb.append("    robot3Auto: ").append(toIndentedString(robot3Auto)).append("\n");
    sb.append("    autoReachPoints: ").append(toIndentedString(autoReachPoints)).append("\n");
    sb.append("    autoCrossingPoints: ").append(toIndentedString(autoCrossingPoints)).append("\n");
    sb.append("    autoBouldersLow: ").append(toIndentedString(autoBouldersLow)).append("\n");
    sb.append("    autoBouldersHigh: ").append(toIndentedString(autoBouldersHigh)).append("\n");
    sb.append("    autoBoulderPoints: ").append(toIndentedString(autoBoulderPoints)).append("\n");
    sb.append("    teleopCrossingPoints: ").append(toIndentedString(teleopCrossingPoints)).append("\n");
    sb.append("    teleopBouldersLow: ").append(toIndentedString(teleopBouldersLow)).append("\n");
    sb.append("    teleopBouldersHigh: ").append(toIndentedString(teleopBouldersHigh)).append("\n");
    sb.append("    teleopBoulderPoints: ").append(toIndentedString(teleopBoulderPoints)).append("\n");
    sb.append("    teleopDefensesBreached: ").append(toIndentedString(teleopDefensesBreached)).append("\n");
    sb.append("    teleopChallengePoints: ").append(toIndentedString(teleopChallengePoints)).append("\n");
    sb.append("    teleopScalePoints: ").append(toIndentedString(teleopScalePoints)).append("\n");
    sb.append("    teleopTowerCaptured: ").append(toIndentedString(teleopTowerCaptured)).append("\n");
    sb.append("    towerFaceA: ").append(toIndentedString(towerFaceA)).append("\n");
    sb.append("    towerFaceB: ").append(toIndentedString(towerFaceB)).append("\n");
    sb.append("    towerFaceC: ").append(toIndentedString(towerFaceC)).append("\n");
    sb.append("    towerEndStrength: ").append(toIndentedString(towerEndStrength)).append("\n");
    sb.append("    techFoulCount: ").append(toIndentedString(techFoulCount)).append("\n");
    sb.append("    foulCount: ").append(toIndentedString(foulCount)).append("\n");
    sb.append("    position2: ").append(toIndentedString(position2)).append("\n");
    sb.append("    position3: ").append(toIndentedString(position3)).append("\n");
    sb.append("    position4: ").append(toIndentedString(position4)).append("\n");
    sb.append("    position5: ").append(toIndentedString(position5)).append("\n");
    sb.append("    position1crossings: ").append(toIndentedString(position1crossings)).append("\n");
    sb.append("    position2crossings: ").append(toIndentedString(position2crossings)).append("\n");
    sb.append("    position3crossings: ").append(toIndentedString(position3crossings)).append("\n");
    sb.append("    position4crossings: ").append(toIndentedString(position4crossings)).append("\n");
    sb.append("    position5crossings: ").append(toIndentedString(position5crossings)).append("\n");
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

