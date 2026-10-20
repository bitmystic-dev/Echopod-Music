package dev.bitmystic.echopod.lyrics

import android.content.Context
import com.dd3boh.lrclib.LrcLib
import dev.bitmystic.echopod.constants.EnableLrcLibKey
import dev.bitmystic.echopod.utils.dataStore
import dev.bitmystic.echopod.utils.get

/**
 * Source: https://github.com/Malopieds/InnerTune
 */
object LrcLibLyricsProvider : LyricsProvider {
    override val id = "lrclib"
    override val name = "LrcLib"

    override fun isEnabled(context: Context): Boolean =
        context.dataStore[EnableLrcLibKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
    ): LyricsFetchResult = LrcLib.getLyrics(title, artist, duration).toFetchResult()

    override suspend fun getAllLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        callback: (String) -> Unit,
    ) {
        LrcLib.getAllLyrics(title, artist, duration, null, callback)
    }
}
