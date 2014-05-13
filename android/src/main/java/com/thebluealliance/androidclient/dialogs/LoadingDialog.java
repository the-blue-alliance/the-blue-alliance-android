package com.thebluealliance.androidclient.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * File created by phil on 5/13/14.
 */
public class LoadingDialog extends DialogFragment {

    public static final String MSG = "message", TITLE = "title";
    private static String msg, title;

    public static LoadingDialog newInstance(String title, String message){
        LoadingDialog d = new LoadingDialog();
        Bundle args = new Bundle();
        args.putString(MSG, message);
        args.putString(TITLE, title);
        d.setArguments(args);
        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() == null || !getArguments().containsKey(MSG) || !getArguments().containsKey(TITLE)){
            throw new IllegalArgumentException("LoadingDialog must be created with message and title parameters. Use LoadingDialog.newInstance");
        }
        msg = getArguments().getString(MSG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.ProgressDialog dialog = android.app.ProgressDialog.show(getActivity(), title, msg, true);
        dialog.setCancelable(false);
        return dialog;
    }
}
