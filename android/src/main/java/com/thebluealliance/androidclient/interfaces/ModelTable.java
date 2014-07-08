package com.thebluealliance.androidclient.interfaces;

import com.thebluealliance.androidclient.models.BasicModel;

/**
 * File created by phil on 6/21/14.
 */
public interface ModelTable<T extends BasicModel> {

    public long add(T in);
    public int update(T in);
    public T get(String key, String[] fields);
    public boolean exists(String key);
    public void delete(T in);

}
