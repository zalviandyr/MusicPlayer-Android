package com.zukron.musicplayer.exo

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 9/3/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */

object ExoPlayerBuilder {
    private var simpleExoPlayer: SimpleExoPlayer? = null

    fun getInstance(context: Context): SimpleExoPlayer {
        simpleExoPlayer
                ?: SimpleExoPlayer.Builder(context).also { simpleExoPlayer = it.build() }
        return simpleExoPlayer!!
    }
}
