package dev.bitmystic.echopod.db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.bitmystic.echopod.db.entities.QueueEntity
import dev.bitmystic.echopod.db.entities.QueueSong
import dev.bitmystic.echopod.db.entities.QueueSongMap
import dev.bitmystic.echopod.models.MultiQueueObject
import dev.bitmystic.echopod.models.toMediaMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface QueueDao {

    // region Gets
    @Query("SELECT * from queue ORDER BY `index`")
    fun getAllQueues(): Flow<List<QueueEntity>>

    @Transaction
    @Query("SELECT song.*, queue_song_map.shuffledIndex from queue_song_map JOIN song ON queue_song_map.songId = song.id WHERE queueId = :queueId ORDER BY `index`")
    fun getQueueSongs(queueId: Long): Flow<List<QueueSong>>

    /** Last [maxQueues] non-empty stored queues, in queue order. */
    suspend fun readQueue(maxQueues: Int = Int.MAX_VALUE): List<MultiQueueObject> {
        val resultQueues = ArrayList<MultiQueueObject>()
        val queues = getAllQueues().first()

        for (queue in queues.asReversed()) {
            if (resultQueues.size >= maxQueues) break
            val shuffledSongs = getQueueSongs(queue.id).first()
            if (shuffledSongs.isEmpty()) continue
            resultQueues.add(
                MultiQueueObject(
                    id = queue.id,
                    title = queue.title,
                    queue = shuffledSongs.map {
                        val s = it.song.toMediaMetadata()
                        s.shuffleIndex = it.shuffledIndex
                        s
                    }.toMutableList(),
                    shuffled = queue.shuffled,
                    queuePos = queue.queuePos,
                    lastSongPos = queue.lastSongPos,
                    index = queue.index,
                    playlistId = queue.playlistId
                )
            )
        }

        resultQueues.reverse()
        return resultQueues
    }

    suspend fun getResumptionQueue(): MultiQueueObject? {
        val queues = getAllQueues().first()
        if (queues.isEmpty()) return null
        val q = queues.last()
        val shuffledSongs = getQueueSongs(q.id).first()
        if (shuffledSongs.isEmpty()) return null

        return MultiQueueObject(
            id = q.id,
            title = q.title,
            queue = shuffledSongs.map {
                val s = it.song.toMediaMetadata()
                s.shuffleIndex = it.shuffledIndex
                s
            }.toMutableList(),
            shuffled = q.shuffled,
            queuePos = q.queuePos,
            lastSongPos = q.lastSongPos,
            index = q.index,
            playlistId = q.playlistId
        )
    }
    // endregion

    // region Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(queue: QueueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(queueSong: QueueSongMap)
    // endregion

    // region Updates
    @Update
    fun update(queue: QueueEntity)

    /**
     * Updates the supplied queues and deletes others.
     * [keepOtherQueues] retains other queues with songs.
     */
    @Transaction
    fun updateAllQueues(queues: List<QueueEntity>, keepOtherQueues: Boolean) {
        val ids = queues.map { it.id }
        if (keepOtherQueues) deleteEmptyQueuesExcept(ids) else deleteQueuesExcept(ids)
        queues.forEach { update(it) }
    }

    // endregion

    // region Deletes
    @Delete
    fun delete(mq: QueueEntity)

    @Query("DELETE FROM queue")
    fun deleteAllQueues()

    @Query("DELETE FROM queue_song_map WHERE queueId = :id")
    fun deleteAllQueueSongs(id: Long)

    @Query("DELETE FROM queue WHERE id = :id")
    fun deleteQueue(id: Long)

    @Query("DELETE FROM queue WHERE id NOT IN (:ids) AND id NOT IN (SELECT queueId FROM queue_song_map)")
    fun deleteEmptyQueuesExcept(ids: List<Long>)

    @Query("DELETE FROM queue WHERE id NOT IN (:ids)")
    fun deleteQueuesExcept(ids: List<Long>)
    // endregion
}