package com.awarelytics.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Syncs low-frequency, anonymized data to Firebase Firestore.
 * Synced data: streaks, rewards, shared presence goals.
 */
@Singleton
class FirestoreSyncManager @Inject constructor() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userId: String?
        get() = auth.currentUser?.uid

    /**
     * Sync the user's current streak data to Firestore.
     */
    suspend fun syncStreakData(
        currentStreak: Int,
        longestStreak: Int,
        totalPresentDays: Int
    ): Result<Unit> {
        val uid = userId ?: return Result.failure(Exception("User not logged in"))
        return try {
            val data = mapOf(
                "current_streak" to currentStreak,
                "longest_streak" to longestStreak,
                "total_present_days" to totalPresentDays,
                "last_synced" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid)
                .collection("stats").document("streaks")
                .set(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync reward/badge data to Firestore.
     */
    suspend fun syncRewards(rewards: List<String>): Result<Unit> {
        val uid = userId ?: return Result.failure(Exception("User not logged in"))
        return try {
            val data = mapOf(
                "earned_rewards" to rewards,
                "last_synced" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid)
                .collection("stats").document("rewards")
                .set(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync weekly summary data (anonymized) to Firestore.
     */
    suspend fun syncWeeklySummary(summaryJson: Map<String, Any>): Result<Unit> {
        val uid = userId ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection("users").document(uid)
                .collection("weekly_summaries")
                .add(summaryJson)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch the user's streak data from Firestore.
     */
    suspend fun fetchStreakData(): Result<Map<String, Any>?> {
        val uid = userId ?: return Result.failure(Exception("User not logged in"))
        return try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("stats").document("streaks")
                .get()
                .await()
            Result.success(snapshot.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
