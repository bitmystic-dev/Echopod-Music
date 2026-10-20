package dev.bitmystic.echopod.db.entities

/**
 * Artwork path for an album that points at a song still in the library, or the album's own
 * non-local artwork.
 */
data class AlbumThumbnail(
    val albumId: String,
    val thumbnailUrl: String?,
)
