package com.example.filemanager.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.filemanager.BuildConfig;

import java.io.File;

public class FileOpen {
    private final String TAG = "FileOpen";

    public void openFile(Context context, File file) {
        Uri uri = uriFromFile(context, file);
        Log.d("FileOpen", "readFile+ uri: " + uri.toString());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (uri.toString().contains(".docx")) {
            intent.setDataAndType(uri, "application/msword");
        } else if (uri.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (uri.toString().contains(".pptx") || uri.toString().contains(".ppt")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (uri.toString().contains(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (uri.toString().contains(".wav")) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (uri.toString().toLowerCase().contains(".jpeg")
                || uri.toString().toLowerCase().contains(".jpg")
                || uri.toString().toLowerCase().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if (uri.toString().contains(".mp4")) {
            intent.setDataAndType(uri, "video/*");
        } else if (uri.toString().contains(".mp3")) {
            intent.setDataAndType(uri, "audio/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            Log.d(TAG, "openFile: try");
            context.startActivity(Intent.createChooser(intent, "Read by:"));
        } catch (Exception e) {
            Log.d(TAG, "openFile: catch " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Log.d(TAG, "uriFromFile: uri = " + FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                ));
                return FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                );
            } catch (Exception e) {
                Log.d(TAG, "uriFromFile: cacth = " + e.getMessage());
                return Uri.fromFile(file);
            }

        } else {
            Log.d(TAG, "uriFromFile: else = " + Uri.fromFile(file));
            return Uri.fromFile(file);
        }
    }
}
