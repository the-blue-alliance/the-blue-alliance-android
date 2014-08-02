package com.thebluealliance.androidclient.accounts;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.JSONManager;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;

/**
 * Created by phil on 7/31/14.
 */
public class DriveHelper {

    private static final String MIMETYPE_JSON = "application/json";
    public static final String USERDATA_FILENAME = "tba_userdata.json";
    public static final String USER_SECRET = "user_secret";

    public static void writeUserSecretToDrive(String secret, GoogleApiClient apiClient) {
        DriveFile currentFile = lookupUserDataFile(apiClient);
        if(currentFile == null){
            // no file currently exists. Create it
            JsonObject data = new JsonObject();
            data.addProperty(USER_SECRET, secret);
            try {
                createNewUserDataFile(data, apiClient);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Unable to write to drive file");
                e.printStackTrace();
            }
        }else{
            JsonObject currentData;
            try {
                currentData = loadFromCloud(currentFile, apiClient);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Unable to read existing drive file");
                e.printStackTrace();
                return;
            }
            currentData.addProperty(USER_SECRET, secret);
            try {
                writeFile(currentFile, currentData, apiClient);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Unable to write to drive file");
                e.printStackTrace();
            }
        }
    }

    private static void writeFile(DriveFile file, JsonObject data, GoogleApiClient apiClient) throws IOException {
        DriveApi.ContentsResult contentsResult = file.openContents(apiClient,
                DriveFile.MODE_WRITE_ONLY, null).await();
        checkStatus("Open file for writing", contentsResult.getStatus());
        FileOutputStream os = new FileOutputStream(contentsResult.getContents()
                .getParcelFileDescriptor().getFileDescriptor());
        String contents = data.toString();
        Log.d(Constants.LOG_TAG, "Saving contents to drive file: " + contents);
        PrintStream writer = new PrintStream(os);
        writer.print(contents);
        com.google.android.gms.common.api.Status status =
                file.commitAndCloseContents(apiClient, contentsResult.getContents()).await();
        writer.close();
        checkStatus("Commit file contents", status);
    }

    public static void checkStatus(String message, com.google.android.gms.common.api.Status status) {
        if (!status.isSuccess()) {
            Log.e(Constants.LOG_TAG, "Error " + status.getStatusCode() + " on " + message);
        }
    }

    public static JsonObject loadFromCloud(DriveFile file, GoogleApiClient apiClient)
            throws IOException {
        DriveApi.ContentsResult contentsResult = file.openContents(apiClient, DriveFile.MODE_READ_ONLY, null).await();
        checkStatus("Open file for reading", contentsResult.getStatus());

        try {
            FileInputStream is = new FileInputStream(contentsResult.getContents()
                    .getParcelFileDescriptor().getFileDescriptor());
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer);
            String contents = writer.toString();
            file.discardContents(apiClient, contentsResult.getContents());

            Log.d(Constants.LOG_TAG, "Contents in the cloud file: [" + contents + "]");

            return JSONManager.getasJsonObject(contents);

        } catch (Exception ex) {
            Log.w(Constants.LOG_TAG, "Ignoring invalid remote content.", ex);
            return null;
        }
    }

    private static DriveFile lookupUserDataFile(GoogleApiClient apiClient) {
        DriveFile result = null;

        // search for a file with the expected name (and get the most recent one, if many)
        Log.d(Constants.LOG_TAG, "Looking up for a file named " + USERDATA_FILENAME);
        Metadata metaOfMostRecent = null;
        MetadataBuffer buffer = Drive.DriveApi.getAppFolder(apiClient).listChildren(apiClient).await().getMetadataBuffer();
        Log.d(Constants.LOG_TAG, "Found " + buffer.getCount() + " files");
        for (Metadata metadata : buffer) {
            if (metaOfMostRecent != null) {
                Log.w(Constants.LOG_TAG, "Warning, found more than one file named " + USERDATA_FILENAME +
                        " in AppData folder. Using the most recently modified.");
            }
            if (metaOfMostRecent == null || metaOfMostRecent.getModifiedDate().compareTo(metadata.getModifiedDate()) < 0) {
                metaOfMostRecent = metadata;
            }
        }
        if (metaOfMostRecent != null) {
            DriveId driveId = metaOfMostRecent.getDriveId();
            result = Drive.DriveApi.getFile(apiClient, driveId);
        }
        buffer.close();

        return result;
    }

    private static void createNewUserDataFile(JsonObject contents, GoogleApiClient apiClient) throws IOException {

        DriveApi.ContentsResult contentsResult = Drive.DriveApi.newContents(apiClient).await();
        checkStatus("creating new file", contentsResult.getStatus());

        DriveFolder appDataFolder = Drive.DriveApi.getAppFolder(apiClient);

        MetadataChangeSet metadataChangeSet =
                new MetadataChangeSet.Builder()
                        .setMimeType(MIMETYPE_JSON)
                        .setTitle(USERDATA_FILENAME)
                        .build();
        Contents contentsObj = contentsResult.getContents();

        FileOutputStream os = new FileOutputStream(contentsObj.getParcelFileDescriptor().getFileDescriptor());
        PrintStream writer = new PrintStream(os);
        writer.print(contents);
        DriveFolder.DriveFileResult fileResult = appDataFolder.createFile(
                apiClient, metadataChangeSet, contentsResult.getContents()).await();
        writer.close();

        Log.d(Constants.LOG_TAG, "Content saved to new Drive file: " + contents.toString());
        checkStatus("saving contents to new file", fileResult.getStatus());
    }
}
