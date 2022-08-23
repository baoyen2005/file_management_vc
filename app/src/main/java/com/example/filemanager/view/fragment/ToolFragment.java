package com.example.filemanager.view.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filemanager.controller.ToolController;
import com.example.filemanager.databinding.FragmentToolBinding;
import com.example.filemanager.service.DownloadService;
import com.example.filemanager.service.NotificationReceiver;
import com.example.filemanager.utils.ValueUtils;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.util.Util;

public class ToolFragment extends Fragment {
    private final String TAG = "ToolFragment";
    private FragmentToolBinding binding;
    private SimpleExoPlayer player;
    private ToolController controller;
    private NotificationReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentToolBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = new ToolController();
        receiver = new NotificationReceiver(getContext());
        initPlayer();
        handleEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) initPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
        if (Util.SDK_INT < 24 || player == null) {
            initPlayer();
            hideSystemUI();
        }
        getActivity().registerReceiver(receiver, new IntentFilter(
                DownloadService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            if (player != null) {
                controller.releasePlayer(player);
                player = null;
            }
        }
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24)
            if (player != null) {
                controller.releasePlayer(player);
                player = null;
            }
    }

    public void initPlayer() {
        player = new SimpleExoPlayer.Builder(getContext()).build();
        binding.playerViewVideo.setPlayer(player);
        controller.playYoutubeVideo(getContext(), player);
    }

    private void handleEvent() {
        binding.btnDownloadVideo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DownloadService.class);
            intent.putExtra(DownloadService.FILENAME, "video_download.mp4");
            intent.putExtra(DownloadService.URL,
                    ValueUtils.urlYoutube);
            getActivity().startService(intent);
        });
    }

    private void hideSystemUI() {
        binding.playerViewVideo.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }
}