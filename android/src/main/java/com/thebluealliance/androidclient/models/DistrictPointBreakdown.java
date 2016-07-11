package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listitems.DistrictTeamListElement;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.renderers.DistrictPointBreakdownRenderer;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.res.Resources;

public class DistrictPointBreakdown implements RenderableModel {

    private int qualPoints, elimPoints, alliancePoints, awardPoints, totalPoints;
    private String teamKey, districtKey, teamName;
    private int rank;

    public DistrictPointBreakdown() {
        this.qualPoints = -1;
        this.elimPoints = -1;
        this.alliancePoints = -1;
        this.awardPoints = -1;
        this.totalPoints = -1;
        rank = -1;
    }

    public int getQualPoints() {
        return qualPoints;
    }

    public void setQualPoints(int qualPoints) {
        this.qualPoints = qualPoints;
    }

    public int getElimPoints() {
        return elimPoints;
    }

    public void setElimPoints(int elimPoints) {
        this.elimPoints = elimPoints;
    }

    public int getAlliancePoints() {
        return alliancePoints;
    }

    public void setAlliancePoints(int alliancePoints) {
        this.alliancePoints = alliancePoints;
    }

    public int getAwardPoints() {
        return awardPoints;
    }

    public void setAwardPoints(int awardPoints) {
        this.awardPoints = awardPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public RenderableModel renderQualPoints(Resources resources) {
        return new BreakdownItem(
          resources.getString(R.string.district_qual_points),
          String.format(resources.getString(R.string.district_points_format), qualPoints));
    }

    public RenderableModel renderElimPoints(Resources resources) {
        return new BreakdownItem(
          resources.getString(R.string.district_elim_points),
          String.format(resources.getString(R.string.district_points_format), elimPoints));
    }

    public RenderableModel renderAlliancePoints(Resources resources) {
        return new BreakdownItem(
          resources.getString(R.string.district_alliance_points),
          String.format(resources.getString(R.string.district_points_format), alliancePoints));
    }

    public RenderableModel renderAwardPoints(Resources resources) {
        return new BreakdownItem(
          resources.getString(R.string.district_award_points),
          String.format(resources.getString(R.string.district_points_format), awardPoints));
    }

    public RenderableModel renderTotalPoints(Resources resources) {
        return new BreakdownItem(resources.getString(R.string.total_district_points),
          String.format(resources.getString(R.string.district_points_format), totalPoints));
    }

    public String getTeamKey() {
        return teamKey;
    }

    public void setTeamKey(String teamKey) {
        this.teamKey = teamKey;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDistrictKey() {
        return districtKey;
    }

    public void setDistrictKey(String districtKey) {
        this.districtKey = districtKey;
    }

    @Override
    public DistrictTeamListElement render(ModelRendererSupplier supplier) {
        DistrictPointBreakdownRenderer renderer =
          (DistrictPointBreakdownRenderer)supplier.getRendererForType(ModelType.DISTRICTPOINTS);
        return renderer != null ? renderer.renderFromModel(this, null) : null;
    }

    private class BreakdownItem implements RenderableModel {

        private String key, value;

        public BreakdownItem(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public ListElement render(ModelRendererSupplier supplier) {
            return new LabelValueListItem(key, value);
        }
    }
}
