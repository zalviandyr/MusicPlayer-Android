package com.zukron.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.zukron.musicplayer.R;
import com.zukron.musicplayer.adapter.MusicAdapter;
import com.zukron.musicplayer.model.Library;
import com.zukron.musicplayer.model.Music;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailMusicActivity extends AppCompatActivity implements MusicAdapter.OnSelected, View.OnClickListener {
    private MaterialToolbar mtDetail;
    private TextView tvToolbarTitleDetail, tvTitlePlayingDetail;
    private TextView tvCurrentDetail, tvDurationDetail;
    private SeekBar sbDetail;
    private MaterialButton btnRandomDetail, btnLoopDetail, btnSkipPreviousDetail, btnPlayPauseDetail, btnSkipNextDetail;
    private RecyclerView rvDetail;
    private SimpleExoPlayer simpleExoPlayer;
    private Handler handler;
    private Runnable runnable;
    private ArrayList<File> listFileMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_music);

        mtDetail = findViewById(R.id.mt_detail);
        tvToolbarTitleDetail = findViewById(R.id.tv_toolbar_title_detail);
        tvTitlePlayingDetail = findViewById(R.id.tv_title_playing_detail);
        tvCurrentDetail = findViewById(R.id.tv_current_duration_detail);
        tvDurationDetail = findViewById(R.id.tv_duration_detail);
        sbDetail = findViewById(R.id.sb_detail);

        btnRandomDetail = findViewById(R.id.btn_random_detail);
        btnLoopDetail = findViewById(R.id.btn_loop_detail);
        btnSkipPreviousDetail = findViewById(R.id.btn_skip_previous_detail);
        btnPlayPauseDetail = findViewById(R.id.btn_play_pause_detail);
        btnSkipNextDetail = findViewById(R.id.btn_skip_next_detail);
        rvDetail = findViewById(R.id.rv_detail);

        init();
    }

    private void init() {
        Library library = getIntent().getParcelableExtra("Library");

        if (library != null) {
            // list music
            listFileMusic = showOnlyMp3(library.getPath());

            // seek bar
            sbDetail.setProgress(0);

            // exo player
            initExoplayer();

            // recyclerview
            setRecyclerView();

            // Toolbar
            mtDetail.setTitle("");
            setSupportActionBar(mtDetail);
            tvToolbarTitleDetail.setText(library.getTitle());

            // button listener
            btnRandomDetail.setOnClickListener(this);
            btnLoopDetail.setOnClickListener(this);
            btnPlayPauseDetail.setOnClickListener(this);
            btnSkipPreviousDetail.setOnClickListener(this);
            btnSkipNextDetail.setOnClickListener(this);
        }
    }

    private void initExoplayer() {
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        TrackSelector trackSelector = new DefaultTrackSelector();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(defaultRenderersFactory, trackSelector);

        // prepare playlist
        preparePlaylist();

        // set duration, current duration and seek bar
        simpleExoPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    setButtonPlay();
                } else {
                    setButtonPause();
                }

                if (playbackState == Player.STATE_READY) {
                    long duration = simpleExoPlayer.getDuration();
                    tvDurationDetail.setText(convertTime(duration));

                    handler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            // current duration
                            long curDuration = simpleExoPlayer.getCurrentPosition();
                            tvCurrentDetail.setText(convertTime(curDuration));

                            // seek bar
                            long progress = (simpleExoPlayer.getCurrentPosition() * 100) / simpleExoPlayer.getDuration();
                            sbDetail.setProgress((int) progress);

                            handler.postDelayed(runnable, 1000);
                        }
                    };

                    handler.postDelayed(runnable, 0);
                }
            }
        });

        // set seek bar
        sbDetail.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // from user
                if (b) {
                    long seekTo = (seekBar.getProgress() * simpleExoPlayer.getDuration()) / 100;
                    simpleExoPlayer.seekTo(seekTo);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                simpleExoPlayer.setPlayWhenReady(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                simpleExoPlayer.setPlayWhenReady(true);
            }
        });
    }

    private void setRecyclerView() {
        ArrayList<Music> musics = new ArrayList<>();

        assert listFileMusic != null;
        for (File value : listFileMusic) {
            Music music = new Music();
            music.setTitle(value.getName());
            music.setPath(value.getPath());

            musics.add(music);
        }

        MusicAdapter musicAdapter = new MusicAdapter(this, musics);
        musicAdapter.setOnSelected(this);
        rvDetail.setAdapter(musicAdapter);
    }

    @Override
    public void onSelectedItem(int position) {
        tvTitlePlayingDetail.setText(listFileMusic.get(position).getName());
        setButtonPause();

        // play music with index
        simpleExoPlayer.seekTo(position, C.TIME_UNSET);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // mengatasi lagu akan berputar bersamaan jika user pindah ke playlist lain
        reset();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        // mengatasi lagu akan berputar bersamaan jika user pindah ke playlist lain
        reset();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_random_detail:
                if (simpleExoPlayer.getShuffleModeEnabled()) {
                    // matikan shuffle
                    btnRandomDetail.setIconTintResource(R.color.strokeCardColor);
                    simpleExoPlayer.setShuffleModeEnabled(false);
                } else {
                    // aftikan shuffle
                    btnRandomDetail.setIconTintResource(R.color.white);
                    simpleExoPlayer.setShuffleModeEnabled(true);
                }
                break;

            case R.id.btn_loop_detail:
                if (simpleExoPlayer.getRepeatMode() == Player.REPEAT_MODE_ONE) {
                    // matikan repeat
                    btnLoopDetail.setIconTintResource(R.color.strokeCardColor);
                    simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                } else {
                    // aktfikan repeat
                    btnLoopDetail.setIconTintResource(R.color.white);
                    simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                }
                break;

            case R.id.btn_play_pause_detail:
                // pause, jika musik sedang berputar
                if (simpleExoPlayer.getPlayWhenReady()) {
                    simpleExoPlayer.setPlayWhenReady(false);
                } else {
                    simpleExoPlayer.setPlayWhenReady(true);
                }
                break;

            case R.id.btn_skip_previous_detail:
                int previousIndex;
                if (simpleExoPlayer.getCurrentWindowIndex() == 0) {
                    previousIndex = listFileMusic.size() - 1;
                } else {
                    previousIndex = simpleExoPlayer.getPreviousWindowIndex();
                }

                tvTitlePlayingDetail.setText(listFileMusic.get(previousIndex).getName());
                simpleExoPlayer.seekTo(previousIndex, C.TIME_UNSET);
                simpleExoPlayer.setPlayWhenReady(true);
                break;

            case R.id.btn_skip_next_detail:
                int nextIndex;
                if (simpleExoPlayer.getCurrentWindowIndex() == (listFileMusic.size() - 1)) {
                    nextIndex = 0;
                } else {
                    nextIndex = simpleExoPlayer.getNextWindowIndex();
                }

                tvTitlePlayingDetail.setText(listFileMusic.get(nextIndex).getName());
                simpleExoPlayer.seekTo(nextIndex, C.TIME_UNSET);
                simpleExoPlayer.setPlayWhenReady(true);
                break;
        }
    }

    private void setButtonPlay() {
        btnPlayPauseDetail.setIcon(getDrawable(R.drawable.ic_play_button_24)); // set button play
    }

    private void setButtonPause() {
        btnPlayPauseDetail.setIcon(getDrawable(R.drawable.ic_pause_button_24)); // set button pause
    }

    private void reset() {
        // jika exo player playing dan mengatasi bug jika kedua lagu berputar bersamaan
        if (simpleExoPlayer.getPlayWhenReady() && simpleExoPlayer.getPlaybackState() == Player.STATE_READY) {
            simpleExoPlayer.release();
            initExoplayer();
        }
    }

    private void preparePlaylist() {
        MediaSource[] playlist;
        String userAgent = Util.getUserAgent(this, getString(R.string.app_name));
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, userAgent);

        if (!listFileMusic.isEmpty()) {
            playlist = new MediaSource[listFileMusic.size()];

            for (int i = 0; i < listFileMusic.size(); i++) {
                Uri uri = Uri.parse(listFileMusic.get(i).getAbsolutePath());
                MediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(uri);

                playlist[i] = mediaSource;
            }

            ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(playlist);
            simpleExoPlayer.prepare(concatenatingMediaSource);
        }
    }

    private String convertTime(long millis) {
        long minute = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        return String.format(Locale.US, "%02d:%02d", minute, seconds);
    }

    private ArrayList<File> showOnlyMp3(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ArrayList<File> filesMp3 = new ArrayList<>();

        if (files != null) {
            for (File value : files) {
                if (value.getName().contains(".mp3")) {
                    filesMp3.add(value);
                }
            }
        }

        return filesMp3;
    }
}