package dev.bitmystic.echopod.lyrics

import android.content.Context
import dev.bitmystic.echopod.constants.EnableSimpMusicKey
import dev.bitmystic.echopod.utils.dataStore
import dev.bitmystic.echopod.utils.get
import com.dd3boh.simpmusic.SimpMusicLyrics

object SimpMusicLyricsProvider : LyricsProvider {
    override val id = "simpmusic"
    override val name = "SimpMusic"

    override fun isEnabled(context: Context): Boolean =
        context.dataStore[EnableSimpMusicKey] ?: true

    override suspend fun getLyrics(id: String, title: String, artist: String, duration: Int): LyricsFetchResult =
        SimpMusicLyrics.getLyrics(id, duration).toFetchResult()

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        SimpMusicLyrics.getAllLyrics(id, duration, callback)
    }
}
