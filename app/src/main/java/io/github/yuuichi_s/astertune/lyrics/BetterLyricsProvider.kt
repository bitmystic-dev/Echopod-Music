package dev.bitmystic.echopod.lyrics

import android.content.Context
import com.dd3boh.betterlyrics.BetterLyrics
import dev.bitmystic.echopod.constants.EnableBetterLyricsKey
import dev.bitmystic.echopod.utils.dataStore
import dev.bitmystic.echopod.utils.get

object BetterLyricsProvider : LyricsProvider {
    override val id = "betterlyrics"
    override val name = "BetterLyrics"

    override fun isEnabled(context: Context): Boolean =
        context.dataStore[EnableBetterLyricsKey] ?: false

    override suspend fun getLyrics(id: String, title: String, artist: String, duration: Int): LyricsFetchResult =
        BetterLyrics.getLyrics(title, artist, duration).toFetchResult()
}
