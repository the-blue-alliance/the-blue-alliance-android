package com.thebluealliance.androidclient.tracing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.perf.metrics.Trace;

public class TraceWrapper {
    private @Nullable Trace mTrace;

    public TraceWrapper() {
        mTrace = null;
    }

    public TraceWrapper(@NonNull Trace trace) {
        mTrace = trace;
    }

    public void start() {
        if (mTrace != null) {
            mTrace.start();
        }
    }

    public void stop() {
        if (mTrace != null) {
            mTrace.stop();
        }
    }

    public void putAttribute(String key, String value) {
        if (mTrace != null) {
            mTrace.putAttribute(key, value);
        }
    }

    public void putMetric(String metricName, long metricValue) {
        if (mTrace != null) {
            mTrace.putMetric(metricName, metricValue);
        }
    }
}
