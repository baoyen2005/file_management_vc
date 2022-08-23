package com.example.filemanager.view.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.filemanager.controller.FileFragmentController;
import com.example.filemanager.controller.FileOpen;
import com.example.filemanager.databinding.FragmentFileManagementBinding;
import com.example.filemanager.view.adapter.FileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


public class FileManagementFragment extends Fragment {
    private final String TAG = "Fragment";
    private FragmentFileManagementBinding binding;
    public FileAdapter fileAdapter;
    public FileFragmentController controller;

    public FileManagementFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFileManagementBinding.inflate(inflater, container, false);
        controller = new FileFragmentController();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initValue();
    }

    public void initValue() {
        List<File> files = controller.getAllFiles();
        List<File> filesOfRoot = controller.getAllFilesInFolder(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
        //TODO adapter
        fileAdapter = new FileAdapter(files, new FileItemCallBack());
        binding.recycleFiles.setHasFixedSize(true);
        binding.recycleFiles.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recycleFiles.setAdapter(fileAdapter);
        //TODO toolbar + search view
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.isEmpty()) {
                    fileAdapter.updateList(filesOfRoot);
                } else {
                    List<File> list = controller.getFilesByName(newText);
                    if (list.isEmpty()) {
                        binding.recycleFiles.setVisibility(View.GONE);
                    } else {
                        binding.recycleFiles.setVisibility(View.VISIBLE);
                        fileAdapter.updateList(list);
                    }
                }
                return false;
            }
        });
        binding.searchView.setOnCloseListener(() -> {
            binding.recycleFiles.setVisibility(View.VISIBLE);
            fileAdapter.updateList(filesOfRoot);
            return false;
        });
    }

    private class FileItemCallBack implements FileAdapter.FileItemCallBack {
        Stack<String> fileStackClicked = new Stack<>();

        @Override
        public void onItemClickListener(File file, int position) {
            fileStackClicked.add(file.getAbsolutePath()); // luuw đường daanxx mỗi khi click 1 file or folder
            controller.setFileStack(fileStackClicked);
            if (file.isDirectory()) {
                List<File> arrayList = new ArrayList<>();
                File[] files = file.listFiles();
                if (files != null) {
                    arrayList.addAll(Arrays.asList(files));
                }
                fileAdapter.updateList(controller.getAllFilesInFolder(new File(file.getAbsolutePath())));
            } else {
                fileStackClicked.pop();// click file thì xóa đường dẫn
                FileOpen fileOpen = new FileOpen();
                try {
                    fileOpen.openFile(getContext(), file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}