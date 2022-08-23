package com.example.filemanager.utils;

import static com.example.filemanager.utils.ValueUtils.invalidFileName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.example.filemanager.R;

import java.io.File;
import java.text.SimpleDateFormat;

public class FunctionUtils {
    public static void showToast(Context context, String mes) {
        Toast.makeText(context, mes, Toast.LENGTH_SHORT).show();
    }

    public static String getFileName(File file) {
        if (file == null)
            return invalidFileName;
        return file.getName();
    }

    public static String getFileLastModified(File file) {
        long dateLong = file.lastModified();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = simpleDateFormat.format(dateLong);
        return date;
    }

    public static String getFileQuantity(File file) {
        int length = 0;
        File[] files = file.listFiles();
        if (files != null) {
            length = files.length;
            if (length >= 1) {
                if (length == 1) {
                    return String.valueOf(R.string.one_file);
                } else {
                    return String.format("%s files", length);
                }
            } else {
                return String.valueOf(R.string.empty_file);
            }
        } else {
            return String.valueOf(R.string.empty_file);
        }
    }

    private static long folderSize(File file) {
        long size = 0;
        if (file != null) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    if (child.isDirectory()) {
                        size += folderSize(child);
                    } else size += child.length();
                }
            } else {
                size = file.length();
            }
        }
        return size;
    }

    @SuppressLint("DefaultLocale")
    public static String getFileSize(File file) {
        long size = folderSize(file);
        if (size < 1024) {

            return String.format("%.2f B", (double) size);
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", (double) size / 1024);
        } else {
            return String.format("%.2f MB", (double) size / (1024 * 1024));
        }
    }

    public static String getExt(String filePath) {
        int strLength = filePath.lastIndexOf(".");
        if (strLength > 0)
            return filePath.substring(strLength + 1).toLowerCase();
        return null;
    }
}
