package dev.bitmystic.echopod.viewmodels

import dev.bitmystic.echopod.db.entities.Album
import dev.bitmystic.echopod.db.entities.AlbumEntity
import dev.bitmystic.echopod.db.entities.GenreEntity
import dev.bitmystic.echopod.db.entities.Song
import dev.bitmystic.echopod.db.entities.SongEntity
import dev.bitmystic.echopod.ui.screens.artist.hoursAndMinutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Values shown on the local artist page, computed from the artist's songs and albums.
 */
class LocalArtistSummaryTest {

    private fun song(
        id: String,
        year: Int? = null,
        duration: Int = 0,
        albumId: String? = null,
        mappedAlbumId: String? = null,
        genres: List<String> = emptyList(),
    ) = Song(
        song = SongEntity(id = id, title = id, duration = duration, localPath = null, albumId = albumId, year = year),
        artists = emptyList(),
        album = mappedAlbumId?.let { AlbumEntity(id = it, title = it, songCount = 0, duration = 0) },
        genre = genres.map { GenreEntity(id = it, title = it) },
    )

    private fun album(id: String, year: Int? = null) =
        Album(album = AlbumEntity(id = id, title = id, year = year, songCount = 0, duration = 0), downloadCount = 0, artists = emptyList())

    @Test
    fun latestYear_ignoresSongsWithoutYear() {
        val songs = listOf(song("a", year = 2024), song("b"), song("c", year = 2026))
        assertEquals(2026, localArtistLatestYear(songs))
    }

    @Test
    fun latestYear_isNullWhenNoSongHasYear() {
        assertNull(localArtistLatestYear(listOf(song("a"), song("b"))))
    }

    @Test
    fun totalSeconds_countsUnknownLengthAsZero() {
        val songs = listOf(song("a", duration = 120), song("b", duration = -1), song("c", duration = 60))
        assertEquals(180, localArtistTotalSeconds(songs))
    }

    @Test
    fun hoursAndMinutes_exactlyOneHour() {
        assertEquals(1 to 0, hoursAndMinutes(3600))
    }

    @Test
    fun hoursAndMinutes_underOneHour() {
        assertEquals(0 to 59, hoursAndMinutes(3599))
    }

    @Test
    fun genres_mostSongsFirstAndCountedOncePerSong() {
        val songs = listOf(
            song("a", genres = listOf("Rock", "Rock")),
            song("b", genres = listOf("Pop")),
            song("c", genres = listOf("Pop")),
        )
        assertEquals(listOf("Pop", "Rock"), localArtistGenres(songs))
    }

    @Test
    fun albums_newestFirstAndWithoutYearLast() {
        val albums = listOf(album("none"), album("old", year = 2020), album("new", year = 2025))
        assertEquals(listOf("new", "old", "none"), localArtistAlbums(albums, emptyList()).map { it.album.id })
    }

    @Test
    fun albums_ownYearWinsOverSongYears() {
        val result = localArtistAlbums(listOf(album("x", year = 2020)), listOf(song("a", year = 2025, albumId = "x")))
        assertEquals(2020, result.single().year)
    }

    @Test
    fun albums_yearFromSongAlbumIdOnly() {
        val songs = listOf(song("a", year = 2023, albumId = "x"), song("b", year = 2024, albumId = "x"))
        assertEquals(2024, localArtistAlbums(listOf(album("x")), songs).single().year)
    }

    @Test
    fun albums_yearFromSongAlbumMapOnly() {
        val songs = listOf(song("a", year = 2022, mappedAlbumId = "x"))
        assertEquals(2022, localArtistAlbums(listOf(album("x")), songs).single().year)
    }

    @Test
    fun albums_songsOfOtherAlbumsAreIgnored() {
        val songs = listOf(song("a", year = 2030, albumId = "y"), song("b", year = 2021, albumId = "x"))
        assertEquals(2021, localArtistAlbums(listOf(album("x")), songs).single().year)
    }
}
