package com.example.filemanager.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;

import com.example.filemanager.utils.ValueUtils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class ToolController {
    private final String TAG = "ToolController";
    private boolean playerReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    public void releasePlayer(SimpleExoPlayer player) {
        if (player != null) {
            playerReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void playYoutubeVideo(Context context, SimpleExoPlayer player) {
        new YouTubeExtractor(context) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if (ytFiles != null) {
                    int videoTag = 137; // video tag for 1080p mp4
                    int audioTAg = 140;// audio tag for mp4
                    List<Integer> iTags = new ArrayList<>();
                    iTags.add(22);
                    iTags.add(137);
                    iTags.add(18);
                    String videoUrl = "";
                    for (int i : iTags) {
                        YtFile ytFile = ytFiles.get(i);
                        if (ytFile != null) {
                            String downloadUrl = ytFile.getUrl();
                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                videoUrl = downloadUrl;
                            }
                        }
                    }
                    if (videoUrl.equals("")) videoUrl = ytFiles.get(videoTag).getUrl();
                    String audioUrl = ytFiles.get(audioTAg).getUrl();

                    MediaSource audioSource = new ProgressiveMediaSource.Factory(
                            new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(audioUrl));

                    MediaSource videoSource = new ProgressiveMediaSource.Factory(
                            new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(videoUrl));

                    if (player != null) {
                        player.setMediaSource(new MergingMediaSource(
                                true, videoSource, audioSource), true);
                        player.prepare();
                        player.setPlayWhenReady(playerReady);
                        player.seekTo(currentWindow, playbackPosition);
                    }
                }
            }
        }.extract(ValueUtils.urlYoutube, false, true);
    }
}
