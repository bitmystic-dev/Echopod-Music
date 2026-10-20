package dev.bitmystic.echopod.db

import dev.bitmystic.echopod.db.entities.SongEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

/** Tests that [SongEntity.toggleLike] toggles local likes without changing library membership. */
class SongEntityLikeTest {
    @Test
    fun toggleLike_localSongTogglesLikeOnly() {
        val inLibrary = LocalDateTime.of(2026, 1, 1, 0, 0)
        val song = SongEntity(id = "LSsong", title = "song", isLocal = true, inLibrary = inLibrary, localPath = "/storage/music/song.flac")

        val liked = song.toggleLike()
        assertTrue(liked.liked)
        assertNotNull(liked.likedDate)
        assertEquals(inLibrary, liked.inLibrary)

        val unliked = liked.toggleLike()
        assertFalse(unliked.liked)
        assertNull(unliked.likedDate)
    }

    @Test
    fun toggleLike_localSongOutsideLibraryStaysOutside() {
        val song = SongEntity(id = "LSsong", title = "song", isLocal = true, inLibrary = null, localPath = null)
        assertNull(song.toggleLike().inLibrary)
    }
}
