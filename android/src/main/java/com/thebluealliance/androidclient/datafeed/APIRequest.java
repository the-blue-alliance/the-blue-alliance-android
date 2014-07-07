package com.thebluealliance.androidclient.datafeed;

import java.util.Arrays;

/**
 * Created by phil on 7/7/14.
 */
public class APIRequest {
    TBAv2.QUERY type;
    String url;
    String sqlWhere;
    String[] whereArgs;
    APIResponse<String> response;

    /**
     * Constructor for an object wrapping an API Request
     * @param type
     * @param url
     * @param whereArgs
     */
    public APIRequest(TBAv2.QUERY type, String url,String[] whereArgs) {
        this.type = type;
        this.url = url;
        this.whereArgs = whereArgs;
        this.sqlWhere = TBAv2.API_WHERE.get(this.type);
    }

    public TBAv2.QUERY getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getSqlWhere() {
        return sqlWhere;
    }

    public String[] getWhereArgs() {
        return whereArgs;
    }

    public void setResponse(APIResponse<String> response){
        this.response = response;
    }

    public APIResponse<String> getResponse(){
        return response;
    }

    @Override
    public String toString() {
        return "APIRequest{" +
                "type=" + type +
                ", url='" + url + '\'' +
                ", sqlWhere='" + sqlWhere + '\'' +
                ", whereArgs=" + Arrays.toString(whereArgs) +
                '}';
    }

    @Override
    public boolean equals(Object in) {
        if (this == in) return true;
        if (in == null || getClass() != in.getClass()) return false;

        APIRequest other = (APIRequest) in;

        return type == other.type && url.equals(other.url);

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }
}
