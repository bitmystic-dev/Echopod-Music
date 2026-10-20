package dev.bitmystic.echopod.lyrics

import android.content.Context
import dev.bitmystic.echopod.constants.EnableKugouKey
import dev.bitmystic.echopod.utils.dataStore
import dev.bitmystic.echopod.utils.get
import com.zionhuang.kugou.KuGou

object KuGouLyricsProvider : LyricsProvider {
    override val id = "kugou"
    override val name = "Kugou"
    override fun isEnabled(context: Context): Boolean =
        context.dataStore[EnableKugouKey] ?: true

    override suspend fun getLyrics(id: String, title: String, artist: String, duration: Int): LyricsFetchResult =
        KuGou.getLyrics(title, artist, duration).toFetchResult()

    override suspend fun getAllLyrics(id: String, title: String, artist: String, duration: Int, callback: (String) -> Unit) {
        KuGou.getAllPossibleLyricsOptions(title, artist, duration, callback)
    }
}
