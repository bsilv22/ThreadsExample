package edu.farmingdale.threadsexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import edu.farmingdale.threadsexample.ui.theme.ThreadsExampleTheme
import edu.farmingdale.threadsexample.countdowntimer.TimerScreen
import edu.farmingdale.threadsexample.countdowntimer.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreadsExampleTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showCountdown by remember { mutableStateOf(true) }
    val timerViewModel = remember { TimerViewModel() }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Button(
                onClick = { showCountdown = !showCountdown }
            ) {
                Text(if (showCountdown) "Switch to Fibonacci" else "Switch to Timer")
            }

            if (showCountdown) {
                TimerScreen(timerViewModel = timerViewModel)
            } else {
                FibonacciDemoNoBgThrd()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// ToDo 1: Call `FibonacciDemoNoBgThrd` that calculates the Fibonacci number of a given number.
// ToDo 2: Create a composable function called `FibonacciDemoWithCoroutine` that calculates the
//  Fibonacci number of a given number using a coroutine.
// ToDo 3: Start the application using the CountDownActivity
// ToDo 4: Make the Text 2f the timer larger
// ToDo 5: Show a visual indicator of the timer going down to 0
// ToDo 6: Add a button to rest the timer
// ToDo 7: Play a sound when the timer reaches 0
// ToDo 8: During the last 10 seconds, make the text red and bold
