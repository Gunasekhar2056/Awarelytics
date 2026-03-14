package com.awarelytics.app.ml

import android.content.Context
import android.util.Log
import com.awarelytics.app.data.local.DriftEvent
import com.awarelytics.app.data.repository.AwarelyticsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device TensorFlow Lite classifier for phubbing detection.
 *
 * Runs a quantized 8-bit model entirely on-device for zero-latency
 * inference and strict privacy compliance.
 *
 * Input: 6-feature vector (from FeatureAggregator)
 * Output: Float between 0.0 and 1.0 (Phubbing Probability Score)
 *
 * If probability > 0.85 → emits ActionableDriftEvent to the ViewModel.
 */
@Singleton
class PhubbingClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val featureAggregator: FeatureAggregator,
    private val repository: AwarelyticsRepository
) {
    private var interpreter: Interpreter? = null
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var inferenceJob: Job? = null

    // Phubbing threshold - if probability exceeds this, trigger a nudge
    private val phubbingThreshold = 0.85f

    // Inference interval - run every 5 minutes
    private val inferenceIntervalMs = 5 * 60 * 1000L

    // Callback for when a drift event is detected
    var onDriftEventDetected: ((Float) -> Unit)? = null

    companion object {
        private const val TAG = "PhubbingClassifier"
        private const val MODEL_FILENAME = "phubbing_model.tflite"
        private const val NUM_FEATURES = 6
    }

    /**
     * Initialize the TFLite interpreter by loading the model from assets.
     */
    fun initialize() {
        try {
            val modelBuffer = loadModelFile()
            val options = Interpreter.Options().apply {
                setNumThreads(2)
            }
            interpreter = Interpreter(modelBuffer, options)
            Log.d(TAG, "TFLite model loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load TFLite model: ${e.message}")
            // Fallback: use heuristic-based scoring if model fails to load
        }
    }

    /**
     * Start periodic inference every 5 minutes.
     */
    fun startPeriodicInference() {
        inferenceJob?.cancel()
        inferenceJob = scope.launch {
            while (isActive) {
                runInference()
                delay(inferenceIntervalMs)
            }
        }
    }

    /**
     * Stop the periodic inference.
     */
    fun stopInference() {
        inferenceJob?.cancel()
    }

    /**
     * Run a single inference cycle:
     * 1. Aggregate features from Room DB
     * 2. Feed into TFLite model
     * 3. Process output probability
     * 4. If > threshold, emit ActionableDriftEvent
     */
    suspend fun runInference(): Float {
        val features = featureAggregator.aggregateFeatures()
        val probability = predict(features)

        Log.d(TAG, "Phubbing probability: $probability (features: ${features.toFloatArray().toList()})")

        // Threshold logic: if probability > 0.85, emit ActionableDriftEvent
        if (probability > phubbingThreshold) {
            emitDriftEvent(probability)
        }

        return probability
    }

    /**
     * Run the TFLite model on the feature vector.
     * Falls back to heuristic scoring if model is not available.
     */
    private fun predict(features: FeatureAggregator.FeatureVector): Float {
        val inputArray = features.toFloatArray()

        return if (interpreter != null) {
            runTFLiteInference(inputArray)
        } else {
            // Heuristic fallback when TFLite model is not available
            heuristicScore(features)
        }
    }

    /**
     * Run TFLite inference on the input tensor.
     */
    private fun runTFLiteInference(inputArray: FloatArray): Float {
        // Prepare input buffer [1, NUM_FEATURES]
        val inputBuffer = ByteBuffer.allocateDirect(NUM_FEATURES * 4).apply {
            order(ByteOrder.nativeOrder())
            inputArray.forEach { putFloat(it) }
            rewind()
        }

        // Prepare output buffer [1, 1]
        val outputBuffer = ByteBuffer.allocateDirect(4).apply {
            order(ByteOrder.nativeOrder())
        }

        try {
            interpreter?.run(inputBuffer, outputBuffer)
            outputBuffer.rewind()
            val probability = outputBuffer.float
            return probability.coerceIn(0f, 1f)
        } catch (e: Exception) {
            Log.e(TAG, "TFLite inference error: ${e.message}")
            return 0f
        }
    }

    /**
     * Heuristic fallback scoring when TFLite model is unavailable.
     * Applies weighted scoring based on behavioral signals.
     */
    private fun heuristicScore(features: FeatureAggregator.FeatureVector): Float {
        var score = 0f

        // High unlock frequency is a strong signal
        score += (features.unlocksPast15Min / 10f).coerceAtMost(0.3f)

        // Short average sessions indicate mindless scrolling
        if (features.avgSessionLengthSeconds in 1f..15f) {
            score += 0.2f
        }

        // High app switching frequency
        score += (features.appSwitchFrequency / 15f).coerceAtMost(0.2f)

        // Social context amplifies the phubbing score
        if (features.isSocialContext) {
            score += 0.15f
        }

        // Evening time is prime phubbing time
        if (features.timeOfDayCategory == 2) {
            score += 0.1f
        }

        // Many recent notifications may trigger checking behavior
        score += (features.recentNotificationCount / 20f).coerceAtMost(0.1f)

        return score.coerceIn(0f, 1f)
    }

    /**
     * Emit an ActionableDriftEvent to the repository and notify the ViewModel.
     */
    private suspend fun emitDriftEvent(probability: Float) {
        val event = DriftEvent(
            timestamp = System.currentTimeMillis(),
            ml_probability = probability,
            user_reaction = "PENDING"
        )
        repository.logDriftEvent(event)

        // Notify any registered listener (ViewModel / NudgeManager)
        onDriftEventDetected?.invoke(probability)

        Log.w(TAG, "🚨 ActionableDriftEvent emitted! Probability: $probability")
    }

    /**
     * Load the TFLite model from assets.
     */
    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(MODEL_FILENAME)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Clean up resources.
     */
    fun release() {
        inferenceJob?.cancel()
        interpreter?.close()
        interpreter = null
    }
}
