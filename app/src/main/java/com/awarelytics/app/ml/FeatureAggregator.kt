package com.awarelytics.app.ml

import com.awarelytics.app.data.repository.AwarelyticsRepository
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Aggregates feature vectors from the Room DB every 5 minutes for ML inference.
 *
 * Feature Vector (6 features):
 * [0] unlocks_past_15_mins (int)
 * [1] avg_session_length_seconds (float)
 * [2] app_switch_frequency (int)
 * [3] is_social_context (boolean / 0 or 1)
 * [4] time_of_day_category (int: 0-3)
 * [5] recent_notification_count (int)
 */
@Singleton
class FeatureAggregator @Inject constructor(
    private val repository: AwarelyticsRepository
) {

    data class FeatureVector(
        val unlocksPast15Min: Int,
        val avgSessionLengthSeconds: Float,
        val appSwitchFrequency: Int,
        val isSocialContext: Boolean,
        val timeOfDayCategory: Int,
        val recentNotificationCount: Int
    ) {
        /**
         * Convert to a float array for TFLite input tensor.
         */
        fun toFloatArray(): FloatArray {
            return floatArrayOf(
                unlocksPast15Min.toFloat(),
                avgSessionLengthSeconds,
                appSwitchFrequency.toFloat(),
                if (isSocialContext) 1f else 0f,
                timeOfDayCategory.toFloat(),
                recentNotificationCount.toFloat()
            )
        }
    }

    /**
     * Pull aggregated features from Room DB.
     * Called every 5 minutes by the ML pipeline.
     */
    suspend fun aggregateFeatures(): FeatureVector {
        val now = System.currentTimeMillis()
        val fifteenMinAgo = now - (15 * 60 * 1000L)
        val fiveMinAgo = now - (5 * 60 * 1000L)

        // Feature 1: Unlocks in the past 15 minutes
        val unlocks = repository.getUnlockCount(fifteenMinAgo)

        // Feature 2: Average session length (seconds) in past 15 minutes
        val avgSessionLength = repository.getAvgSessionLength(fifteenMinAgo) ?: 0f

        // Feature 3: App switch frequency in past 5 minutes
        val appSwitches = repository.getAppSwitchCount(fiveMinAgo)

        // Feature 4: Is the user in a social context? (BLE device count > 1)
        val bleDeviceCount = repository.getLatestBleDeviceCount() ?: 0f
        val isSocialContext = bleDeviceCount > 1f

        // Feature 5: Time of day category
        // 0 = Morning (6-12), 1 = Afternoon (12-18), 2 = Evening (18-22), 3 = Night (22-6)
        val timeCategory = getTimeOfDayCategory()

        // Feature 6: Recent notification count (past 5 minutes)
        val notificationCount = repository.getNotificationCount(fiveMinAgo)

        return FeatureVector(
            unlocksPast15Min = unlocks,
            avgSessionLengthSeconds = avgSessionLength,
            appSwitchFrequency = appSwitches,
            isSocialContext = isSocialContext,
            timeOfDayCategory = timeCategory,
            recentNotificationCount = notificationCount
        )
    }

    /**
     * Categorize current hour into time-of-day buckets.
     * 0 = Morning (6-12), 1 = Afternoon (12-18), 2 = Evening (18-22), 3 = Night (22-6)
     */
    private fun getTimeOfDayCategory(): Int {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..11 -> 0   // Morning
            in 12..17 -> 1  // Afternoon
            in 18..21 -> 2  // Evening
            else -> 3       // Night
        }
    }
}
