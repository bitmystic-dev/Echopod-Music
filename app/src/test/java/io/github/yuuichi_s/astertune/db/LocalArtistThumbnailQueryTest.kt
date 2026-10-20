package dev.bitmystic.echopod.db

import android.app.Application
import androidx.room.Room
import dev.bitmystic.echopod.db.entities.AlbumEntity
import dev.bitmystic.echopod.db.entities.ArtistEntity
import dev.bitmystic.echopod.db.entities.SongAlbumMap
import dev.bitmystic.echopod.db.entities.SongArtistMap
import dev.bitmystic.echopod.db.entities.SongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.LocalDateTime

/**
 * Artwork queries for local artists when the song an album's artwork came from leaves the library
 * while other songs of the album stay.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, sdk = [36])
class LocalArtistThumbnailQueryTest {
    private lateinit var database: MusicDatabase

    private val pathP = "/storage/music/a/p.flac"
    private val pathQ = "/storage/music/a/q.flac"
    private val pathR = "/storage/music/b/r.flac"

    @Before
    fun setUp() {
        database = MusicDatabase(
            Room.inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), InternalDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        )
        database.insert(ArtistEntity(id = "LAartist", name = "artist", isLocal = true))
        listOf("p" to pathP, "q" to pathQ, "r" to pathR).forEach { (id, path) ->
            database.insert(
                SongEntity(
                    id = id, title = id, thumbnailUrl = path, isLocal = true,
                    inLibrary = LocalDateTime.now(), localPath = path,
                    albumId = if (id == "r") "remote" else "local",
                )
            )
            database.insert(SongArtistMap(id, "LAartist", 0))
        }
        database.insert(AlbumEntity(id = "local", title = "local", thumbnailUrl = pathP, songCount = 2, duration = 0, isLocal = true))
        database.insert(AlbumEntity(id = "remote", title = "remote", thumbnailUrl = "https://example.com/r.jpg", songCount = 1, duration = 0))
        database.insert(SongAlbumMap("p", "local", 0))
        database.insert(SongAlbumMap("q", "local", 1))
        database.insert(SongAlbumMap("r", "remote", 0))
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun albumThumbnails() =
        database.artistAlbumThumbnails("LAartist").first().associate { it.albumId to it.thumbnailUrl }

    private fun setArtistThumbnail(url: String?) = runBlocking {
        val artist = database.artistById("LAartist")!!
        database.update(artist.copy(thumbnailUrl = url))
    }

    @Test
    fun albumThumbnail_keepsOwnArtworkWhileItsSongIsInLibrary() = runBlocking {
        assertEquals(pathP, albumThumbnails()["local"])
    }

    @Test
    fun albumThumbnail_usesRemainingSongWhenArtworkSongLeavesLibrary() = runBlocking {
        database.disableLocalSong("p")
        assertEquals(pathQ, albumThumbnails()["local"])
    }

    @Test
    fun albumThumbnail_keepsNonLocalArtwork() = runBlocking {
        assertEquals("https://example.com/r.jpg", albumThumbnails()["remote"])
    }

    @Test
    fun isLibrarySongPath_followsLibraryMembership() = runBlocking {
        assertTrue(database.isLibrarySongPath(pathP).first())
        database.disableLocalSong("p")
        assertFalse(database.isLibrarySongPath(pathP).first())
    }

    @Test
    fun usableArtistThumbnail_dropsArtworkWhoseSongLeftLibrary() = runBlocking {
        setArtistThumbnail(pathP)
        assertEquals(listOf("LAartist"), database.localArtistsWithUsableThumbnail().first())
        database.disableLocalSong("p")
        assertEquals(emptyList<String>(), database.localArtistsWithUsableThumbnail().first())
    }

    @Test
    fun usableArtistThumbnail_keepsNonLocalArtworkAndSkipsNone() = runBlocking {
        setArtistThumbnail("https://example.com/a.jpg")
        assertEquals(listOf("LAartist"), database.localArtistsWithUsableThumbnail().first())
        setArtistThumbnail(null)
        assertEquals(emptyList<String>(), database.localArtistsWithUsableThumbnail().first())
    }
}
