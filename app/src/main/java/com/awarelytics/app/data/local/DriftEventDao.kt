package com.awarelytics.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DriftEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: DriftEvent)

    @Update
    suspend fun update(event: DriftEvent)

    @Query("SELECT * FROM drift_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<DriftEvent>>

    @Query("SELECT * FROM drift_events WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getEventsSince(since: Long): Flow<List<DriftEvent>>

    /** Count drift events in a time range (for weekly summary) */
    @Query("SELECT COUNT(*) FROM drift_events WHERE timestamp BETWEEN :start AND :end")
    suspend fun countEventsInRange(start: Long, end: Long): Int

    /** Get average ML probability for drift events */
    @Query("SELECT AVG(ml_probability) FROM drift_events WHERE timestamp BETWEEN :start AND :end")
    suspend fun avgProbabilityInRange(start: Long, end: Long): Float?

    /** Get the last drift event for backoff calculation */
    @Query("SELECT * FROM drift_events ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastEvent(): DriftEvent?

    /** Count how many consecutive events were ignored */
    @Query("SELECT COUNT(*) FROM drift_events WHERE user_reaction = 'IGNORED' AND timestamp >= :since")
    suspend fun countIgnoredSince(since: Long): Int

    /** Get events for weekly summary with reaction breakdown */
    @Query("SELECT user_reaction, COUNT(*) as count FROM drift_events WHERE timestamp BETWEEN :start AND :end GROUP BY user_reaction")
    suspend fun getReactionBreakdown(start: Long, end: Long): List<ReactionCount>

    @Query("DELETE FROM drift_events WHERE timestamp < :before")
    suspend fun deleteEventsBefore(before: Long)
}

/** Helper data class for reaction count aggregation */
data class ReactionCount(
    val user_reaction: String,
    val count: Int
)
