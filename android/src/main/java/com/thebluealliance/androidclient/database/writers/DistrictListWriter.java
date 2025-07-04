package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.model.DistrictDbModel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.WorkerThread;

import thebluealliance.api.model.District;

public class DistrictListWriter extends BaseDbWriter<List<District>> {
    @Inject
    public DistrictListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<District> districts) {
        List<DistrictDbModel> models = districts.stream()
                .map(DistrictDbModel::fromDistrict)
                .collect(Collectors.toList());
        mDb.getDistrictsTable().add(ImmutableList.copyOf(models));
    }
}