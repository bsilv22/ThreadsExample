package edu.farmingdale.threadsexample.countdowntimer

import android.util.Log
import android.widget.NumberPicker
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DecimalFormat
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel = viewModel()
) {
    // Calculate progress
    val totalMillis = remember(timerViewModel.selectedHour, timerViewModel.selectedMinute, timerViewModel.selectedSecond) {
        (timerViewModel.selectedHour * 3600 + timerViewModel.selectedMinute * 60 + timerViewModel.selectedSecond) * 1000L
    }
    val progress = if (totalMillis > 0) {
        timerViewModel.remainingMillis.toFloat() / totalMillis.toFloat()
    } else 0f

    // Animate the progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "progress"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Main countdown timer text
        Box(
            modifier = modifier
                .padding(20.dp)
                .size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            val duration: Duration = timerViewModel.remainingMillis.milliseconds
            val isLastTenSeconds = timerViewModel.remainingMillis <= 10000

            Text(
                text = String.format(
                    "%02d:%02d:%02d",
                    duration.inWholeHours,
                    duration.inWholeMinutes % 60,
                    duration.inWholeSeconds % 60
                ),
                style = TextStyle(
                    fontSize = 40.sp * 2f,
                    color = if (isLastTenSeconds) Color.Red else Color.Black,
                    fontWeight = if (isLastTenSeconds) androidx.compose.ui.text.font.FontWeight.Bold
                    else androidx.compose.ui.text.font.FontWeight.Normal
                )
            )
        }

        // Smaller visual progress indicator
        if (timerViewModel.isRunning) {
            Box(
                modifier = modifier
                    .padding(bottom = 20.dp)
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                    color = when {
                        timerViewModel.remainingMillis <= 10000 -> Color.Red
                        timerViewModel.remainingMillis <= 30000 -> Color(0xFFFFA500)
                        else -> Color(0xFF2196F3)
                    }
                )
            }
        }

        TimePicker(
            hour = timerViewModel.selectedHour,
            min = timerViewModel.selectedMinute,
            sec = timerViewModel.selectedSecond,
            onTimePick = timerViewModel::selectTime
        )

        // Control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 50.dp)
        ) {
            if (timerViewModel.isRunning) {
                if (!timerViewModel.isPaused) {
                    Button(
                        onClick = timerViewModel::pauseTimer,
                        modifier = modifier.padding(end = 8.dp)
                    ) {
                        Text("Pause")
                    }
                } else {
                    Button(
                        onClick = timerViewModel::startTimer,
                        modifier = modifier.padding(end = 8.dp)
                    ) {
                        Text("Resume")
                    }
                }
                Button(
                    onClick = timerViewModel::cancelTimer
                ) {
                    Text("Cancel")
                }
            } else {
                Button(
                    enabled = timerViewModel.selectedHour +
                            timerViewModel.selectedMinute +
                            timerViewModel.selectedSecond > 0,
                    onClick = timerViewModel::startTimer
                ) {
                    Text("Start")
                }
            }
        }
    }
}
@Composable
fun TimePicker(
    hour: Int = 0,
    min: Int = 0,
    sec: Int = 0,
    onTimePick: (Int, Int, Int) -> Unit = { _: Int, _: Int, _: Int -> }
) {
    var hourVal by remember { mutableIntStateOf(hour) }
    var minVal by remember { mutableIntStateOf(min) }
    var secVal by remember { mutableIntStateOf(sec) }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Hours")
            NumberPickerWrapper(
                initVal = hourVal,
                maxVal = 99,
                onNumPick = {
                    hourVal = it
                    onTimePick(hourVal, minVal, secVal)
                }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            Text("Minutes")
            NumberPickerWrapper(
                initVal = minVal,
                onNumPick = {
                    minVal = it
                    onTimePick(hourVal, minVal, secVal)
                }
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Seconds")
            NumberPickerWrapper(
                initVal = secVal,
                onNumPick = {
                    secVal = it
                    onTimePick(hourVal, minVal, secVal)
                }
            )
        }
    }
}

@Composable
fun NumberPickerWrapper(
    initVal: Int = 0,
    minVal: Int = 0,
    maxVal: Int = 59,
    onNumPick: (Int) -> Unit = {}
) {
    val numFormat = NumberPicker.Formatter { i: Int ->
        DecimalFormat("00").format(i)
    }

    AndroidView(
        factory = { context ->
            NumberPicker(context).apply {
                setOnValueChangedListener { _, _, newVal -> onNumPick(newVal) }
                minValue = minVal
                maxValue = maxVal
                value = initVal
                setFormatter(numFormat)
            }
        }
    )
}