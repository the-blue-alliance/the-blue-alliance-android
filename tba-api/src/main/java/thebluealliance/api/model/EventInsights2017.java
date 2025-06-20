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
import java.util.List;

/**
 * Insights for FIRST STEAMWORKS qualification and elimination matches.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-06-19T09:18:44.421555100-05:00[America/Chicago]", comments = "Generator version: 7.13.0")
public class EventInsights2017 {
  public static final String SERIALIZED_NAME_AVERAGE_FOUL_SCORE = "average_foul_score";
  @SerializedName(SERIALIZED_NAME_AVERAGE_FOUL_SCORE)
  @javax.annotation.Nonnull
  private Float averageFoulScore;

  public static final String SERIALIZED_NAME_AVERAGE_FUEL_POINTS = "average_fuel_points";
  @SerializedName(SERIALIZED_NAME_AVERAGE_FUEL_POINTS)
  @javax.annotation.Nonnull
  private Float averageFuelPoints;

  public static final String SERIALIZED_NAME_AVERAGE_FUEL_POINTS_AUTO = "average_fuel_points_auto";
  @SerializedName(SERIALIZED_NAME_AVERAGE_FUEL_POINTS_AUTO)
  @javax.annotation.Nonnull
  private Float averageFuelPointsAuto;

  public static final String SERIALIZED_NAME_AVERAGE_FUEL_POINTS_TELEOP = "average_fuel_points_teleop";
  @SerializedName(SERIALIZED_NAME_AVERAGE_FUEL_POINTS_TELEOP)
  @javax.annotation.Nonnull
  private Float averageFuelPointsTeleop;

  public static final String SERIALIZED_NAME_AVERAGE_HIGH_GOALS = "average_high_goals";
  @SerializedName(SERIALIZED_NAME_AVERAGE_HIGH_GOALS)
  @javax.annotation.Nonnull
  private Float averageHighGoals;

  public static final String SERIALIZED_NAME_AVERAGE_HIGH_GOALS_AUTO = "average_high_goals_auto";
  @SerializedName(SERIALIZED_NAME_AVERAGE_HIGH_GOALS_AUTO)
  @javax.annotation.Nonnull
  private Float averageHighGoalsAuto;

  public static final String SERIALIZED_NAME_AVERAGE_HIGH_GOALS_TELEOP = "average_high_goals_teleop";
  @SerializedName(SERIALIZED_NAME_AVERAGE_HIGH_GOALS_TELEOP)
  @javax.annotation.Nonnull
  private Float averageHighGoalsTeleop;

  public static final String SERIALIZED_NAME_AVERAGE_LOW_GOALS = "average_low_goals";
  @SerializedName(SERIALIZED_NAME_AVERAGE_LOW_GOALS)
  @javax.annotation.Nonnull
  private Float averageLowGoals;

  public static final String SERIALIZED_NAME_AVERAGE_LOW_GOALS_AUTO = "average_low_goals_auto";
  @SerializedName(SERIALIZED_NAME_AVERAGE_LOW_GOALS_AUTO)
  @javax.annotation.Nonnull
  private Float averageLowGoalsAuto;

  public static final String SERIALIZED_NAME_AVERAGE_LOW_GOALS_TELEOP = "average_low_goals_teleop";
  @SerializedName(SERIALIZED_NAME_AVERAGE_LOW_GOALS_TELEOP)
  @javax.annotation.Nonnull
  private Float averageLowGoalsTeleop;

  public static final String SERIALIZED_NAME_AVERAGE_MOBILITY_POINTS_AUTO = "average_mobility_points_auto";
  @SerializedName(SERIALIZED_NAME_AVERAGE_MOBILITY_POINTS_AUTO)
  @javax.annotation.Nonnull
  private Float averageMobilityPointsAuto;

  public static final String SERIALIZED_NAME_AVERAGE_POINTS_AUTO = "average_points_auto";
  @SerializedName(SERIALIZED_NAME_AVERAGE_POINTS_AUTO)
  @javax.annotation.Nonnull
  private Float averagePointsAuto;

  public static final String SERIALIZED_NAME_AVERAGE_POINTS_TELEOP = "average_points_teleop";
  @SerializedName(SERIALIZED_NAME_AVERAGE_POINTS_TELEOP)
  @javax.annotation.Nonnull
  private Float averagePointsTeleop;

  public static final String SERIALIZED_NAME_AVERAGE_ROTOR_POINTS = "average_rotor_points";
  @SerializedName(SERIALIZED_NAME_AVERAGE_ROTOR_POINTS)
  @javax.annotation.Nonnull
  private Float averageRotorPoints;

  public static final String SERIALIZED_NAME_AVERAGE_ROTOR_POINTS_AUTO = "average_rotor_points_auto";
  @SerializedName(SERIALIZED_NAME_AVERAGE_ROTOR_POINTS_AUTO)
  @javax.annotation.Nonnull
  private Float averageRotorPointsAuto;

  public static final String SERIALIZED_NAME_AVERAGE_ROTOR_POINTS_TELEOP = "average_rotor_points_teleop";
  @SerializedName(SERIALIZED_NAME_AVERAGE_ROTOR_POINTS_TELEOP)
  @javax.annotation.Nonnull
  private Float averageRotorPointsTeleop;

  public static final String SERIALIZED_NAME_AVERAGE_SCORE = "average_score";
  @SerializedName(SERIALIZED_NAME_AVERAGE_SCORE)
  @javax.annotation.Nonnull
  private Float averageScore;

  public static final String SERIALIZED_NAME_AVERAGE_TAKEOFF_POINTS_TELEOP = "average_takeoff_points_teleop";
  @SerializedName(SERIALIZED_NAME_AVERAGE_TAKEOFF_POINTS_TELEOP)
  @javax.annotation.Nonnull
  private Float averageTakeoffPointsTeleop;

  public static final String SERIALIZED_NAME_AVERAGE_WIN_MARGIN = "average_win_margin";
  @SerializedName(SERIALIZED_NAME_AVERAGE_WIN_MARGIN)
  @javax.annotation.Nonnull
  private Float averageWinMargin;

  public static final String SERIALIZED_NAME_AVERAGE_WIN_SCORE = "average_win_score";
  @SerializedName(SERIALIZED_NAME_AVERAGE_WIN_SCORE)
  @javax.annotation.Nonnull
  private Float averageWinScore;

  public static final String SERIALIZED_NAME_HIGH_KPA = "high_kpa";
  @SerializedName(SERIALIZED_NAME_HIGH_KPA)
  @javax.annotation.Nonnull
  private List<String> highKpa = new ArrayList<>();

  public static final String SERIALIZED_NAME_HIGH_SCORE = "high_score";
  @SerializedName(SERIALIZED_NAME_HIGH_SCORE)
  @javax.annotation.Nonnull
  private List<String> highScore = new ArrayList<>();

  public static final String SERIALIZED_NAME_KPA_ACHIEVED = "kpa_achieved";
  @SerializedName(SERIALIZED_NAME_KPA_ACHIEVED)
  @javax.annotation.Nonnull
  private List<Float> kpaAchieved = new ArrayList<>();

  public static final String SERIALIZED_NAME_MOBILITY_COUNTS = "mobility_counts";
  @SerializedName(SERIALIZED_NAME_MOBILITY_COUNTS)
  @javax.annotation.Nonnull
  private List<Float> mobilityCounts = new ArrayList<>();

  public static final String SERIALIZED_NAME_ROTOR1_ENGAGED = "rotor_1_engaged";
  @SerializedName(SERIALIZED_NAME_ROTOR1_ENGAGED)
  @javax.annotation.Nonnull
  private List<Float> rotor1Engaged = new ArrayList<>();

  public static final String SERIALIZED_NAME_ROTOR1_ENGAGED_AUTO = "rotor_1_engaged_auto";
  @SerializedName(SERIALIZED_NAME_ROTOR1_ENGAGED_AUTO)
  @javax.annotation.Nonnull
  private List<Float> rotor1EngagedAuto = new ArrayList<>();

  public static final String SERIALIZED_NAME_ROTOR2_ENGAGED = "rotor_2_engaged";
  @SerializedName(SERIALIZED_NAME_ROTOR2_ENGAGED)
  @javax.annotation.Nonnull
  private List<Float> rotor2Engaged = new ArrayList<>();

  public static final String SERIALIZED_NAME_ROTOR2_ENGAGED_AUTO = "rotor_2_engaged_auto";
  @SerializedName(SERIALIZED_NAME_ROTOR2_ENGAGED_AUTO)
  @javax.annotation.Nonnull
  private List<Float> rotor2EngagedAuto = new ArrayList<>();

  public static final String SERIALIZED_NAME_ROTOR3_ENGAGED = "rotor_3_engaged";
  @SerializedName(SERIALIZED_NAME_ROTOR3_ENGAGED)
  @javax.annotation.Nonnull
  private List<Float> rotor3Engaged = new ArrayList<>();

  public static final String SERIALIZED_NAME_ROTOR4_ENGAGED = "rotor_4_engaged";
  @SerializedName(SERIALIZED_NAME_ROTOR4_ENGAGED)
  @javax.annotation.Nonnull
  private List<Float> rotor4Engaged = new ArrayList<>();

  public static final String SERIALIZED_NAME_TAKEOFF_COUNTS = "takeoff_counts";
  @SerializedName(SERIALIZED_NAME_TAKEOFF_COUNTS)
  @javax.annotation.Nonnull
  private List<Float> takeoffCounts = new ArrayList<>();

  public static final String SERIALIZED_NAME_UNICORN_MATCHES = "unicorn_matches";
  @SerializedName(SERIALIZED_NAME_UNICORN_MATCHES)
  @javax.annotation.Nonnull
  private List<Float> unicornMatches = new ArrayList<>();

  public EventInsights2017() {
  }

  public EventInsights2017 averageFoulScore(@javax.annotation.Nonnull Float averageFoulScore) {
    
    this.averageFoulScore = averageFoulScore;
    return this;
  }

  /**
   * Average foul score.
   * @return averageFoulScore
   */
  @javax.annotation.Nonnull

  public Float getAverageFoulScore() {
    return averageFoulScore;
  }


  public void setAverageFoulScore(@javax.annotation.Nonnull Float averageFoulScore) {
    this.averageFoulScore = averageFoulScore;
  }

  public EventInsights2017 averageFuelPoints(@javax.annotation.Nonnull Float averageFuelPoints) {
    
    this.averageFuelPoints = averageFuelPoints;
    return this;
  }

  /**
   * Average fuel points scored.
   * @return averageFuelPoints
   */
  @javax.annotation.Nonnull

  public Float getAverageFuelPoints() {
    return averageFuelPoints;
  }


  public void setAverageFuelPoints(@javax.annotation.Nonnull Float averageFuelPoints) {
    this.averageFuelPoints = averageFuelPoints;
  }

  public EventInsights2017 averageFuelPointsAuto(@javax.annotation.Nonnull Float averageFuelPointsAuto) {
    
    this.averageFuelPointsAuto = averageFuelPointsAuto;
    return this;
  }

  /**
   * Average fuel points scored during auto.
   * @return averageFuelPointsAuto
   */
  @javax.annotation.Nonnull

  public Float getAverageFuelPointsAuto() {
    return averageFuelPointsAuto;
  }


  public void setAverageFuelPointsAuto(@javax.annotation.Nonnull Float averageFuelPointsAuto) {
    this.averageFuelPointsAuto = averageFuelPointsAuto;
  }

  public EventInsights2017 averageFuelPointsTeleop(@javax.annotation.Nonnull Float averageFuelPointsTeleop) {
    
    this.averageFuelPointsTeleop = averageFuelPointsTeleop;
    return this;
  }

  /**
   * Average fuel points scored during teleop.
   * @return averageFuelPointsTeleop
   */
  @javax.annotation.Nonnull

  public Float getAverageFuelPointsTeleop() {
    return averageFuelPointsTeleop;
  }


  public void setAverageFuelPointsTeleop(@javax.annotation.Nonnull Float averageFuelPointsTeleop) {
    this.averageFuelPointsTeleop = averageFuelPointsTeleop;
  }

  public EventInsights2017 averageHighGoals(@javax.annotation.Nonnull Float averageHighGoals) {
    
    this.averageHighGoals = averageHighGoals;
    return this;
  }

  /**
   * Average points scored in the high goal.
   * @return averageHighGoals
   */
  @javax.annotation.Nonnull

  public Float getAverageHighGoals() {
    return averageHighGoals;
  }


  public void setAverageHighGoals(@javax.annotation.Nonnull Float averageHighGoals) {
    this.averageHighGoals = averageHighGoals;
  }

  public EventInsights2017 averageHighGoalsAuto(@javax.annotation.Nonnull Float averageHighGoalsAuto) {
    
    this.averageHighGoalsAuto = averageHighGoalsAuto;
    return this;
  }

  /**
   * Average points scored in the high goal during auto.
   * @return averageHighGoalsAuto
   */
  @javax.annotation.Nonnull

  public Float getAverageHighGoalsAuto() {
    return averageHighGoalsAuto;
  }


  public void setAverageHighGoalsAuto(@javax.annotation.Nonnull Float averageHighGoalsAuto) {
    this.averageHighGoalsAuto = averageHighGoalsAuto;
  }

  public EventInsights2017 averageHighGoalsTeleop(@javax.annotation.Nonnull Float averageHighGoalsTeleop) {
    
    this.averageHighGoalsTeleop = averageHighGoalsTeleop;
    return this;
  }

  /**
   * Average points scored in the high goal during teleop.
   * @return averageHighGoalsTeleop
   */
  @javax.annotation.Nonnull

  public Float getAverageHighGoalsTeleop() {
    return averageHighGoalsTeleop;
  }


  public void setAverageHighGoalsTeleop(@javax.annotation.Nonnull Float averageHighGoalsTeleop) {
    this.averageHighGoalsTeleop = averageHighGoalsTeleop;
  }

  public EventInsights2017 averageLowGoals(@javax.annotation.Nonnull Float averageLowGoals) {
    
    this.averageLowGoals = averageLowGoals;
    return this;
  }

  /**
   * Average points scored in the low goal.
   * @return averageLowGoals
   */
  @javax.annotation.Nonnull

  public Float getAverageLowGoals() {
    return averageLowGoals;
  }


  public void setAverageLowGoals(@javax.annotation.Nonnull Float averageLowGoals) {
    this.averageLowGoals = averageLowGoals;
  }

  public EventInsights2017 averageLowGoalsAuto(@javax.annotation.Nonnull Float averageLowGoalsAuto) {
    
    this.averageLowGoalsAuto = averageLowGoalsAuto;
    return this;
  }

  /**
   * Average points scored in the low goal during auto.
   * @return averageLowGoalsAuto
   */
  @javax.annotation.Nonnull

  public Float getAverageLowGoalsAuto() {
    return averageLowGoalsAuto;
  }


  public void setAverageLowGoalsAuto(@javax.annotation.Nonnull Float averageLowGoalsAuto) {
    this.averageLowGoalsAuto = averageLowGoalsAuto;
  }

  public EventInsights2017 averageLowGoalsTeleop(@javax.annotation.Nonnull Float averageLowGoalsTeleop) {
    
    this.averageLowGoalsTeleop = averageLowGoalsTeleop;
    return this;
  }

  /**
   * Average points scored in the low goal during teleop.
   * @return averageLowGoalsTeleop
   */
  @javax.annotation.Nonnull

  public Float getAverageLowGoalsTeleop() {
    return averageLowGoalsTeleop;
  }


  public void setAverageLowGoalsTeleop(@javax.annotation.Nonnull Float averageLowGoalsTeleop) {
    this.averageLowGoalsTeleop = averageLowGoalsTeleop;
  }

  public EventInsights2017 averageMobilityPointsAuto(@javax.annotation.Nonnull Float averageMobilityPointsAuto) {
    
    this.averageMobilityPointsAuto = averageMobilityPointsAuto;
    return this;
  }

  /**
   * Average mobility points scored during auto.
   * @return averageMobilityPointsAuto
   */
  @javax.annotation.Nonnull

  public Float getAverageMobilityPointsAuto() {
    return averageMobilityPointsAuto;
  }


  public void setAverageMobilityPointsAuto(@javax.annotation.Nonnull Float averageMobilityPointsAuto) {
    this.averageMobilityPointsAuto = averageMobilityPointsAuto;
  }

  public EventInsights2017 averagePointsAuto(@javax.annotation.Nonnull Float averagePointsAuto) {
    
    this.averagePointsAuto = averagePointsAuto;
    return this;
  }

  /**
   * Average points scored during auto.
   * @return averagePointsAuto
   */
  @javax.annotation.Nonnull

  public Float getAveragePointsAuto() {
    return averagePointsAuto;
  }


  public void setAveragePointsAuto(@javax.annotation.Nonnull Float averagePointsAuto) {
    this.averagePointsAuto = averagePointsAuto;
  }

  public EventInsights2017 averagePointsTeleop(@javax.annotation.Nonnull Float averagePointsTeleop) {
    
    this.averagePointsTeleop = averagePointsTeleop;
    return this;
  }

  /**
   * Average points scored during teleop.
   * @return averagePointsTeleop
   */
  @javax.annotation.Nonnull

  public Float getAveragePointsTeleop() {
    return averagePointsTeleop;
  }


  public void setAveragePointsTeleop(@javax.annotation.Nonnull Float averagePointsTeleop) {
    this.averagePointsTeleop = averagePointsTeleop;
  }

  public EventInsights2017 averageRotorPoints(@javax.annotation.Nonnull Float averageRotorPoints) {
    
    this.averageRotorPoints = averageRotorPoints;
    return this;
  }

  /**
   * Average rotor points scored.
   * @return averageRotorPoints
   */
  @javax.annotation.Nonnull

  public Float getAverageRotorPoints() {
    return averageRotorPoints;
  }


  public void setAverageRotorPoints(@javax.annotation.Nonnull Float averageRotorPoints) {
    this.averageRotorPoints = averageRotorPoints;
  }

  public EventInsights2017 averageRotorPointsAuto(@javax.annotation.Nonnull Float averageRotorPointsAuto) {
    
    this.averageRotorPointsAuto = averageRotorPointsAuto;
    return this;
  }

  /**
   * Average rotor points scored during auto.
   * @return averageRotorPointsAuto
   */
  @javax.annotation.Nonnull

  public Float getAverageRotorPointsAuto() {
    return averageRotorPointsAuto;
  }


  public void setAverageRotorPointsAuto(@javax.annotation.Nonnull Float averageRotorPointsAuto) {
    this.averageRotorPointsAuto = averageRotorPointsAuto;
  }

  public EventInsights2017 averageRotorPointsTeleop(@javax.annotation.Nonnull Float averageRotorPointsTeleop) {
    
    this.averageRotorPointsTeleop = averageRotorPointsTeleop;
    return this;
  }

  /**
   * Average rotor points scored during teleop.
   * @return averageRotorPointsTeleop
   */
  @javax.annotation.Nonnull

  public Float getAverageRotorPointsTeleop() {
    return averageRotorPointsTeleop;
  }


  public void setAverageRotorPointsTeleop(@javax.annotation.Nonnull Float averageRotorPointsTeleop) {
    this.averageRotorPointsTeleop = averageRotorPointsTeleop;
  }

  public EventInsights2017 averageScore(@javax.annotation.Nonnull Float averageScore) {
    
    this.averageScore = averageScore;
    return this;
  }

  /**
   * Average score.
   * @return averageScore
   */
  @javax.annotation.Nonnull

  public Float getAverageScore() {
    return averageScore;
  }


  public void setAverageScore(@javax.annotation.Nonnull Float averageScore) {
    this.averageScore = averageScore;
  }

  public EventInsights2017 averageTakeoffPointsTeleop(@javax.annotation.Nonnull Float averageTakeoffPointsTeleop) {
    
    this.averageTakeoffPointsTeleop = averageTakeoffPointsTeleop;
    return this;
  }

  /**
   * Average takeoff points scored during teleop.
   * @return averageTakeoffPointsTeleop
   */
  @javax.annotation.Nonnull

  public Float getAverageTakeoffPointsTeleop() {
    return averageTakeoffPointsTeleop;
  }


  public void setAverageTakeoffPointsTeleop(@javax.annotation.Nonnull Float averageTakeoffPointsTeleop) {
    this.averageTakeoffPointsTeleop = averageTakeoffPointsTeleop;
  }

  public EventInsights2017 averageWinMargin(@javax.annotation.Nonnull Float averageWinMargin) {
    
    this.averageWinMargin = averageWinMargin;
    return this;
  }

  /**
   * Average margin of victory.
   * @return averageWinMargin
   */
  @javax.annotation.Nonnull

  public Float getAverageWinMargin() {
    return averageWinMargin;
  }


  public void setAverageWinMargin(@javax.annotation.Nonnull Float averageWinMargin) {
    this.averageWinMargin = averageWinMargin;
  }

  public EventInsights2017 averageWinScore(@javax.annotation.Nonnull Float averageWinScore) {
    
    this.averageWinScore = averageWinScore;
    return this;
  }

  /**
   * Average winning score.
   * @return averageWinScore
   */
  @javax.annotation.Nonnull

  public Float getAverageWinScore() {
    return averageWinScore;
  }


  public void setAverageWinScore(@javax.annotation.Nonnull Float averageWinScore) {
    this.averageWinScore = averageWinScore;
  }

  public EventInsights2017 highKpa(@javax.annotation.Nonnull List<String> highKpa) {
    
    this.highKpa = highKpa;
    return this;
  }

  public EventInsights2017 addHighKpaItem(String highKpaItem) {
    if (this.highKpa == null) {
      this.highKpa = new ArrayList<>();
    }
    this.highKpa.add(highKpaItem);
    return this;
  }

  /**
   * An array with three values, kPa scored, match key from the match with the high kPa, and the name of the match
   * @return highKpa
   */
  @javax.annotation.Nonnull

  public List<String> getHighKpa() {
    return highKpa;
  }


  public void setHighKpa(@javax.annotation.Nonnull List<String> highKpa) {
    this.highKpa = highKpa;
  }

  public EventInsights2017 highScore(@javax.annotation.Nonnull List<String> highScore) {
    
    this.highScore = highScore;
    return this;
  }

  public EventInsights2017 addHighScoreItem(String highScoreItem) {
    if (this.highScore == null) {
      this.highScore = new ArrayList<>();
    }
    this.highScore.add(highScoreItem);
    return this;
  }

  /**
   * An array with three values, high score, match key from the match with the high score, and the name of the match
   * @return highScore
   */
  @javax.annotation.Nonnull

  public List<String> getHighScore() {
    return highScore;
  }


  public void setHighScore(@javax.annotation.Nonnull List<String> highScore) {
    this.highScore = highScore;
  }

  public EventInsights2017 kpaAchieved(@javax.annotation.Nonnull List<Float> kpaAchieved) {
    
    this.kpaAchieved = kpaAchieved;
    return this;
  }

  public EventInsights2017 addKpaAchievedItem(Float kpaAchievedItem) {
    if (this.kpaAchieved == null) {
      this.kpaAchieved = new ArrayList<>();
    }
    this.kpaAchieved.add(kpaAchievedItem);
    return this;
  }

  /**
   * An array with three values, number of times kPa bonus achieved, number of opportunities to bonus, and percentage.
   * @return kpaAchieved
   */
  @javax.annotation.Nonnull

  public List<Float> getKpaAchieved() {
    return kpaAchieved;
  }


  public void setKpaAchieved(@javax.annotation.Nonnull List<Float> kpaAchieved) {
    this.kpaAchieved = kpaAchieved;
  }

  public EventInsights2017 mobilityCounts(@javax.annotation.Nonnull List<Float> mobilityCounts) {
    
    this.mobilityCounts = mobilityCounts;
    return this;
  }

  public EventInsights2017 addMobilityCountsItem(Float mobilityCountsItem) {
    if (this.mobilityCounts == null) {
      this.mobilityCounts = new ArrayList<>();
    }
    this.mobilityCounts.add(mobilityCountsItem);
    return this;
  }

  /**
   * An array with three values, number of times mobility bonus achieved, number of opportunities to bonus, and percentage.
   * @return mobilityCounts
   */
  @javax.annotation.Nonnull

  public List<Float> getMobilityCounts() {
    return mobilityCounts;
  }


  public void setMobilityCounts(@javax.annotation.Nonnull List<Float> mobilityCounts) {
    this.mobilityCounts = mobilityCounts;
  }

  public EventInsights2017 rotor1Engaged(@javax.annotation.Nonnull List<Float> rotor1Engaged) {
    
    this.rotor1Engaged = rotor1Engaged;
    return this;
  }

  public EventInsights2017 addRotor1EngagedItem(Float rotor1EngagedItem) {
    if (this.rotor1Engaged == null) {
      this.rotor1Engaged = new ArrayList<>();
    }
    this.rotor1Engaged.add(rotor1EngagedItem);
    return this;
  }

  /**
   * An array with three values, number of times rotor 1 engaged, number of opportunities to engage, and percentage.
   * @return rotor1Engaged
   */
  @javax.annotation.Nonnull

  public List<Float> getRotor1Engaged() {
    return rotor1Engaged;
  }


  public void setRotor1Engaged(@javax.annotation.Nonnull List<Float> rotor1Engaged) {
    this.rotor1Engaged = rotor1Engaged;
  }

  public EventInsights2017 rotor1EngagedAuto(@javax.annotation.Nonnull List<Float> rotor1EngagedAuto) {
    
    this.rotor1EngagedAuto = rotor1EngagedAuto;
    return this;
  }

  public EventInsights2017 addRotor1EngagedAutoItem(Float rotor1EngagedAutoItem) {
    if (this.rotor1EngagedAuto == null) {
      this.rotor1EngagedAuto = new ArrayList<>();
    }
    this.rotor1EngagedAuto.add(rotor1EngagedAutoItem);
    return this;
  }

  /**
   * An array with three values, number of times rotor 1 engaged in auto, number of opportunities to engage in auto, and percentage.
   * @return rotor1EngagedAuto
   */
  @javax.annotation.Nonnull

  public List<Float> getRotor1EngagedAuto() {
    return rotor1EngagedAuto;
  }


  public void setRotor1EngagedAuto(@javax.annotation.Nonnull List<Float> rotor1EngagedAuto) {
    this.rotor1EngagedAuto = rotor1EngagedAuto;
  }

  public EventInsights2017 rotor2Engaged(@javax.annotation.Nonnull List<Float> rotor2Engaged) {
    
    this.rotor2Engaged = rotor2Engaged;
    return this;
  }

  public EventInsights2017 addRotor2EngagedItem(Float rotor2EngagedItem) {
    if (this.rotor2Engaged == null) {
      this.rotor2Engaged = new ArrayList<>();
    }
    this.rotor2Engaged.add(rotor2EngagedItem);
    return this;
  }

  /**
   * An array with three values, number of times rotor 2 engaged, number of opportunities to engage, and percentage.
   * @return rotor2Engaged
   */
  @javax.annotation.Nonnull

  public List<Float> getRotor2Engaged() {
    return rotor2Engaged;
  }


  public void setRotor2Engaged(@javax.annotation.Nonnull List<Float> rotor2Engaged) {
    this.rotor2Engaged = rotor2Engaged;
  }

  public EventInsights2017 rotor2EngagedAuto(@javax.annotation.Nonnull List<Float> rotor2EngagedAuto) {
    
    this.rotor2EngagedAuto = rotor2EngagedAuto;
    return this;
  }

  public EventInsights2017 addRotor2EngagedAutoItem(Float rotor2EngagedAutoItem) {
    if (this.rotor2EngagedAuto == null) {
      this.rotor2EngagedAuto = new ArrayList<>();
    }
    this.rotor2EngagedAuto.add(rotor2EngagedAutoItem);
    return this;
  }

  /**
   * An array with three values, number of times rotor 2 engaged in auto, number of opportunities to engage in auto, and percentage.
   * @return rotor2EngagedAuto
   */
  @javax.annotation.Nonnull

  public List<Float> getRotor2EngagedAuto() {
    return rotor2EngagedAuto;
  }


  public void setRotor2EngagedAuto(@javax.annotation.Nonnull List<Float> rotor2EngagedAuto) {
    this.rotor2EngagedAuto = rotor2EngagedAuto;
  }

  public EventInsights2017 rotor3Engaged(@javax.annotation.Nonnull List<Float> rotor3Engaged) {
    
    this.rotor3Engaged = rotor3Engaged;
    return this;
  }

  public EventInsights2017 addRotor3EngagedItem(Float rotor3EngagedItem) {
    if (this.rotor3Engaged == null) {
      this.rotor3Engaged = new ArrayList<>();
    }
    this.rotor3Engaged.add(rotor3EngagedItem);
    return this;
  }

  /**
   * An array with three values, number of times rotor 3 engaged, number of opportunities to engage, and percentage.
   * @return rotor3Engaged
   */
  @javax.annotation.Nonnull

  public List<Float> getRotor3Engaged() {
    return rotor3Engaged;
  }


  public void setRotor3Engaged(@javax.annotation.Nonnull List<Float> rotor3Engaged) {
    this.rotor3Engaged = rotor3Engaged;
  }

  public EventInsights2017 rotor4Engaged(@javax.annotation.Nonnull List<Float> rotor4Engaged) {
    
    this.rotor4Engaged = rotor4Engaged;
    return this;
  }

  public EventInsights2017 addRotor4EngagedItem(Float rotor4EngagedItem) {
    if (this.rotor4Engaged == null) {
      this.rotor4Engaged = new ArrayList<>();
    }
    this.rotor4Engaged.add(rotor4EngagedItem);
    return this;
  }

  /**
   * An array with three values, number of times rotor 4 engaged, number of opportunities to engage, and percentage.
   * @return rotor4Engaged
   */
  @javax.annotation.Nonnull

  public List<Float> getRotor4Engaged() {
    return rotor4Engaged;
  }


  public void setRotor4Engaged(@javax.annotation.Nonnull List<Float> rotor4Engaged) {
    this.rotor4Engaged = rotor4Engaged;
  }

  public EventInsights2017 takeoffCounts(@javax.annotation.Nonnull List<Float> takeoffCounts) {
    
    this.takeoffCounts = takeoffCounts;
    return this;
  }

  public EventInsights2017 addTakeoffCountsItem(Float takeoffCountsItem) {
    if (this.takeoffCounts == null) {
      this.takeoffCounts = new ArrayList<>();
    }
    this.takeoffCounts.add(takeoffCountsItem);
    return this;
  }

  /**
   * An array with three values, number of times takeoff was counted, number of opportunities to takeoff, and percentage.
   * @return takeoffCounts
   */
  @javax.annotation.Nonnull

  public List<Float> getTakeoffCounts() {
    return takeoffCounts;
  }


  public void setTakeoffCounts(@javax.annotation.Nonnull List<Float> takeoffCounts) {
    this.takeoffCounts = takeoffCounts;
  }

  public EventInsights2017 unicornMatches(@javax.annotation.Nonnull List<Float> unicornMatches) {
    
    this.unicornMatches = unicornMatches;
    return this;
  }

  public EventInsights2017 addUnicornMatchesItem(Float unicornMatchesItem) {
    if (this.unicornMatches == null) {
      this.unicornMatches = new ArrayList<>();
    }
    this.unicornMatches.add(unicornMatchesItem);
    return this;
  }

  /**
   * An array with three values, number of times a unicorn match (Win + kPa &amp; Rotor Bonuses) occured, number of opportunities to have a unicorn match, and percentage.
   * @return unicornMatches
   */
  @javax.annotation.Nonnull

  public List<Float> getUnicornMatches() {
    return unicornMatches;
  }


  public void setUnicornMatches(@javax.annotation.Nonnull List<Float> unicornMatches) {
    this.unicornMatches = unicornMatches;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventInsights2017 eventInsights2017 = (EventInsights2017) o;
    return Objects.equals(this.averageFoulScore, eventInsights2017.averageFoulScore) &&
        Objects.equals(this.averageFuelPoints, eventInsights2017.averageFuelPoints) &&
        Objects.equals(this.averageFuelPointsAuto, eventInsights2017.averageFuelPointsAuto) &&
        Objects.equals(this.averageFuelPointsTeleop, eventInsights2017.averageFuelPointsTeleop) &&
        Objects.equals(this.averageHighGoals, eventInsights2017.averageHighGoals) &&
        Objects.equals(this.averageHighGoalsAuto, eventInsights2017.averageHighGoalsAuto) &&
        Objects.equals(this.averageHighGoalsTeleop, eventInsights2017.averageHighGoalsTeleop) &&
        Objects.equals(this.averageLowGoals, eventInsights2017.averageLowGoals) &&
        Objects.equals(this.averageLowGoalsAuto, eventInsights2017.averageLowGoalsAuto) &&
        Objects.equals(this.averageLowGoalsTeleop, eventInsights2017.averageLowGoalsTeleop) &&
        Objects.equals(this.averageMobilityPointsAuto, eventInsights2017.averageMobilityPointsAuto) &&
        Objects.equals(this.averagePointsAuto, eventInsights2017.averagePointsAuto) &&
        Objects.equals(this.averagePointsTeleop, eventInsights2017.averagePointsTeleop) &&
        Objects.equals(this.averageRotorPoints, eventInsights2017.averageRotorPoints) &&
        Objects.equals(this.averageRotorPointsAuto, eventInsights2017.averageRotorPointsAuto) &&
        Objects.equals(this.averageRotorPointsTeleop, eventInsights2017.averageRotorPointsTeleop) &&
        Objects.equals(this.averageScore, eventInsights2017.averageScore) &&
        Objects.equals(this.averageTakeoffPointsTeleop, eventInsights2017.averageTakeoffPointsTeleop) &&
        Objects.equals(this.averageWinMargin, eventInsights2017.averageWinMargin) &&
        Objects.equals(this.averageWinScore, eventInsights2017.averageWinScore) &&
        Objects.equals(this.highKpa, eventInsights2017.highKpa) &&
        Objects.equals(this.highScore, eventInsights2017.highScore) &&
        Objects.equals(this.kpaAchieved, eventInsights2017.kpaAchieved) &&
        Objects.equals(this.mobilityCounts, eventInsights2017.mobilityCounts) &&
        Objects.equals(this.rotor1Engaged, eventInsights2017.rotor1Engaged) &&
        Objects.equals(this.rotor1EngagedAuto, eventInsights2017.rotor1EngagedAuto) &&
        Objects.equals(this.rotor2Engaged, eventInsights2017.rotor2Engaged) &&
        Objects.equals(this.rotor2EngagedAuto, eventInsights2017.rotor2EngagedAuto) &&
        Objects.equals(this.rotor3Engaged, eventInsights2017.rotor3Engaged) &&
        Objects.equals(this.rotor4Engaged, eventInsights2017.rotor4Engaged) &&
        Objects.equals(this.takeoffCounts, eventInsights2017.takeoffCounts) &&
        Objects.equals(this.unicornMatches, eventInsights2017.unicornMatches);
  }

  @Override
  public int hashCode() {
    return Objects.hash(averageFoulScore, averageFuelPoints, averageFuelPointsAuto, averageFuelPointsTeleop, averageHighGoals, averageHighGoalsAuto, averageHighGoalsTeleop, averageLowGoals, averageLowGoalsAuto, averageLowGoalsTeleop, averageMobilityPointsAuto, averagePointsAuto, averagePointsTeleop, averageRotorPoints, averageRotorPointsAuto, averageRotorPointsTeleop, averageScore, averageTakeoffPointsTeleop, averageWinMargin, averageWinScore, highKpa, highScore, kpaAchieved, mobilityCounts, rotor1Engaged, rotor1EngagedAuto, rotor2Engaged, rotor2EngagedAuto, rotor3Engaged, rotor4Engaged, takeoffCounts, unicornMatches);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventInsights2017 {\n");
    sb.append("    averageFoulScore: ").append(toIndentedString(averageFoulScore)).append("\n");
    sb.append("    averageFuelPoints: ").append(toIndentedString(averageFuelPoints)).append("\n");
    sb.append("    averageFuelPointsAuto: ").append(toIndentedString(averageFuelPointsAuto)).append("\n");
    sb.append("    averageFuelPointsTeleop: ").append(toIndentedString(averageFuelPointsTeleop)).append("\n");
    sb.append("    averageHighGoals: ").append(toIndentedString(averageHighGoals)).append("\n");
    sb.append("    averageHighGoalsAuto: ").append(toIndentedString(averageHighGoalsAuto)).append("\n");
    sb.append("    averageHighGoalsTeleop: ").append(toIndentedString(averageHighGoalsTeleop)).append("\n");
    sb.append("    averageLowGoals: ").append(toIndentedString(averageLowGoals)).append("\n");
    sb.append("    averageLowGoalsAuto: ").append(toIndentedString(averageLowGoalsAuto)).append("\n");
    sb.append("    averageLowGoalsTeleop: ").append(toIndentedString(averageLowGoalsTeleop)).append("\n");
    sb.append("    averageMobilityPointsAuto: ").append(toIndentedString(averageMobilityPointsAuto)).append("\n");
    sb.append("    averagePointsAuto: ").append(toIndentedString(averagePointsAuto)).append("\n");
    sb.append("    averagePointsTeleop: ").append(toIndentedString(averagePointsTeleop)).append("\n");
    sb.append("    averageRotorPoints: ").append(toIndentedString(averageRotorPoints)).append("\n");
    sb.append("    averageRotorPointsAuto: ").append(toIndentedString(averageRotorPointsAuto)).append("\n");
    sb.append("    averageRotorPointsTeleop: ").append(toIndentedString(averageRotorPointsTeleop)).append("\n");
    sb.append("    averageScore: ").append(toIndentedString(averageScore)).append("\n");
    sb.append("    averageTakeoffPointsTeleop: ").append(toIndentedString(averageTakeoffPointsTeleop)).append("\n");
    sb.append("    averageWinMargin: ").append(toIndentedString(averageWinMargin)).append("\n");
    sb.append("    averageWinScore: ").append(toIndentedString(averageWinScore)).append("\n");
    sb.append("    highKpa: ").append(toIndentedString(highKpa)).append("\n");
    sb.append("    highScore: ").append(toIndentedString(highScore)).append("\n");
    sb.append("    kpaAchieved: ").append(toIndentedString(kpaAchieved)).append("\n");
    sb.append("    mobilityCounts: ").append(toIndentedString(mobilityCounts)).append("\n");
    sb.append("    rotor1Engaged: ").append(toIndentedString(rotor1Engaged)).append("\n");
    sb.append("    rotor1EngagedAuto: ").append(toIndentedString(rotor1EngagedAuto)).append("\n");
    sb.append("    rotor2Engaged: ").append(toIndentedString(rotor2Engaged)).append("\n");
    sb.append("    rotor2EngagedAuto: ").append(toIndentedString(rotor2EngagedAuto)).append("\n");
    sb.append("    rotor3Engaged: ").append(toIndentedString(rotor3Engaged)).append("\n");
    sb.append("    rotor4Engaged: ").append(toIndentedString(rotor4Engaged)).append("\n");
    sb.append("    takeoffCounts: ").append(toIndentedString(takeoffCounts)).append("\n");
    sb.append("    unicornMatches: ").append(toIndentedString(unicornMatches)).append("\n");
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

