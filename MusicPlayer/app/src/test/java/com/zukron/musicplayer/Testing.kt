package com.zukron.musicplayer

import com.zukron.musicplayer.exo.ExoPlayerBuilder
import com.zukron.musicplayer.model.Library
import org.junit.Test

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 9/3/2020
 * Contact me if any issues on zukronalviandy@gmail.com
 */
class Testing {
    @Test()
    fun distinctObject() {
        var libraries = mutableListOf<Library>()
        libraries.add(Library("Yellow claw", "dsaaasd"))
        libraries.add(Library("Yellow claw", "dsaaasdzxczxcxzczxcxc"))

        libraries = libraries.distinct().toMutableList()
        libraries.add(Library("Yellow claw", "dsaaasdzxczxcxzczxcxc"))
        for (l in libraries) {
            println(l.title)
        }
    }

    @Test
    fun singletonObject() {

    }
}