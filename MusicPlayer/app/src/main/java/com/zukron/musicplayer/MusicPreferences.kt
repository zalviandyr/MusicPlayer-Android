package com.zukron.musicplayer

import android.content.Context
import com.zukron.musicplayer.model.Library

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 9/3/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */

class MusicPreferences {
    companion object {
        private const val NAME = "music_player_pref"
        private const val MODE = 0 // MODE PRIVATE

        fun load(context: Context): List<Library> {
            val sharedPreferences = context.getSharedPreferences(NAME, MODE)
            val libraries = mutableListOf<Library>()
            val size = sharedPreferences.getInt("music_size", 0)

            if (size != 0) {
                for (i in 0 until size) {
                    val title = sharedPreferences.getString("music_title_$i", null)
                    val path = sharedPreferences.getString("music_path_$i", null)
                    val library = Library(title, path)

                    libraries.add(library)
                }
            }
            return libraries
        }

        fun save(libraries: List<Library>, context: Context) {
            val sharedPreferences = context.getSharedPreferences(NAME, MODE)
            val editor = sharedPreferences.edit()

            editor.putInt("music_size", libraries.size)
            for (i in libraries.indices) {
                editor.putString("music_title_$i", libraries[i].title)
                editor.putString("music_path_$i", libraries[i].path)
            }

            editor.apply()
        }
    }
}