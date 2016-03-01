package com.thebluealliance.androidclient.viewmodels;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public interface ViewModelRenderer<VIEWMODEL, ARGS> {

    @WorkerThread
    @Nullable VIEWMODEL renderToViewModel(Context context, @Nullable ARGS args);
}
