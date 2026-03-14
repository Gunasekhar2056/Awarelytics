package com.awarelytics.app.nudge

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.awarelytics.app.data.repository.AwarelyticsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the "Awareness, not restriction" habit interruption system.
 *
 * Upon receiving an ActionableDriftEvent:
 * 1. Triggers micro-vibration cues (two short pulses)
 * 2. Deploys a context-aware silent notification
 * 3. Uses exponential backoff to prevent notification fatigue
 */
@Singleton
class NudgeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AwarelyticsRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    // Exponential backoff state
    private var consecutiveIgnoredNudges = 0
    private var lastNudgeTimestamp = 0L
    private val baseDelayMs = 60_000L   // 1 minute base delay
    private val maxDelayMs = 30 * 60_000L // 30 minutes max delay

    // Notification ID counter
    private var notificationIdCounter = 2000

    companion object {
        private const val TAG = "NudgeManager"
        private const val NUDGE_CHANNEL_ID = "phubbing_nudge_channel"

        // Distinct haptic pattern: two short pulses (different from standard notifications)
        private val VIBRATION_PATTERN = longArrayOf(0, 100, 80, 100)
        private val VIBRATION_AMPLITUDES = intArrayOf(0, 150, 0, 150)
    }

    init {
        createNudgeNotificationChannel()
    }

    /**
     * Handle an ActionableDriftEvent.
     * Checks backoff timing before delivering the nudge.
     */
    fun handleDriftEvent(phubbingProbability: Float) {
        scope.launch {
            val now = System.currentTimeMillis()
            val backoffDelay = calculateBackoffDelay()

            // Check if we should suppress this nudge due to backoff
            if (now - lastNudgeTimestamp < backoffDelay) {
                Log.d(TAG, "Nudge suppressed by backoff. Next allowed in ${(backoffDelay - (now - lastNudgeTimestamp)) / 1000}s")
                return@launch
            }

            // Deliver the nudge
            triggerMicroVibration()
            showContextAwareNotification(phubbingProbability)

            lastNudgeTimestamp = now

            Log.d(TAG, "Nudge delivered. Probability: $phubbingProbability, Ignored streak: $consecutiveIgnoredNudges")
        }
    }

    /**
     * Micro-Vibration Cues
     *
     * Uses Android's Vibrator class with a specific haptic pattern
     * (two short pulses) that is distinct from standard notifications.
     * Acts as a physical "look up" prompt.
     */
    private fun triggerMicroVibration() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val effect = VibrationEffect.createWaveform(
                VIBRATION_PATTERN,
                VIBRATION_AMPLITUDES,
                -1 // Don't repeat
            )
            vibrator.vibrate(effect)
        }
    }

    /**
     * Context-Aware Notification
     *
     * Uses a high-priority, silent notification channel.
     * The payload is context-aware with personalized messaging.
     */
    private suspend fun showContextAwareNotification(probability: Float) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Build context-aware message
        val message = buildContextAwareMessage(probability)

        val notification = NotificationCompat.Builder(context, NUDGE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🧠 Presence Check")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSilent(true) // Silent notification — haptics handle the alert
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        notificationManager.notify(notificationIdCounter++, notification)
    }

    /**
     * Build a context-aware notification message based on current state.
     */
    private suspend fun buildContextAwareMessage(probability: Float): String {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % (24 * 60 * 60 * 1000))

        val unlocks = repository.getUnlockCount(startOfDay)
        val bleCount = repository.getLatestBleDeviceCount() ?: 0f
        val isSocial = bleCount > 1f

        return when {
            isSocial && unlocks > 5 -> "You've unlocked your phone $unlocks times while with others. Be present. 🤝"
            isSocial -> "People nearby — put the phone down and connect. 💬"
            unlocks > 10 -> "You've unlocked your phone $unlocks times today. Take a mindful pause. 🧘"
            probability > 0.9f -> "High distraction detected. Try putting your phone face-down for 5 minutes. 📱⬇️"
            else -> "Quick check: Are you using your phone intentionally right now? 🤔"
        }
    }

    /**
     * Exponential Backoff Algorithm
     *
     * Prevents notification fatigue by progressively increasing the delay
     * between nudges when the user ignores them.
     *
     * Formula: delay = baseDelay * 2^(consecutiveIgnored), capped at maxDelay
     */
    private fun calculateBackoffDelay(): Long {
        val delay = baseDelayMs * (1L shl consecutiveIgnoredNudges.coerceAtMost(5))
        return delay.coerceAtMost(maxDelayMs)
    }

    /**
     * Called when the user reacts to a nudge (e.g., locks phone).
     * Resets the backoff counter.
     */
    fun onUserReacted(reaction: String) {
        when (reaction) {
            "PHONE_LOCKED" -> {
                consecutiveIgnoredNudges = 0
                Log.d(TAG, "User locked phone — backoff reset")
            }
            "IGNORED" -> {
                consecutiveIgnoredNudges++
                Log.d(TAG, "User ignored nudge — backoff level: $consecutiveIgnoredNudges")
            }
        }
    }

    /**
     * Create the nudge notification channel.
     * High priority but silent — haptics provide the primary cue.
     */
    private fun createNudgeNotificationChannel() {
        val channel = NotificationChannel(
            NUDGE_CHANNEL_ID,
            "Presence Nudges",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Gentle reminders to be present in social situations"
            enableVibration(false) // We handle vibration manually
            setShowBadge(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
