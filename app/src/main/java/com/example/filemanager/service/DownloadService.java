package com.example.filemanager.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadService extends IntentService {
    private final String TAG = "DownloadService";
    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "url_path";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "service receiver";

    public DownloadService() {
        super("DownloadService");
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        OkHttpClient client = new OkHttpClient();
        String urlPath = intent.getStringExtra(URL);
        String fileName = intent.getStringExtra(FILENAME);
        File mediaFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            mediaFile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + fileName);
        } else {
            mediaFile = new File(Environment.getExternalStorageDirectory(),
                    fileName);
        }
        Call call = client.newCall(new Request.Builder().url(urlPath).get().build());

        try {
            Response response = call.execute();
            if (response.code() == 200 || response.code() == 201) {
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.e(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                InputStream inputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    byte[] buff = new byte[1024 * 4];
                    OutputStream output = new FileOutputStream(mediaFile);
                    while (true) {
                        int readed = inputStream.read(buff);
                        if (readed == -1) {
                            result = Activity.RESULT_CANCELED;
                            break;
                        }
                        output.write(buff, 0, readed);
                    }
                    output.flush();
                    output.close();
                    result = Activity.RESULT_OK;
                    Log.e(TAG, "onHandleIntent: result = " + result);
                } catch (IOException ignore) {
                    Log.e(TAG, "onHandleIntent: IOException = " + ignore.getMessage());
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } else {
                result = Activity.RESULT_CANCELED;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = Activity.RESULT_CANCELED;
        }
        publishResults(mediaFile.getAbsolutePath(), result);
    }

    private void publishResults(String outputPath, int result) {
        Log.e(TAG, "publishResults: result = " + result);
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}