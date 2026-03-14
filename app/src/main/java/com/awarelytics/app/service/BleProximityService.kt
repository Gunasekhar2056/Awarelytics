package com.awarelytics.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.awarelytics.app.data.local.TelemetryLog
import com.awarelytics.app.data.repository.AwarelyticsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Background BLE (Bluetooth Low Energy) proximity service.
 *
 * Detects nearby smartphones/wearables to determine social context.
 * Privacy-preserving: MAC addresses are hashed and immediately discarded.
 * Only an estimated_device_count integer is recorded.
 *
 * If estimated_device_count > 1, sets is_social_context = true.
 */
@AndroidEntryPoint
class BleProximityService : Service() {

    @Inject
    lateinit var repository: AwarelyticsRepository

    private var bleScanner: BluetoothLeScanner? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var scanJob: Job? = null

    // Track unique devices detected in the current scan window
    // Uses hashed identifiers for privacy
    private val detectedDeviceHashes = mutableSetOf<String>()

    companion object {
        private const val TAG = "BleProximityService"
        private const val NOTIFICATION_CHANNEL_ID = "ble_scan_channel"
        private const val NOTIFICATION_ID = 1001
        private const val SCAN_INTERVAL_MS = 30_000L    // Scan every 30 seconds
        private const val SCAN_DURATION_MS = 5_000L     // Each scan lasts 5 seconds
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        initBleScanner()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPeriodicScanning()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scanJob?.cancel()
        stopBleScanning()
    }

    private fun initBleScanner() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        bleScanner = bluetoothManager?.adapter?.bluetoothLeScanner
    }

    /**
     * Start periodic BLE scanning with intervals.
     * Low-power scanning mode to preserve battery.
     */
    private fun startPeriodicScanning() {
        scanJob?.cancel()
        scanJob = scope.launch {
            while (isActive) {
                performSingleScan()
                delay(SCAN_INTERVAL_MS)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun performSingleScan() {
        val scanner = bleScanner ?: return

        detectedDeviceHashes.clear()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setReportDelay(0)
            .build()

        try {
            scanner.startScan(emptyList<ScanFilter>(), scanSettings, scanCallback)
            delay(SCAN_DURATION_MS)
            scanner.stopScan(scanCallback)

            // Record the device count
            val deviceCount = detectedDeviceHashes.size
            logDeviceCount(deviceCount)

            Log.d(TAG, "BLE scan complete. Devices detected: $deviceCount")
        } catch (e: Exception) {
            Log.e(TAG, "BLE scan error", e)
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // Privacy: hash the MAC address and discard the original
            val hashedAddress = hashMacAddress(result.device.address)
            detectedDeviceHashes.add(hashedAddress)
            // The original MAC address is never stored
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { result ->
                val hashedAddress = hashMacAddress(result.device.address)
                detectedDeviceHashes.add(hashedAddress)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE scan failed with error code: $errorCode")
        }
    }

    /**
     * Privacy-preserving: hash MAC address using SHA-256.
     * The hash is only used for deduplication within a single scan window,
     * then discarded. No MAC addresses are ever stored.
     */
    private fun hashMacAddress(mac: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(mac.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(16) // truncated hash
    }

    /**
     * Log the estimated device count to the telemetry DB.
     * This is the ONLY data persisted from BLE scanning.
     */
    private fun logDeviceCount(count: Int) {
        scope.launch {
            repository.logTelemetry(
                TelemetryLog(
                    timestamp = System.currentTimeMillis(),
                    event_type = "BLE_SCAN",
                    value = count.toFloat(),
                    category = "PROXIMITY"
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopBleScanning() {
        try {
            bleScanner?.stopScan(scanCallback)
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping BLE scan", e)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Presence Detection",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Background proximity scanning for social context awareness"
            setShowBadge(false)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Awarelytics Active")
            .setContentText("Monitoring your digital presence")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }
}
