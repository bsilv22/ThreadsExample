package edu.farmingdale.threadsexample.countdowntimer

import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private var timerJob: Job? = null
    private var toneGenerator: ToneGenerator? = null

    // Values selected in time picker
    var selectedHour by mutableIntStateOf(0)
        private set
    var selectedMinute by mutableIntStateOf(0)
        private set
    var selectedSecond by mutableIntStateOf(0)
        private set

    // Total milliseconds when timer starts
    var totalMillis by mutableLongStateOf(0L)
        private set

    // Time that remains
    var remainingMillis by mutableLongStateOf(0L)
        private set

    // Timer's running status
    var isRunning by mutableStateOf(false)
        private set

    // Timer's paused status
    var isPaused by mutableStateOf(false)
        private set

    init {
        initializeToneGenerator()
    }

    private fun initializeToneGenerator() {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun selectTime(hour: Int, min: Int, sec: Int) {
        selectedHour = hour
        selectedMinute = min
        selectedSecond = sec
    }

    fun startTimer() {
        if (isPaused) {
            // Resume from pause
            isPaused = false
            startCountdown()
        } else {
            // Start new timer
            totalMillis = (selectedHour * 60 * 60 + selectedMinute * 60 + selectedSecond) * 1000L

            if (totalMillis > 0) {
                isRunning = true
                remainingMillis = totalMillis
                startCountdown()
            }
        }
    }

    fun pauseTimer() {
        if (isRunning && !isPaused) {
            timerJob?.cancel()
            isPaused = true
        }
    }

    fun cancelTimer() {
        if (isRunning) {
            timerJob?.cancel()
            isRunning = false
            isPaused = false
            remainingMillis = 0
        }
    }

    private fun startCountdown() {
        timerJob = viewModelScope.launch {
            while (remainingMillis > 0) {
                delay(1000)
                remainingMillis -= 1000
            }

            // Play sound when timer reaches 0
            playTimerFinishSound()

            isRunning = false
            isPaused = false
        }
    }

    private fun playTimerFinishSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        toneGenerator?.release()
        toneGenerator = null
    }
}