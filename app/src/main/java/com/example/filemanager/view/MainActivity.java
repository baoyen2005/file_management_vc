package com.example.filemanager.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.example.filemanager.R;
import com.example.filemanager.controller.FileFragmentController;
import com.example.filemanager.databinding.ActivityMainBinding;
import com.example.filemanager.utils.FunctionUtils;
import com.example.filemanager.utils.ValueUtils;
import com.example.filemanager.view.adapter.ViewPagerAdapter;
import com.example.filemanager.view.fragment.FileManagementFragment;
import com.example.filemanager.view.fragment.ToolFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private ViewPagerAdapter viewPagerAdapter;
    private ActivityMainBinding binding;
    private FileManagementFragment fileManagementFragment;
    private ToolFragment toolFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();
        setViewpager();
        setBottomNavigationView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        fileManagementFragment = new FileManagementFragment();
        toolFragment = new ToolFragment();
    }

    private void setViewpager() {
        viewPagerAdapter.addFragment(fileManagementFragment);
        viewPagerAdapter.addFragment(toolFragment);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        if (position == 0) {
                            binding.bottomNav.getMenu().findItem(R.id.ic_file).setChecked(true);
                        } else {
                            binding.bottomNav.getMenu().findItem(R.id.ic_tool).setChecked(true);
                        }
                    }
                }
        );
    }

    private void setBottomNavigationView() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.ic_file) {
                new FileManagementFragment();
                binding.viewPager.setCurrentItem(0);
            } else {
                new ToolFragment();
                binding.viewPager.setCurrentItem(1);
            }
            return true;
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    FunctionUtils.showToast(this, ValueUtils.toastPermission);
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PackageManager.PERMISSION_GRANTED);
                }
            }
        } else {
            fileManagementFragment.initValue();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fileManagementFragment.initValue();
    }

    @Override
    public void onBackPressed() {
        FileFragmentController controller = fileManagementFragment.controller;
        List<File> list = new ArrayList<>();
        Stack<String> stack = controller.getFileStack();
        if (stack.size() > 1) {
            stack.pop();
            fileManagementFragment.controller.setFileStack(stack);
            if (!list.isEmpty()) list.clear();
            list.addAll(controller.getAllFilesInFolder(new File(controller.getFileStack().get(stack.size() - 1))));
            fileManagementFragment.fileAdapter.updateList(list);
        } else {
            if (!list.isEmpty()) list.clear();
            list.addAll(controller.getAllFilesInFolder(new File(Environment.getExternalStorageDirectory().getAbsolutePath())));
            fileManagementFragment.fileAdapter.updateList(list);
        }
    }
}