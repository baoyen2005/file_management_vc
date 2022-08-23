package com.example.filemanager.service;

import static android.app.Activity.RESULT_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    private final Context context;
    private final String TAG = "NotificationReceiver";

    public NotificationReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String string = bundle.getString(DownloadService.FILEPATH);
            int resultCode = bundle.getInt(DownloadService.RESULT);
            if (resultCode == RESULT_OK) {
                Toast.makeText(context,
                        "Download complete. Download URI: " + string,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Download failed",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}