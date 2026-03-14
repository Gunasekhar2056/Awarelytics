package com.awarelytics.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * telemetry_logs table
 * Stores high-frequency usage telemetry data.
 */
@Entity(tableName = "telemetry_logs")
data class TelemetryLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,         // Epoch milliseconds
    val event_type: String,      // e.g., "UNLOCK", "SESSION_END", "BLE_SCAN", "APP_SWITCH"
    val value: Float,            // e.g., session duration in seconds, BLE device count
    val category: String         // e.g., "SOCIAL_APP", "GAME", "PRODUCTIVITY", "SYSTEM"
)
