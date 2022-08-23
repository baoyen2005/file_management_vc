package com.example.filemanager.model;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FileManagementModel {
    private final String TAG = "FileManagementModel";
    private Stack<String> fileStack = new Stack<>();
    private final List<File> files = new ArrayList<>();
    private final List<File> allFiles = new ArrayList<>();

    public Stack<String> getFileStack() {
        return fileStack;
    }

    public void setFileStack(Stack<String> fileStack) {
        this.fileStack = fileStack;
    }

    public List<File> findFilesInExternal(File root) {
        File[] tempFiles = root.listFiles();
        if (root.getAbsolutePath().equals(Environment.getExternalStorageDirectory().toString())) {
            if (tempFiles != null && tempFiles.length > 0) {
                allFiles.clear();
                for (File fi : tempFiles) {
                    if (!fi.isHidden() && fi.isDirectory()) ;
                    allFiles.add(fi);
                }
            }
        } else {
            if (tempFiles != null && tempFiles.length > 0) {
                allFiles.clear();
                for (File fi : tempFiles) {
                    allFiles.add(fi);
                }
            }
        }
        return allFiles;
    }

    public List<File> getAllFilesFromExternal() {
        String DIR_INTERNAL = Environment.getExternalStorageDirectory().getAbsolutePath();
        File storage = new File(DIR_INTERNAL);
        files.clear();
        files.addAll(findFilesInExternal(storage));
        if (!fileStack.contains(storage.getAbsolutePath())) {
            fileStack.add(storage.getAbsolutePath());
        }
        setFileStack(fileStack);
        return files;
    }

    public List<File> getFilesByName(String name) {
        List<File> list = new ArrayList<>();
        if (files.isEmpty()) {
            Log.d(TAG, "getFilesByName: files is empty");
        } else {
            for (int i = 0; i < files.size(); i++) {
                if (files.get(i).getName().toLowerCase().contains(name.toLowerCase())) {
                    list.add(files.get(i));
                }
            }
        }
        return list;
    }
}
