package com.example.Lakshyacomp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlightAnalyzerMainScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FlightAnalyzerMainScreen() {
        var fromLocation by remember { mutableStateOf("") }
        var toLocation by remember { mutableStateOf("") }
        val locations = listOf("USA", "UK", "UAE", "INDIA", "JAPAN")
        var fromExpanded by remember { mutableStateOf(false) }
        var toExpanded by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "FLIGHT ANALYSER",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // "From" Dropdown
            Text(text = "From", style = MaterialTheme.typography.bodyLarge)
            ExposedDropdownMenuBox(
                expanded = fromExpanded,
                onExpandedChange = { fromExpanded = !fromExpanded }
            ) {
                TextField(
                    value = fromLocation,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { fromExpanded = true }
                )
                ExposedDropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                    locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location) },
                            onClick = {
                                fromLocation = location
                                fromExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))


            Text(text = "To", style = MaterialTheme.typography.bodyLarge)
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = !toExpanded }
            ) {
                TextField(
                    value = toLocation,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { toExpanded = true }
                )
                ExposedDropdownMenu(expanded = toExpanded, onDismissRequest = { toExpanded = false }) {
                    locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location) },
                            onClick = {
                                toLocation = location
                                toExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (fromLocation.isNotEmpty() && toLocation.isNotEmpty() && fromLocation != toLocation) {
                        val intent = Intent(this@MainActivity, SecondActivity::class.java)
                        intent.putExtra("FROM", fromLocation)
                        intent.putExtra("TO", toLocation)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, "Select valid locations!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("GO")
            }
        }
    }
}
