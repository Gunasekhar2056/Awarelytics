package com.awarelytics.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * drift_events table
 * Stores phubbing intervention events triggered by the ML model.
 */
@Entity(tableName = "drift_events")
data class DriftEvent(
    @PrimaryKey(autoGenerate = true)
    val event_id: Long = 0,
    val timestamp: Long,          // Epoch milliseconds when intervention triggered
    val ml_probability: Float,    // TFLite model output score (0.0 - 1.0)
    val user_reaction: String     // "IGNORED", "PHONE_LOCKED", "APP_OPENED"
)
