package com.thebluealliance.androidclient.imgur;

import com.thebluealliance.androidclient.Constants;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ImgurUtils {

    private ImgurUtils() {
        // unused
    }

    public static File createFile(Uri uri, Context context) {
        Log.d(Constants.LOG_TAG, "URI: " + uri);
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);

            String timeStamp = String.valueOf(System.currentTimeMillis());
            File cacheDir = context.getCacheDir();
            File tempFile = new File(cacheDir, timeStamp + ".png");

            OutputStream out = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length = 0;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.close();
            in.close();

            return tempFile;
        } catch (IOException e) {
            System.out.println("error creating file");
            e.printStackTrace();
        }
        return null;
    }

    public static AlertDialog getTeamImageUploadInfoDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setMessage(Html.fromHtml("You can now swipe to the <b>Media</b> tab to upload robot images!"))
                .create();
    }
}
