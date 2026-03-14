package com.awarelytics.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awarelytics.app.data.local.DriftEvent
import com.awarelytics.app.data.local.TelemetryLog
import com.awarelytics.app.data.repository.AwarelyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalUnlocksToday: Int = 0,
    val avgSessionLength: Float = 0f,
    val appSwitchesToday: Int = 0,
    val phubbingEventsToday: Int = 0,
    val isSocialContext: Boolean = false,
    val currentStreak: Int = 0,
    val phubbingScore: Float = 0f,
    val recentLogs: List<TelemetryLog> = emptyList(),
    val recentDriftEvents: List<DriftEvent> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AwarelyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Track the latest phubbing probability for display
    private val _phubbingProbability = MutableStateFlow(0f)
    val phubbingProbability: StateFlow<Float> = _phubbingProbability.asStateFlow()

    init {
        loadDashboardData()
        observeTelemetryLogs()
        observeDriftEvents()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startOfDay = now - (now % (24 * 60 * 60 * 1000))
            val fifteenMinAgo = now - (15 * 60 * 1000)

            val unlocks = repository.getUnlockCount(startOfDay)
            val avgSession = repository.getAvgSessionLength(startOfDay) ?: 0f
            val appSwitches = repository.getAppSwitchCount(startOfDay)
            val bleDeviceCount = repository.getLatestBleDeviceCount() ?: 0f
            val isSocial = bleDeviceCount > 1f

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                totalUnlocksToday = unlocks,
                avgSessionLength = avgSession,
                appSwitchesToday = appSwitches,
                isSocialContext = isSocial
            )
        }
    }

    private fun observeTelemetryLogs() {
        viewModelScope.launch {
            val startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000))
            repository.getTelemetryLogsSince(startOfDay).collect { logs ->
                _uiState.value = _uiState.value.copy(
                    recentLogs = logs.take(20) // Show last 20 events
                )
            }
        }
    }

    private fun observeDriftEvents() {
        viewModelScope.launch {
            val startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000))
            repository.getDriftEventsSince(startOfDay).collect { events ->
                _uiState.value = _uiState.value.copy(
                    recentDriftEvents = events,
                    phubbingEventsToday = events.size
                )
            }
        }
    }

    /**
     * Called by the ML pipeline when a new phubbing probability is calculated.
     */
    fun updatePhubbingScore(score: Float) {
        _phubbingProbability.value = score
        _uiState.value = _uiState.value.copy(phubbingScore = score)
    }

    /**
     * Refresh all dashboard data.
     */
    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadDashboardData()
    }
}
