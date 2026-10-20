package dev.bitmystic.echopod.db

import android.app.Application
import androidx.room.Room
import dev.bitmystic.echopod.constants.AlbumFilter
import dev.bitmystic.echopod.constants.AlbumSortType
import dev.bitmystic.echopod.constants.ArtistFilter
import dev.bitmystic.echopod.constants.ArtistSortType
import dev.bitmystic.echopod.db.entities.AlbumArtistMap
import dev.bitmystic.echopod.db.entities.AlbumEntity
import dev.bitmystic.echopod.db.entities.ArtistEntity
import dev.bitmystic.echopod.db.entities.SongAlbumMap
import dev.bitmystic.echopod.db.entities.SongArtistMap
import dev.bitmystic.echopod.db.entities.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.LocalDateTime

/**
 * Tests [MusicDatabase.refreshLocalAlbums] and artist artwork from library songs.
 * Also checks album and artist list updates when songs leave the library.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, sdk = [36])
class LocalAlbumRefreshTest {
    private lateinit var database: MusicDatabase

    private val pathP = "/storage/music/a/p.flac"
    private val pathQ = "/storage/music/a/q.flac"
    private val pathX = "/storage/music/a/x.flac"
    private val pathE = "/storage/music/e/e.flac"

    @Before
    fun setUp() {
        database = MusicDatabase(
            Room.inMemoryDatabaseBuilder(RuntimeEnvironment.getApplication(), InternalDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        )
        listOf("LAa" to "a", "LAb" to "b", "LAc" to "c").forEach { (id, name) ->
            database.insert(ArtistEntity(id = id, name = name, isLocal = true))
        }
        database.insert(ArtistEntity(id = "UCr", name = "remote artist"))

        database.insert(AlbumEntity(id = "local", title = "local", thumbnailUrl = pathP, songCount = 3, duration = 0, isLocal = true))
        database.insert(AlbumEntity(id = "empty", title = "empty", thumbnailUrl = pathE, songCount = 1, duration = 0, isLocal = true))
        database.insert(AlbumEntity(id = "remote", title = "remote", thumbnailUrl = "https://example.com/r.jpg", songCount = 1, duration = 0))

        insertSong("p", pathP, "local", inLibrary = true, "LAa", "LAb")
        insertSong("q", pathQ, "local", inLibrary = true, "LAb")
        insertSong("x", pathX, "local", inLibrary = false, "LAc")
        insertSong("e", pathE, "empty", inLibrary = false, "LAc")
        database.insert(
            SongEntity(id = "r", title = "r", thumbnailUrl = "https://example.com/r.jpg", inLibrary = LocalDateTime.now(), localPath = null, albumId = "remote")
        )
        database.insert(SongAlbumMap("r", "remote", 0))
        database.insert(SongArtistMap("r", "UCr", 0))
        database.insert(AlbumArtistMap("remote", "UCr", 0))
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun insertSong(id: String, path: String, albumId: String, inLibrary: Boolean, vararg artistIds: String) {
        database.insert(
            SongEntity(
                id = id, title = id, thumbnailUrl = path, isLocal = true,
                inLibrary = if (inLibrary) LocalDateTime.now() else null,
                localPath = if (inLibrary) path else null,
                albumId = albumId,
            )
        )
        database.insert(SongAlbumMap(id, albumId, 0))
        artistIds.forEachIndexed { index, artistId -> database.insert(SongArtistMap(id, artistId, index)) }
    }

    private fun refresh() = database.refreshLocalAlbums()

    private suspend fun albumArtists(): Map<String, Set<String>> =
        database.albums(AlbumFilter.LIBRARY, AlbumSortType.NAME, false).first()
            .associate { album -> album.id to album.artists.map { it.id }.toSet() }

    private fun thumbnail(albumId: String) = database.albumById(albumId)?.thumbnailUrl

    @Test
    fun albumArtists_linkEveryArtistOfLibrarySongs() = runBlocking {
        refresh()
        assertEquals(setOf("LAa", "LAb"), albumArtists()["local"])
    }

    @Test
    fun albumArtists_dropArtistsWhoseSongsLeftLibrary() = runBlocking {
        database.insert(AlbumArtistMap("local", "LAc", 0))
        refresh()
        assertEquals(setOf("LAa", "LAb"), albumArtists()["local"])

        database.disableLocalSong("p")
        refresh()
        assertEquals(setOf("LAb"), albumArtists()["local"])
    }

    @Test
    fun albumArtists_keepNonLocalAlbums() = runBlocking {
        refresh()
        assertEquals(setOf("UCr"), albumArtists()["remote"])
    }

    @Test
    fun albumThumbnail_keepsArtworkWhileItsSongIsInLibrary() {
        // q.flac is kept although p.flac, also a library song of the album, sorts first.
        database.update(database.albumById("local")!!.copy(thumbnailUrl = pathQ))
        refresh()
        assertEquals(pathQ, thumbnail("local"))
    }

    @Test
    fun albumTotals_recountedFromLibrarySongs() {
        // Stored totals differ from the recounted ones, so a skipped recount fails.
        database.insert(AlbumEntity(id = "timed", title = "timed", songCount = 9, duration = 999, isLocal = true))
        listOf(Triple("t1", 100, true), Triple("t2", 250, true), Triple("t3", 400, false)).forEach { (id, duration, inLibrary) ->
            val path = "/storage/music/t/$id.flac"
            database.insert(
                SongEntity(
                    id = id, title = id, duration = duration, thumbnailUrl = path, isLocal = true,
                    inLibrary = if (inLibrary) LocalDateTime.now() else null,
                    localPath = if (inLibrary) path else null,
                    albumId = "timed",
                )
            )
            database.insert(SongAlbumMap(id, "timed", 0))
        }

        refresh()
        val before = database.albumById("timed")!!
        assertEquals(2, before.songCount)
        assertEquals(350, before.duration)

        database.disableLocalSong("t2")
        refresh()
        val after = database.albumById("timed")!!
        assertEquals(1, after.songCount)
        assertEquals(100, after.duration)
    }

    @Test
    fun albumThumbnail_usesRemainingSongWhenArtworkSongLeavesLibrary() {
        database.disableLocalSong("p")
        refresh()
        assertEquals(pathQ, thumbnail("local"))
    }

    @Test
    fun albumThumbnail_clearedWhenNoSongIsInLibrary() {
        refresh()
        assertNull(thumbnail("empty"))
    }

    @Test
    fun albumThumbnail_keepsNonLocalArtwork() {
        refresh()
        assertEquals("https://example.com/r.jpg", thumbnail("remote"))
    }

    @Test
    fun artistThumbnail_ignoresSongsOutsideLibrary() = runBlocking {
        // Without an album, the artist's artwork comes from its songs; x.flac sorts before z.flac.
        database.insert(ArtistEntity(id = "LAd", name = "d", isLocal = true))
        database.insert(SongEntity(id = "y", title = "y", thumbnailUrl = "/storage/music/d/x.flac", isLocal = true, localPath = null))
        database.insert(SongArtistMap("y", "LAd", 0))
        database.insert(
            SongEntity(
                id = "z", title = "z", thumbnailUrl = "/storage/music/d/z.flac", isLocal = true,
                inLibrary = LocalDateTime.now(), localPath = "/storage/music/d/z.flac",
            )
        )
        database.insert(SongArtistMap("z", "LAd", 0))

        val thumbnails = database.localArtistThumbnails().first().associate { it.artistId to it.thumbnailUrl }
        assertEquals("/storage/music/d/z.flac", thumbnails["LAd"])
        assertNull(thumbnails["LAc"])
    }

    @Test
    fun localArtists_updateWhenSongLeavesLibrary() = runBlocking {
        val songCounts = Channel<Int?>(Channel.UNLIMITED)
        val job = launch(Dispatchers.Default) {
            database.artists(ArtistFilter.LIBRARY, ArtistSortType.NAME, false, localOnly = true).collect { artists ->
                songCounts.send(artists.find { it.id == "LAb" }?.songCount)
            }
        }
        assertEquals(2, songCounts.receive())

        database.disableLocalSong("q")
        withTimeout(5_000) {
            while (songCounts.receive() != 1) Unit
        }
        job.cancel()
    }

    @Test
    fun albums_updateWhenSongsLeaveLibrary() = runBlocking {
        val albumIds = Channel<Set<String>>(Channel.UNLIMITED)
        val job = launch(Dispatchers.Default) {
            database.albums(AlbumFilter.LIBRARY, AlbumSortType.NAME, false).collect { albums ->
                albumIds.send(albums.map { it.id }.toSet())
            }
        }
        assertEquals(setOf("local", "remote"), albumIds.receive())

        database.disableLocalSong("p")
        database.disableLocalSong("q")
        withTimeout(5_000) {
            while (albumIds.receive() != setOf("remote")) Unit
        }
        job.cancel()
    }
}
