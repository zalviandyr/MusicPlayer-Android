package com.zukron.musicplayer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Project name is Music Player
 * Created by Zukron Alviandy R on 7/27/2020
 */
@Parcelize
data class Library(
        val title: String?,
        val path: String?
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        other as Library
        return this.title == other.title
    }

    override fun hashCode(): Int {
        return this.title.hashCode()
    }
}
