package com.thebluealliance.androidclient.tracing;

import com.google.firebase.perf.FirebasePerformance;

import androidx.annotation.Nullable;

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
