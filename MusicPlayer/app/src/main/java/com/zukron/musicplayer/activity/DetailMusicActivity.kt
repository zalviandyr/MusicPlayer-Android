package com.zukron.musicplayer.activity

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.zukron.musicplayer.R
import com.zukron.musicplayer.adapter.MusicAdapter
import com.zukron.musicplayer.exo.ExoPlayerBuilder
import com.zukron.musicplayer.model.Library
import com.zukron.musicplayer.model.Music
import kotlinx.android.synthetic.main.activity_detail_music.*
import java.io.File
import java.lang.Runnable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 9/3/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */
class DetailMusicActivity : AppCompatActivity(), View.OnClickListener, Player.EventListener, MusicAdapter.OnSelected, SeekBar.OnSeekBarChangeListener {
    companion object {
        const val EXTRA_LIBRARY = "extra_library"
    }

    private val filterMp3: (path: String) -> MutableList<File> = {
        val files = File(it).listFiles()
        val filesMp3 = mutableListOf<File>()

        if (files != null) {
            for (value in files) {
                if (value.name.contains(".mp3")) {
                    filesMp3.add(value)
                }
            }
        }
        filesMp3
    }
    private var listFileMusic = mutableListOf<File>()
    private val musicAdapter = MusicAdapter(this)
    private fun Long.convertTime(): String {
        val minute = TimeUnit.MILLISECONDS.toMinutes(this)
        val second = TimeUnit.MILLISECONDS.toSeconds(this) % 60

        return String.format(Locale.US, "%02d:%02d", minute, second)
    }

    private var simpleExoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_music)

        val library = intent.getParcelableExtra<Library>(EXTRA_LIBRARY)

        if (library != null) {
            listFileMusic = library.path?.let {
                filterMp3(it)
            }!!

            // recycler view
            musicAdapter.musics = listFileMusic.map {
                Music(it.name, it.absolutePath)
            }.toMutableList()

            recycler_view.adapter = musicAdapter

            // init exo player
            simpleExoPlayer = ExoPlayerBuilder.getInstance(this)

            // prepare playlist
            preparePlaylist()

            // init seek bar
            seek_bar.progress = 0

            // init toolbar
            material_toolbar.title = ""
            setSupportActionBar(material_toolbar)
            tv_toolbar_title.text = library.title

            // music adapter listener
            musicAdapter.onSelected = this

            // exo player listener
            simpleExoPlayer!!.addListener(this)

            seek_bar.setOnSeekBarChangeListener(this)

            // button listener
            btn_random.setOnClickListener(this)
            btn_loop.setOnClickListener(this)
            btn_play_pause.setOnClickListener(this)
            btn_skip_previous.setOnClickListener(this)
            btn_skip_next.setOnClickListener(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_random -> {
                if (simpleExoPlayer!!.shuffleModeEnabled) {
                    // turn off shuffle
                    btn_random.setIconTintResource(R.color.strokeCardColor)
                    simpleExoPlayer!!.shuffleModeEnabled = false
                } else {
                    // turn on shuffle
                    btn_random.setIconTintResource(R.color.white)
                    simpleExoPlayer!!.shuffleModeEnabled = true
                }
            }

            R.id.btn_loop -> {
                if (simpleExoPlayer!!.repeatMode == Player.REPEAT_MODE_ONE) {
                    // turn off repeat
                    btn_loop.setIconTintResource(R.color.strokeCardColor)
                    simpleExoPlayer!!.repeatMode = Player.REPEAT_MODE_OFF
                } else {
                    // turn on repeat
                    btn_loop.setIconTintResource(R.color.white)
                    simpleExoPlayer!!.repeatMode = Player.REPEAT_MODE_ONE
                }
            }

            R.id.btn_play_pause -> {
                // pause, if music playing
                simpleExoPlayer!!.playWhenReady = !simpleExoPlayer!!.playWhenReady
            }

            R.id.btn_skip_previous -> {
                val previousIndex: Int = if (simpleExoPlayer!!.currentWindowIndex == 0) {
                    listFileMusic.size - 1
                } else {
                    simpleExoPlayer!!.previousWindowIndex
                }

                tv_title_playing.text = listFileMusic[previousIndex].name
                simpleExoPlayer!!.seekTo(previousIndex, C.TIME_UNSET)
                simpleExoPlayer!!.playWhenReady = true
            }

            R.id.btn_skip_next -> {
                val nextIndex: Int = if (simpleExoPlayer!!.currentWindowIndex == (listFileMusic.size - 1)) {
                    0
                } else {
                    simpleExoPlayer!!.nextWindowIndex
                }

                tv_title_playing.text = listFileMusic[nextIndex].name
                simpleExoPlayer!!.seekTo(nextIndex, C.TIME_UNSET)
                simpleExoPlayer!!.playWhenReady = true
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        // set button icon
        setButtonIcon(playWhenReady)

        if (playbackState == Player.STATE_READY) {
            tv_title_playing.text = listFileMusic[simpleExoPlayer!!.currentWindowIndex].name
            val duration = simpleExoPlayer!!.duration
            tv_duration.text = duration.convertTime()

            val handler = Handler(simpleExoPlayer!!.applicationLooper)
            val runnable = object : Runnable {
                override fun run() {
                    val curDuration = simpleExoPlayer!!.currentPosition
                    val progress = (simpleExoPlayer!!.currentPosition * 100.0) / simpleExoPlayer!!.duration

                    tv_current_duration.text = curDuration.convertTime()
                    seek_bar.progress = progress.toInt()

                    handler.postDelayed(this, 1000)
                }
            }

            handler.postDelayed(runnable, 0)
        }
    }

    override fun onSelectedItem(position: Int) {
        setButtonIcon(true)

        // play music with index
        simpleExoPlayer!!.seekTo(position, C.TIME_UNSET)
        simpleExoPlayer!!.playWhenReady = true
    }

    override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
        // input from user
        if (b) {
            val seekTo = (seekBar!!.progress * simpleExoPlayer!!.duration) / 100
            simpleExoPlayer!!.seekTo(seekTo)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        simpleExoPlayer!!.playWhenReady = false
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        simpleExoPlayer!!.playWhenReady = true
    }

    private fun preparePlaylist() {
        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, userAgent)
        val playlist: List<MediaSource> = listFileMusic.map {
            val uri: Uri = Uri.parse(it.absolutePath)
            ProgressiveMediaSource
                    .Factory(defaultDataSourceFactory)
                    .createMediaSource(uri)

        }.toList()

        val concatenatingMediaSource = ConcatenatingMediaSource()
        concatenatingMediaSource.addMediaSources(playlist)
        simpleExoPlayer?.prepare(concatenatingMediaSource, true, true)
    }

    private fun setButtonIcon(isPlay: Boolean) {
        if (isPlay) {
            btn_play_pause.icon = ContextCompat.getDrawable(this, R.drawable.ic_pause_button_24)
        } else {
            btn_play_pause.icon = ContextCompat.getDrawable(this, R.drawable.ic_play_button_24)
        }
    }
}