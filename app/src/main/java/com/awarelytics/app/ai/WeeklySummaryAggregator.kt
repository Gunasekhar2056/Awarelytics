package com.awarelytics.app.ai

import com.awarelytics.app.data.local.TelemetryLog
import com.awarelytics.app.data.repository.AwarelyticsRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Aggregates Room DB data into an anonymized JSON summary at the end of the week.
 *
 * Privacy guarantees:
 * - No raw timestamps are included (only time categories)
 * - No specific app names (only categories like "SOCIAL_APP", "GAME")
 * - All data is bucketed into aggregated counts and averages
 */
@Singleton
class WeeklySummaryAggregator @Inject constructor(
    private val repository: AwarelyticsRepository
) {
    /**
     * Generate an anonymized weekly summary as a Map (for JSON conversion).
     * Called at the end of each week.
     */
    suspend fun generateWeeklySummary(): Map<String, Any> {
        val now = System.currentTimeMillis()
        val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000L)

        // Get all telemetry logs from past week
        val logs = repository.getTelemetryLogsInRange(oneWeekAgo, now)

        // Get drift event data
        val driftEventCount = repository.getDriftEventCountInRange(oneWeekAgo, now)
        val reactionBreakdown = repository.getReactionBreakdown(oneWeekAgo, now)

        // Aggregate by category (anonymized — no specific app names)
        val categoryBreakdown = logs
            .groupBy { it.category }
            .mapValues { (_, events) -> events.size }

        // Time-of-day distribution of phubbing events
        val timeDistribution = calculateTimeDistribution(
            logs.filter { it.event_type == "APP_SWITCH" || it.event_type == "SHORT_BURST" }
        )

        // Find primary trigger category
        val primaryTrigger = categoryBreakdown
            .maxByOrNull { it.value }
            ?.key ?: "UNKNOWN"

        // Calculate total unlocks, sessions, and averages
        val totalUnlocks = logs.count { it.event_type == "UNLOCK" }
        val sessions = logs.filter { it.event_type == "SESSION_END" }
        val avgSessionLength = if (sessions.isNotEmpty()) {
            sessions.map { it.value.toDouble() }.average()
        } else 0.0

        val shortBursts = logs.count { it.event_type == "SHORT_BURST" }

        // Social context events
        val bleScans = logs.filter { it.event_type == "BLE_SCAN" }
        val socialContextPercentage = if (bleScans.isNotEmpty()) {
            (bleScans.count { it.value > 1f }.toFloat() / bleScans.size * 100).toInt()
        } else 0

        // Reaction breakdown for JSON
        val reactions = reactionBreakdown.associate { it.user_reaction to it.count }

        return mapOf(
            "week_start" to formatWeekLabel(oneWeekAgo),
            "week_end" to formatWeekLabel(now),
            "total_phubbing_events" to driftEventCount,
            "total_unlocks" to totalUnlocks,
            "avg_session_length_seconds" to avgSessionLength,
            "short_burst_count" to shortBursts,
            "category_breakdown" to categoryBreakdown,
            "time_distribution" to timeDistribution,
            "primary_trigger_category" to primaryTrigger,
            "social_context_percentage" to socialContextPercentage,
            "user_reactions" to reactions
        )
    }

    /**
     * Calculate time-of-day distribution (anonymized — only buckets, no timestamps).
     * Returns percentage for each time category.
     */
    private fun calculateTimeDistribution(events: List<TelemetryLog>): Map<String, Int> {
        if (events.isEmpty()) return mapOf(
            "morning_6_12" to 0,
            "afternoon_12_18" to 0,
            "evening_18_22" to 0,
            "night_22_6" to 0
        )

        val calendar = Calendar.getInstance()
        var morning = 0; var afternoon = 0; var evening = 0; var night = 0

        events.forEach { event ->
            calendar.timeInMillis = event.timestamp
            when (calendar.get(Calendar.HOUR_OF_DAY)) {
                in 6..11 -> morning++
                in 12..17 -> afternoon++
                in 18..21 -> evening++
                else -> night++
            }
        }

        val total = events.size.toFloat()
        return mapOf(
            "morning_6_12" to ((morning / total) * 100).toInt(),
            "afternoon_12_18" to ((afternoon / total) * 100).toInt(),
            "evening_18_22" to ((evening / total) * 100).toInt(),
            "night_22_6" to ((night / total) * 100).toInt()
        )
    }

    /**
     * Format a week label (e.g., "2026-W10") — no exact dates for privacy.
     */
    private fun formatWeekLabel(epochMs: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = epochMs }
        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        return "$year-W${week.toString().padStart(2, '0')}"
    }
}
