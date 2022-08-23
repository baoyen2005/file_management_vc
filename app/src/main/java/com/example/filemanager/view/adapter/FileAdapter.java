package com.example.filemanager.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.example.filemanager.databinding.FileItemLayoutBinding;
import com.example.filemanager.utils.FunctionUtils;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private final String TAG = "FileAdapter";
    private final List<File> files;
    private final FileItemCallBack fileItemCallBack;

    public FileAdapter(List<File> files, FileItemCallBack fileItemCallBack) {
        this.files = files;
        this.fileItemCallBack = fileItemCallBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FileItemLayoutBinding binding = FileItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.binding.constraintItem.getContext();
        File fileItem = files.get(position);

        holder.binding.tvFileNameItem.setText(fileItem.getName());
        holder.binding.tvLastModifiedItem.setText(FunctionUtils.getFileLastModified(fileItem));
        if (!fileItem.isDirectory()) {
            String ext = FunctionUtils.getExt(fileItem.getPath());
            if (ext == null) {
            } else {
                switch (ext) {
                    case "mp3": {
                        holder.binding.imgFileTypeItem.setImageResource(R.drawable.ic_baseline_music_note_24);
                        break;
                    }
                    case "gif": {
                        holder.binding.imgFileTypeItem.setImageResource(R.drawable.ic_baseline_video_file_24);
                        break;
                    }
                    case "apk": {
                        holder.binding.imgFileTypeItem.setImageResource(R.drawable.apk_icon);
                        break;
                    }
                    case "jpeg":
                    case "jpg":
                    case "png":
                    case "mp4": {
                        String url = fileItem.getPath();
                        Glide.with(context)
                                .load(new File(url))
                                .centerCrop()
                                .into(holder.binding.imgFileTypeItem);
                        break;
                    }
                    default: {
                        holder.binding.imgFileTypeItem.setImageResource(R.drawable.ic_baseline_file_copy_24);
                        break;
                    }
                }
            }
        }
        if (fileItem.isDirectory()) {
            String quantity = FunctionUtils.getFileQuantity(fileItem);
            holder.binding.tvFileSizeItem.setText(quantity);
        } else {
            String size = FunctionUtils.getFileSize(fileItem);
            Log.d(TAG, size);
            holder.binding.tvFileSizeItem.setText(size);
        }
        holder.binding.constraintItem.setOnClickListener(v -> {
            fileItemCallBack.onItemClickListener(fileItem, position);
        });
    }

    @Override
    public int getItemCount() {
        if (!files.isEmpty()) return files.size();
        else return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<File> list) {
        if (list != null && !list.isEmpty()) {
            if (!files.isEmpty()) files.clear();
            files.addAll(list);
            notifyDataSetChanged();
        } else {
            Log.d(TAG, "updateList: list null or empty");
        }
    }

    public interface FileItemCallBack {
        void onItemClickListener(File file, int position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final FileItemLayoutBinding binding;

        public ViewHolder(FileItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
