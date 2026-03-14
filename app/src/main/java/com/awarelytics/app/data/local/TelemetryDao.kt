package com.awarelytics.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: TelemetryLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<TelemetryLog>)

    @Query("SELECT * FROM telemetry_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<TelemetryLog>>

    @Query("SELECT * FROM telemetry_logs WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getLogsSince(since: Long): Flow<List<TelemetryLog>>

    /** Count unlocks in a rolling window */
    @Query("SELECT COUNT(*) FROM telemetry_logs WHERE event_type = 'UNLOCK' AND timestamp >= :since")
    suspend fun countUnlocksSince(since: Long): Int

    /** Average session length in a rolling window */
    @Query("SELECT AVG(value) FROM telemetry_logs WHERE event_type = 'SESSION_END' AND timestamp >= :since")
    suspend fun avgSessionLengthSince(since: Long): Float?

    /** Count app switches in a rolling window */
    @Query("SELECT COUNT(*) FROM telemetry_logs WHERE event_type = 'APP_SWITCH' AND timestamp >= :since")
    suspend fun countAppSwitchesSince(since: Long): Int

    /** Get the latest BLE scan device count */
    @Query("SELECT value FROM telemetry_logs WHERE event_type = 'BLE_SCAN' ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestBleDeviceCount(): Float?

    /** Count recent notifications */
    @Query("SELECT COUNT(*) FROM telemetry_logs WHERE event_type = 'NOTIFICATION' AND timestamp >= :since")
    suspend fun countNotificationsSince(since: Long): Int

    /** Get logs for a specific event type within a time range */
    @Query("SELECT * FROM telemetry_logs WHERE event_type = :eventType AND timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    suspend fun getLogsByTypeInRange(eventType: String, start: Long, end: Long): List<TelemetryLog>

    /** Get all logs within a date range (for weekly aggregation) */
    @Query("SELECT * FROM telemetry_logs WHERE timestamp BETWEEN :start AND :end")
    suspend fun getLogsInRange(start: Long, end: Long): List<TelemetryLog>

    /** Get total count of logs */
    @Query("SELECT COUNT(*) FROM telemetry_logs")
    suspend fun getTotalLogCount(): Int

    @Query("DELETE FROM telemetry_logs WHERE timestamp < :before")
    suspend fun deleteLogsBefore(before: Long)
}
