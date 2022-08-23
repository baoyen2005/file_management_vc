package com.example.filemanager.controller;

import com.example.filemanager.model.FileManagementModel;

import java.io.File;
import java.util.List;
import java.util.Stack;

public class FileFragmentController {
    private final String TAG = "FileFragmentController";

    private final FileManagementModel managementModel;

    public FileFragmentController() {
        managementModel = new FileManagementModel();
    }

    public List<File> getAllFiles() {
        return managementModel.getAllFilesFromExternal();
    }

    public List<File> getAllFilesInFolder(File file) {
        return managementModel.findFilesInExternal(file);
    }

    public List<File> getFilesByName(String name) {
        return managementModel.getFilesByName(name);
    }

    public Stack<String> getFileStack() {
        return managementModel.getFileStack();
    }

    public void setFileStack(Stack<String> fileStack) {
        managementModel.setFileStack(fileStack);
    }
}
