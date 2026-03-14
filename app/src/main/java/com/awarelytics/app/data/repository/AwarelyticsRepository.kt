package com.awarelytics.app.data.repository

import com.awarelytics.app.data.local.DriftEvent
import com.awarelytics.app.data.local.DriftEventDao
import com.awarelytics.app.data.local.TelemetryDao
import com.awarelytics.app.data.local.TelemetryLog
import com.awarelytics.app.data.remote.FirebaseAuthManager
import com.awarelytics.app.data.remote.FirestoreSyncManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single repository connecting Room (local) + Firestore (cloud).
 * Acts as the single source of truth for all app data.
 */
@Singleton
class AwarelyticsRepository @Inject constructor(
    private val telemetryDao: TelemetryDao,
    private val driftEventDao: DriftEventDao,
    private val authManager: FirebaseAuthManager,
    private val firestoreSync: FirestoreSyncManager
) {
    // ─── Authentication ───────────────────────────────────────────────

    val currentUser: FirebaseUser?
        get() = authManager.currentUser

    val isLoggedIn: Boolean
        get() = authManager.isLoggedIn

    suspend fun signUp(email: String, password: String) = authManager.signUpWithEmail(email, password)

    suspend fun signIn(email: String, password: String) = authManager.signInWithEmail(email, password)

    suspend fun signInWithGoogle(idToken: String) = authManager.signInWithGoogleCredential(idToken)

    fun signOut() = authManager.signOut()

    // ─── Telemetry Logs ───────────────────────────────────────────────

    suspend fun logTelemetry(log: TelemetryLog) = telemetryDao.insert(log)

    suspend fun logTelemetryBatch(logs: List<TelemetryLog>) = telemetryDao.insertAll(logs)

    fun getAllTelemetryLogs(): Flow<List<TelemetryLog>> = telemetryDao.getAllLogs()

    fun getTelemetryLogsSince(since: Long): Flow<List<TelemetryLog>> = telemetryDao.getLogsSince(since)

    // ─── ML Feature Queries ───────────────────────────────────────────

    suspend fun getUnlockCount(since: Long) = telemetryDao.countUnlocksSince(since)

    suspend fun getAvgSessionLength(since: Long) = telemetryDao.avgSessionLengthSince(since)

    suspend fun getAppSwitchCount(since: Long) = telemetryDao.countAppSwitchesSince(since)

    suspend fun getLatestBleDeviceCount() = telemetryDao.getLatestBleDeviceCount()

    suspend fun getNotificationCount(since: Long) = telemetryDao.countNotificationsSince(since)

    // ─── Drift Events ─────────────────────────────────────────────────

    suspend fun logDriftEvent(event: DriftEvent) = driftEventDao.insert(event)

    suspend fun updateDriftEvent(event: DriftEvent) = driftEventDao.update(event)

    fun getAllDriftEvents(): Flow<List<DriftEvent>> = driftEventDao.getAllEvents()

    fun getDriftEventsSince(since: Long): Flow<List<DriftEvent>> = driftEventDao.getEventsSince(since)

    suspend fun getLastDriftEvent() = driftEventDao.getLastEvent()

    suspend fun countIgnoredNudges(since: Long) = driftEventDao.countIgnoredSince(since)

    // ─── Weekly Aggregation ───────────────────────────────────────────

    suspend fun getTelemetryLogsInRange(start: Long, end: Long) = telemetryDao.getLogsInRange(start, end)

    suspend fun getDriftEventCountInRange(start: Long, end: Long) = driftEventDao.countEventsInRange(start, end)

    suspend fun getReactionBreakdown(start: Long, end: Long) = driftEventDao.getReactionBreakdown(start, end)

    // ─── Cloud Sync ───────────────────────────────────────────────────

    suspend fun syncStreaks(current: Int, longest: Int, totalDays: Int) =
        firestoreSync.syncStreakData(current, longest, totalDays)

    suspend fun syncRewards(rewards: List<String>) =
        firestoreSync.syncRewards(rewards)

    suspend fun syncWeeklySummary(summary: Map<String, Any>) =
        firestoreSync.syncWeeklySummary(summary)

    // ─── Cleanup ──────────────────────────────────────────────────────

    suspend fun cleanupOldData(before: Long) {
        telemetryDao.deleteLogsBefore(before)
        driftEventDao.deleteEventsBefore(before)
    }
}
