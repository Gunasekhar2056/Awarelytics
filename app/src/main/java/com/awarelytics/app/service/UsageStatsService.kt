package com.awarelytics.app.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.awarelytics.app.data.local.TelemetryLog
import com.awarelytics.app.data.repository.AwarelyticsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Captures device usage events using UsageStatsManager and implements
 * the Short-Burst Detection sliding window algorithm.
 *
 * Events tracked:
 * - ACTIVITY_RESUMED: App comes to foreground
 * - ACTIVITY_PAUSED: App goes to background
 * - KEYGUARD_HIDDEN: Device unlocked
 */
@Singleton
class UsageStatsService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AwarelyticsRepository
) {
    private val usageStatsManager: UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private val scope = CoroutineScope(Dispatchers.IO)

    // Track recent app opens for short-burst detection
    data class AppOpenEvent(
        val packageName: String,
        val timestamp: Long,
        val durationMs: Long
    )

    /**
     * Query usage events for the given time range and log them.
     * Called periodically by WorkManager or a foreground service.
     */
    fun queryAndLogEvents(startTime: Long, endTime: Long) {
        scope.launch {
            val events = usageStatsManager.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()

            val resumedApps = mutableMapOf<String, Long>() // packageName -> resumeTimestamp
            val appOpenEvents = mutableListOf<AppOpenEvent>()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)

                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        // App came to foreground
                        resumedApps[event.packageName] = event.timeStamp

                        repository.logTelemetry(
                            TelemetryLog(
                                timestamp = event.timeStamp,
                                event_type = "APP_SWITCH",
                                value = 1f,
                                category = categorizeApp(event.packageName)
                            )
                        )
                    }

                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        // App went to background — calculate session duration
                        val resumeTime = resumedApps.remove(event.packageName)
                        if (resumeTime != null) {
                            val durationSec = (event.timeStamp - resumeTime) / 1000f

                            repository.logTelemetry(
                                TelemetryLog(
                                    timestamp = event.timeStamp,
                                    event_type = "SESSION_END",
                                    value = durationSec,
                                    category = categorizeApp(event.packageName)
                                )
                            )

                            // Track for short-burst detection
                            appOpenEvents.add(
                                AppOpenEvent(
                                    packageName = event.packageName,
                                    timestamp = resumeTime,
                                    durationMs = event.timeStamp - resumeTime
                                )
                            )
                        }
                    }

                    UsageEvents.Event.KEYGUARD_HIDDEN -> {
                        // Device unlocked
                        repository.logTelemetry(
                            TelemetryLog(
                                timestamp = event.timeStamp,
                                event_type = "UNLOCK",
                                value = 1f,
                                category = "SYSTEM"
                            )
                        )
                    }
                }
            }

            // Run short-burst detection
            detectShortBursts(appOpenEvents)
        }
    }

    /**
     * Short-Burst Detection Algorithm (Sliding Window)
     *
     * Flags if the user opens >3 distinct applications for less than 15 seconds each
     * within a 2-minute window. This is the classic "boredom scroll" signature.
     *
     * Algorithm:
     * 1. Slide a 2-minute window across all app open events
     * 2. Within each window, count distinct apps opened for < 15 seconds
     * 3. If count > 3, flag as a short-burst (boredom scroll)
     */
    private suspend fun detectShortBursts(events: List<AppOpenEvent>) {
        if (events.size < 4) return

        val windowDurationMs = 2 * 60 * 1000L // 2 minutes
        val maxSessionDurationMs = 15 * 1000L  // 15 seconds threshold

        val sortedEvents = events.sortedBy { it.timestamp }

        for (i in sortedEvents.indices) {
            val windowStart = sortedEvents[i].timestamp
            val windowEnd = windowStart + windowDurationMs

            // Get all events within this 2-minute window
            val windowEvents = sortedEvents.filter { it.timestamp in windowStart..windowEnd }

            // Count distinct apps that were opened for less than 15 seconds
            val shortBurstApps = windowEvents
                .filter { it.durationMs < maxSessionDurationMs }
                .map { it.packageName }
                .distinct()

            if (shortBurstApps.size > 3) {
                // Log the short-burst detection event
                repository.logTelemetry(
                    TelemetryLog(
                        timestamp = windowStart,
                        event_type = "SHORT_BURST",
                        value = shortBurstApps.size.toFloat(),
                        category = "BEHAVIOR"
                    )
                )
                // Skip ahead to avoid duplicate detections in overlapping windows
                break
            }
        }
    }

    /**
     * Get unlock count in a rolling window (e.g., past 15 minutes).
     */
    fun getUnlockFrequency(windowMinutes: Int = 15): Int {
        val now = System.currentTimeMillis()
        val windowStart = now - (windowMinutes * 60 * 1000L)

        val events = usageStatsManager.queryEvents(windowStart, now)
        val event = UsageEvents.Event()
        var unlockCount = 0

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                unlockCount++
            }
        }
        return unlockCount
    }

    /**
     * Categorize app by package name into broad categories.
     * In production, this would use a more sophisticated lookup or ML classifier.
     */
    private fun categorizeApp(packageName: String): String {
        return when {
            packageName.contains("instagram") ||
            packageName.contains("facebook") ||
            packageName.contains("twitter") ||
            packageName.contains("whatsapp") ||
            packageName.contains("telegram") ||
            packageName.contains("snapchat") ||
            packageName.contains("tiktok") ||
            packageName.contains("messenger") -> "SOCIAL_APP"

            packageName.contains("youtube") ||
            packageName.contains("netflix") ||
            packageName.contains("spotify") ||
            packageName.contains("music") -> "ENTERTAINMENT"

            packageName.contains("game") ||
            packageName.contains("puzzle") -> "GAME"

            packageName.contains("chrome") ||
            packageName.contains("browser") ||
            packageName.contains("firefox") -> "BROWSER"

            packageName.contains("gmail") ||
            packageName.contains("outlook") ||
            packageName.contains("slack") ||
            packageName.contains("teams") -> "PRODUCTIVITY"

            else -> "OTHER"
        }
    }
}
