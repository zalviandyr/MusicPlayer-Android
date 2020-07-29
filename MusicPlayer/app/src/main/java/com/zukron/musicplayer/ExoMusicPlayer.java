package com.zukron.musicplayer;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 7/28/2020
 */
public class ExoMusicPlayer {
    private Context context;
    private SimpleExoPlayer simpleExoPlayer;

    public ExoMusicPlayer(Context context) {
        this.context = context;
        initExoplayer();
    }

    private void initExoplayer() {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(context, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        TrackSelector trackSelector = new DefaultTrackSelector();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector);
    }

    public void prepare(String pathMusic) {
        // jika exo player playing dan mengatasi bug jika kedua lagu berputar bersamaan
        if (simpleExoPlayer.getPlayWhenReady() && simpleExoPlayer.getPlaybackState() == Player.STATE_READY) {
            simpleExoPlayer.release();
            initExoplayer();
        }

        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        ExtractorMediaSource mediaSource = new ExtractorMediaSource(
                Uri.parse(pathMusic),
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );

        simpleExoPlayer.prepare(mediaSource);
    }

    public boolean isPlaying() {
        return simpleExoPlayer.getPlayWhenReady();
    }

    public void play() {
        simpleExoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        simpleExoPlayer.setPlayWhenReady(false);
    }

    public long getCurrentPosition() {
        return simpleExoPlayer.getCurrentPosition();
    }

    public void reset() {
        // jika exo player playing dan mengatasi bug jika kedua lagu berputar bersamaan
        if (simpleExoPlayer.getPlayWhenReady() && simpleExoPlayer.getPlaybackState() == Player.STATE_READY) {
            simpleExoPlayer.release();
            initExoplayer();
        }
    }

    public void preparePlaylist(String path) {
        File file = new File(path);
        File[] files = file.listFiles();

        MediaSource[] mediaSources = null;
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, userAgent);

        if (files != null) {
            mediaSources = new MediaSource[files.length];

            for (int i = 0; i < files.length; i++) {
                Uri uri = Uri.parse(files[i].getAbsolutePath());
                MediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);

                mediaSources[i] = mediaSource;
            }
        }

        if (mediaSources != null) {
            ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(mediaSources);
            simpleExoPlayer.prepare(concatenatingMediaSource);
        }
    }

    public void playIndex(int position) {
        simpleExoPlayer.seekTo(position, C.TIME_UNSET);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    public String getCurrentDuration() {
        long duration = simpleExoPlayer.getContentPosition();
        return convertTime(duration);
    }

    public String getDurationStr() {
        long duration = simpleExoPlayer.getDuration();
        return convertTime(duration);
    }

    public long getDuration() {
        return simpleExoPlayer.getDuration();
    }

    public int getProgress() {
        long progress = (simpleExoPlayer.getCurrentPosition() * 100) / simpleExoPlayer.getDuration();
        return (int) progress;
    }

    public void seekTo(long progressPosition){
        simpleExoPlayer.seekTo(progressPosition);
    }

    private String convertTime(long millis) {
        long minute = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        return String.format(Locale.US, "%02d:%02d", minute, seconds);
    }

    public void addListener(Player.DefaultEventListener listener) {
        simpleExoPlayer.addListener(listener);
    }
}
