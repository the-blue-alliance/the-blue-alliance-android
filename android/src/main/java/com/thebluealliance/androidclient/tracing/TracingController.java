package com.thebluealliance.androidclient.tracing;

import androidx.annotation.Nullable;

import com.google.firebase.perf.FirebasePerformance;

public class TracingController {
    private @Nullable FirebasePerformance mFirebasePef;

    public TracingController(@Nullable FirebasePerformance firebasePerf) {
        mFirebasePef = firebasePerf;
    }

    public TraceWrapper newTrace(String traceName) {
        if (mFirebasePef != null) {
            return new TraceWrapper(mFirebasePef.newTrace(traceName));
        }

        return new TraceWrapper();
    }
}
