package com.thebluealliance.androidclient.viewmodels;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

public interface ViewModelRenderer<VIEWMODEL, ARGS> {

    @WorkerThread
    @Nullable VIEWMODEL renderToViewModel(Context context, @Nullable ARGS args);
}
