package com.example.Lakshyacomp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val from = intent.getStringExtra("FROM") ?: "USA"
        val to = intent.getStringExtra("TO") ?: "INDIA"

        setContent {
            FlightDetailsScreen(from, to)
        }
    }

    @Composable
    fun FlightDetailsScreen(from: String, to: String) {

        val itinerary = readItinerary()

        var isKm by remember { mutableStateOf(true) }
        var stopsReached by remember { mutableStateOf(0) }
        var visibleRowCount by remember { mutableStateOf(3) }

        val route = getTravelRoute(itinerary, from, to)
        val totalLegs = route.size - 1


        val totalDistance = route.zipWithNext().sumOf { getLegDetails(it.first, it.second).distanceKm }
        val distanceCovered = getDistanceCovered(route, stopsReached)
        val distanceLeft = totalDistance - distanceCovered

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(
                onClick = { isKm = !isKm },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (isKm) "Convert to Miles" else "Convert to KM")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Current mode: ${if (isKm) "KM" else "Miles"}")
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(route.zipWithNext().take(visibleRowCount)) { index, (origin, destination) ->
                    LegRow(origin, destination, isKm)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            ProgressCard(
                stopsReached = stopsReached,
                totalLegs = totalLegs,
                currentStop = if (stopsReached < route.size) route[stopsReached] else route.last(),
                nextStop = if (stopsReached < route.size - 1) route[stopsReached + 1] else "Destination reached",
                distanceCovered = distanceCovered,
                distanceLeft = distanceLeft,
                isKm = isKm
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (stopsReached < totalLegs) {
                        stopsReached++
                        if (visibleRowCount < totalLegs) {
                            visibleRowCount++
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (stopsReached < totalLegs) "Next Stop Reached" else "You have arrived!")
            }
        }
    }


    @Composable
    fun readItinerary(): List<String> {
        val context = LocalContext.current
        return remember {
            try {
                context.assets.open("bro.txt").bufferedReader().use { reader ->
                    reader.readLines().map { it.trim() }.filter { it.isNotEmpty() }
                }
            } catch (e: Exception) {

                listOf("USA", "UK", "UAE", "INDIA", "JAPAN")
            }
        }
    }

    fun getDistanceCovered(route: List<String>, stopsReached: Int): Int {
        var distance = 0
        for (i in 0 until stopsReached) {
            val details = getLegDetails(route[i], route[i + 1])
            distance += details.distanceKm
        }
        return distance
    }

    fun getTravelRoute(itinerary: List<String>, from: String, to: String): List<String> {
        val fromIndex = itinerary.indexOf(from)
        val toIndex = itinerary.indexOf(to)
        return if (fromIndex <= toIndex) itinerary.subList(fromIndex, toIndex + 1)
        else itinerary.subList(toIndex, fromIndex + 1).reversed()
    }

    data class LegDetails(val visa: String, val distanceKm: Int, val travelTimeHrs: Int)

    fun getLegDetails(from: String, to: String): LegDetails {
        val legDetailsMap = mapOf(
            Pair("USA", "UK") to LegDetails("No", 7000, 9),
            Pair("UK", "UAE") to LegDetails("Yes", 6400, 8),
            Pair("UAE", "INDIA") to LegDetails("No", 3000, 5),
            Pair("INDIA", "JAPAN") to LegDetails("No", 5000, 7)
        )
        return legDetailsMap[Pair(from, to)] ?: legDetailsMap[Pair(to, from)] ?: LegDetails("Unknown", 0, 0)
    }

    @Composable
    fun LegRow(origin: String, destination: String, isKm: Boolean) {
        val details = getLegDetails(origin, destination)
        val displayedDistance = if (isKm) details.distanceKm else (details.distanceKm * 0.621371).toInt()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$origin → $destination", modifier = Modifier.padding(8.dp))
            Text(text = "Visa: ${details.visa}", modifier = Modifier.padding(8.dp))
            Text(text = "Dist: $displayedDistance ${if (isKm) "KM" else "Miles"}", modifier = Modifier.padding(8.dp))
            Text(text = "Time: ${details.travelTimeHrs} hrs", modifier = Modifier.padding(8.dp))
        }
    }

    @Composable
    fun ProgressCard(
        stopsReached: Int,
        totalLegs: Int,
        currentStop: String,
        nextStop: String,
        distanceCovered: Int,
        distanceLeft: Int,
        isKm: Boolean
    ) {
        val progressPercent = if (totalLegs > 0) (stopsReached.toFloat() / totalLegs * 100).toInt() else 0
        val displayedDistanceCovered = if (isKm) distanceCovered else (distanceCovered * 0.621371).toInt()
        val displayedDistanceLeft = if (isKm) distanceLeft else (distanceLeft * 0.621371).toInt()
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Progress: $progressPercent%", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Current Stop: $currentStop", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Next Stop: $nextStop", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Distance Covered: $displayedDistanceCovered ${if (isKm) "KM" else "Miles"}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Distance Left: $displayedDistanceLeft ${if (isKm) "KM" else "Miles"}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = if (totalLegs > 0) stopsReached / totalLegs.toFloat() else 0f,
                    modifier = Modifier.fillMaxWidth().height(10.dp)
                )
            }
        }
    }
}
