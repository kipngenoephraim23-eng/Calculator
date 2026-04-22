package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp()
                }
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var firstNumber by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("") }
    var isNewNumber by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C))
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Display
        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button rows
        val buttonRows = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        buttonRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { symbol ->
                    CalculatorButton(
                        symbol = symbol,
                        modifier = Modifier
                            .weight(if (symbol == "0") 2f else 1f)
                            .aspectRatio(if (symbol == "0") 2f else 1f),
                        onClick = {
                            handleButtonClick(
                                symbol = symbol,
                                display = display,
                                firstNumber = firstNumber,
                                operation = operation,
                                isNewNumber = isNewNumber,
                                updateState = { newDisplay, newFirst, newOp, newIsNew ->
                                    display = newDisplay
                                    firstNumber = newFirst
                                    operation = newOp
                                    isNewNumber = newIsNew
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when (symbol) {
        "÷", "×", "-", "+", "=" -> Color(0xFFFF9500)
        "C", "±", "%" -> Color(0xFFA5A5A5)
        else -> Color(0xFF333333)
    }

    val textColor = when (symbol) {
        "C", "±", "%" -> Color.Black
        else -> Color.White
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(
            text = symbol,
            fontSize = 32.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

fun handleButtonClick(
    symbol: String,
    display: String,
    firstNumber: String,
    operation: String,
    isNewNumber: Boolean,
    updateState: (String, String, String, Boolean) -> Unit
) {
    when (symbol) {
        in "0".."9" -> {
            val newDisplay = if (isNewNumber || display == "0") symbol
            else display + symbol
            updateState(newDisplay, firstNumber, operation, false)
        }
        "." -> {
            if (!display.contains(".")) {
                val newDisplay = if (isNewNumber) "0." else "$display."
                updateState(newDisplay, firstNumber, operation, false)
            }
        }
        "C" -> {
            updateState("0", "", "", true)
        }
        "±" -> {
            val newDisplay = if (display.startsWith("-"))
                display.drop(1) else "-$display"
            updateState(newDisplay, firstNumber, operation, isNewNumber)
        }
        "%" -> {
            val value = display.toDoubleOrNull() ?: 0.0
            val result = value / 100
            updateState(formatResult(result), firstNumber, operation, true)
        }
        "+", "-", "×", "÷" -> {
            updateState(display, display, symbol, true)
        }
        "=" -> {
            if (firstNumber.isNotEmpty() && operation.isNotEmpty()) {
                val first = firstNumber.toDoubleOrNull() ?: 0.0
                val second = display.toDoubleOrNull() ?: 0.0
                val result = when (operation) {
                    "+" -> first + second
                    "-" -> first - second
                    "×" -> first * second
                    "÷" -> if (second != 0.0) first / second else 0.0
                    else -> 0.0
                }
                updateState(formatResult(result), "", "", true)
            }
        }
    }
}

fun formatResult(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}