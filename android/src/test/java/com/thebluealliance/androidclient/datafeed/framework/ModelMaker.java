package com.thebluealliance.androidclient.datafeed.framework;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.modules.DatafeedModule;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to load models from local json
 * For use in tests
 */
public class ModelMaker {

    private static ModelMaker sModelMaker;

    /**
     * Gets a model from the given file
     * @param modelClass Type of the model
     * @param fileName Name of the json file (without extension). Relative to src/test/resources
     * @return Deserialized model
     */
    public static <MODEL> MODEL getModel(Class<MODEL> modelClass, String fileName) {
        if (sModelMaker == null) {
            sModelMaker = new ModelMaker();
        }
        return sModelMaker.innerGetModel(modelClass, fileName);
    }

    public static <MODEL> List<MODEL> getModelList(Class<MODEL> listClass, String fileName) {
        if (sModelMaker == null) {
            sModelMaker = new ModelMaker();
        }
        return sModelMaker.innerGetModelList(listClass, fileName);
    }

    private <MODEL> MODEL innerGetModel(Class<MODEL> modelClass, String fileName) {
        fileName = fileName + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        Gson gson = DatafeedModule.getGson();

        return gson.fromJson(new BufferedReader(new InputStreamReader(inputStream)), modelClass);
    }

    private <MODEL> List<MODEL> innerGetModelList(Class<MODEL> modelClass, String fileName) {
        fileName = fileName + ".json";
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        Gson gson = DatafeedModule.getGson();
        List<MODEL> output = new ArrayList<>();
        JsonArray fileData =
          gson.fromJson(new BufferedReader(new InputStreamReader(inputStream)), JsonArray.class);

        for (int i = 0; i < fileData.size(); i++) {
            output.add(gson.fromJson(fileData.get(i), modelClass));
        }
        return output;
    }
}
