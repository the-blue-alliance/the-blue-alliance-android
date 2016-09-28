package com.thebluealliance.androidclient.models;

import java.util.Date;

public class APIStatus extends com.thebluealliance.api.model.ApiStatus {

    private String jsonBlob;

    /* Admin Message */
    private Date messageExipration;

    public APIStatus() {

    }

   public String getJsonBlob() {
        return jsonBlob;
    }

    public void setJsonBlob(String jsonBlob) {
        this.jsonBlob = jsonBlob;
    }

}
