package dev.bitmystic.echopod.utils.scanners

import dev.bitmystic.echopod.db.entities.ArtistEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime

/** How [mergedArtistUpdate] rewrites the surviving artist when same-name local artists are merged. */
class MergedArtistUpdateTest {

    private val earlier = LocalDateTime.of(2026, 1, 1, 0, 0)
    private val later = LocalDateTime.of(2026, 6, 1, 0, 0)

    private fun artist(id: String, thumbnailUrl: String? = null, bookmarkedAt: LocalDateTime? = null) =
        ArtistEntity(id = id, name = "artist", thumbnailUrl = thumbnailUrl, bookmarkedAt = bookmarkedAt, isLocal = true)

    @Test
    fun takesImageFromDuplicateWhenOnlyTheImageChanges() {
        val target = artist("survivor", bookmarkedAt = earlier)
        val result = mergedArtistUpdate(target, listOf(artist("dup", thumbnailUrl = "/storage/a.flac")))
        assertEquals(target.copy(thumbnailUrl = "/storage/a.flac"), result)
    }

    @Test
    fun keepsSurvivorImage() {
        val target = artist("survivor", thumbnailUrl = "/storage/own.flac")
        val result = mergedArtistUpdate(target, listOf(artist("dup", thumbnailUrl = "/storage/a.flac", bookmarkedAt = earlier)))
        assertEquals(target.copy(bookmarkedAt = earlier), result)
    }

    @Test
    fun keepsEarliestBookmarkTime() {
        val target = artist("survivor", bookmarkedAt = later)
        val result = mergedArtistUpdate(target, listOf(artist("dup", bookmarkedAt = earlier)))
        assertEquals(target.copy(bookmarkedAt = earlier), result)
    }

    @Test
    fun returnsNullWhenNothingChanges() {
        val target = artist("survivor", thumbnailUrl = "/storage/own.flac", bookmarkedAt = earlier)
        assertNull(mergedArtistUpdate(target, listOf(artist("dup", bookmarkedAt = later))))
    }

    @Test
    fun returnsNullWhenNoArtistHasImageOrBookmark() {
        assertNull(mergedArtistUpdate(artist("survivor"), listOf(artist("dup"))))
    }
}
