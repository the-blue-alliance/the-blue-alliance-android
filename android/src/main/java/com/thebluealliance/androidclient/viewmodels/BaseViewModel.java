package com.thebluealliance.androidclient.viewmodels;

import java.util.Arrays;

public abstract class BaseViewModel {

    public abstract boolean equals(Object o);

    public abstract int hashCode();

    protected static int hashFromValues(Object... values) {
        return Arrays.deepHashCode(values);
    }
}
