package edu.farmingdale.threadsexample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FibonacciDemoNoBgThrd() {
    var answer by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("40") }
    var isCalculating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Simple recursive function
    fun calculateFib(n: Long): Long {
        return if (n <= 1) n else calculateFib(n - 1) + calculateFib(n - 2)
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Number?") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    isCalculating = true
                    try {
                        val num = textInput.toLongOrNull() ?: 0
                        val result = withContext(Dispatchers.Default) {
                            calculateFib(num)
                        }
                        answer = result.toString()
                    } catch (e: Exception) {
                        answer = "Error: ${e.message}"
                    }
                    isCalculating = false
                }
            },
            enabled = !isCalculating,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(if (isCalculating) "Calculating..." else "Calculate Fibonacci")
        }

        Text(
            text = if (isCalculating) "Calculating..." else "Result: $answer",
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}