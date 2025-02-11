package com.example.assignment123

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    // Instead of a hardcoded itinerary, we read it from a text file in the assets folder.
    private lateinit var itinerary: List<String>

    // This property will hold the computed route so that it can be used later.
    private lateinit var route: List<String>

    // Flag to track whether distances are shown in KM (true) or in Miles (false)
    private var isKm = true

    // Data class to hold details for each leg of the journey.
    data class LegDetails(val visa: String, val distanceKm: Int, val travelTimeHrs: Int)

    // Dummy data for each leg.
    private val legDetailsMap = mapOf(
        Pair("US", "UK") to LegDetails("No", 7000, 9),
        Pair("UK", "UAE") to LegDetails("Yes", 6400, 8),
        Pair("UAE", "IND") to LegDetails("No", 3000, 5),
        Pair("IND", "JAP") to LegDetails("No", 5000, 7),
        // Reversed pairs for reverse travel:
        Pair("UK", "US") to LegDetails("No", 7000, 9),
        Pair("UAE", "UK") to LegDetails("Yes", 6400, 8),
        Pair("IND", "UAE") to LegDetails("No", 3000, 5),
        Pair("JAP", "IND") to LegDetails("No", 5000, 7)
    )

    // Variables for progress tracking.
    private var stopsReached = 0
    private var distanceCovered = 0  // in KM
    private var totalDistance = 0    // total route distance in KM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Read the itinerary from the text file "bro.txt" in the assets folder.
        itinerary = readItineraryFromFile("bro.txt")

        // Retrieve the departure ("FROM") and destination ("TO") passed from MainActivity.
        // Default to the first and last stops from the itinerary if not provided.
        val from = intent.getStringExtra("FROM") ?: (itinerary.firstOrNull() ?: "")
        val to = intent.getStringExtra("TO") ?: (itinerary.lastOrNull() ?: "")

        // Compute and store the travel route.
        route = getTravelRoute(from, to)

        // Calculate total distance for the route.
        totalDistance = 0
        for (i in 0 until route.size - 1) {
            val origin = route[i]
            val destination = route[i + 1]
            val details = legDetailsMap[Pair(origin, destination)]
                ?: legDetailsMap[Pair(destination, origin)]
                ?: LegDetails("Unknown", 0, 0)
            totalDistance += details.distanceKm
        }

        // Get reference to the TableLayout for route details.
        val tableLayout: TableLayout = findViewById(R.id.table_flight_details)

        // Create and add a header row.
        val headerRow = TableRow(this)
        val headerLeg = TextView(this).apply {
            text = "ROUTE"
            setPadding(16, 16, 16, 16)
            textSize = 18f
            setBackgroundColor(0xFFCCCCCC.toInt())
            setTextColor(0xFF000000.toInt())
        }
        val headerVisa = TextView(this).apply {
            text = "VISA"
            setPadding(16, 16, 16, 16)
            textSize = 18f
            setBackgroundColor(0xFFCCCCCC.toInt())
            setTextColor(0xFF000000.toInt())
        }
        val headerDistance = TextView(this).apply {
            text = "Distance"
            setPadding(16, 16, 16, 16)
            textSize = 18f
            setBackgroundColor(0xFFCCCCCC.toInt())
            setTextColor(0xFF000000.toInt())
        }
        val headerTime = TextView(this).apply {
            text = "Time(hrs)"
            setPadding(16, 16, 16, 16)
            textSize = 18f
            setBackgroundColor(0xFFCCCCCC.toInt())
            setTextColor(0xFF000000.toInt())
        }
        headerRow.addView(headerLeg)
        headerRow.addView(headerVisa)
        headerRow.addView(headerDistance)
        headerRow.addView(headerTime)
        tableLayout.addView(headerRow)

        // Populate the table with a row for each leg.
        for (i in 0 until route.size - 1) {
            val origin = route[i]
            val destination = route[i + 1]
            val row = TableRow(this).apply { setPadding(8, 8, 8, 8) }
            val legTextView = TextView(this).apply {
                text = "$origin → $destination"
                setPadding(16, 16, 16, 16)
                textSize = 16f
                setTextColor(0xFF000000.toInt())
            }
            row.addView(legTextView)
            val details = legDetailsMap[Pair(origin, destination)]
                ?: legDetailsMap[Pair(destination, origin)]
                ?: LegDetails("Unknown", 0, 0)
            val visaTextView = TextView(this).apply {
                text = details.visa
                setPadding(16, 16, 16, 16)
                textSize = 16f
                setTextColor(0xFF000000.toInt())
            }
            row.addView(visaTextView)
            val distanceTextView = TextView(this).apply {
                text = details.distanceKm.toString()  // Initially display KM.
                setPadding(16, 16, 16, 16)
                textSize = 16f
                tag = details.distanceKm  // Save the original KM value.
                setTextColor(0xFF000000.toInt())
            }
            row.addView(distanceTextView)
            val timeTextView = TextView(this).apply {
                text = details.travelTimeHrs.toString()
                setPadding(16, 16, 16, 16)
                textSize = 16f
                setTextColor(0xFF000000.toInt())
            }
            row.addView(timeTextView)
            tableLayout.addView(row)
        }

        // Set up the Convert Distance button.
        val btnConvert: Button = findViewById(R.id.button_convert)
        btnConvert.setOnClickListener {
            // Toggle conversion flag.
            isKm = !isKm
            updateDistanceInTable(tableLayout)
            // Also update the progress section with converted distances.
            updateProgress()
            btnConvert.text = if (isKm) "Convert Distance to Miles" else "Convert Distance to KM"
        }

        // Set up the progress section.
        val textJourneyProgress: TextView = findViewById(R.id.text_journey_progress)
        val progressBar: ProgressBar = findViewById(R.id.progress_journey)
        val btnNextStop: Button = findViewById(R.id.btn_next_stop)

        // Initialize progress variables.
        stopsReached = 0
        distanceCovered = 0
        updateProgress(textJourneyProgress, progressBar, route.size - 1)

        // When the "Next Stop Reached" button is clicked:
        btnNextStop.setOnClickListener {
            val totalLegs = route.size - 1
            if (stopsReached < totalLegs) {
                // Add the next leg's distance to the distance covered.
                val origin = route[stopsReached]
                val destination = route[stopsReached + 1]
                val details = legDetailsMap[Pair(origin, destination)]
                    ?: legDetailsMap[Pair(destination, origin)]
                    ?: LegDetails("Unknown", 0, 0)
                distanceCovered += details.distanceKm
                stopsReached++
                updateProgress(textJourneyProgress, progressBar, totalLegs)
            }
        }
    }

    // Reads the itinerary from a text file in the assets folder.
    private fun readItineraryFromFile(filename: String): List<String> {
        return assets.open(filename).bufferedReader().use { it.readText() }
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Computes the route between two locations using the itinerary.
    private fun getTravelRoute(from: String, to: String): List<String> {
        val fromIndex = itinerary.indexOf(from)
        val toIndex = itinerary.indexOf(to)
        if (fromIndex == -1 || toIndex == -1) {
            throw IllegalArgumentException("One or both locations not found in the itinerary")
        }
        return if (fromIndex <= toIndex) {
            itinerary.subList(fromIndex, toIndex + 1)
        } else {
            itinerary.subList(toIndex, fromIndex + 1).reversed()
        }
    }

    // Updates the distances in the table (conversion logic).
    private fun updateDistanceInTable(tableLayout: TableLayout) {
        // Loop through table rows (skip header at index 0).
        for (i in 1 until tableLayout.childCount) {
            val row = tableLayout.getChildAt(i) as TableRow
            // Distance TextView is the third column (index 2).
            val distanceTextView = row.getChildAt(2) as TextView
            val originalDistanceKm = distanceTextView.tag as Int
            if (isKm) {
                distanceTextView.text = originalDistanceKm.toString()
            } else {
                val miles = originalDistanceKm * 0.621371
                distanceTextView.text = String.format("%.2f", miles)
            }
        }
    }

    // Updates the progress TextView and ProgressBar.
    private fun updateProgress(textView: TextView, progressBar: ProgressBar, totalLegs: Int) {
        // Calculate progress percentage based on distance covered.
        val progressPercent = if (totalDistance == 0) 0 else (distanceCovered.toFloat() / totalDistance * 100).toInt()
        progressBar.progress = progressPercent

        // Convert distance values if needed.
        val coveredStr = if (isKm) distanceCovered.toString() else String.format("%.2f", distanceCovered * 0.621371)
        val leftValue = totalDistance - distanceCovered
        val leftStr = if (isKm) leftValue.toString() else String.format("%.2f", leftValue * 0.621371)

        // Determine current and next stops.
        val currentStop = if (stopsReached < route.size) route[stopsReached] else route.last()
        val nextStop = if (stopsReached < route.size - 1) route[stopsReached + 1] else "Destination reached"

        // Compose progress text.
        val progressText = if (stopsReached < totalLegs)
            "Stops reached: $stopsReached / $totalLegs\n" +
                    "Current Stop: $currentStop\n" +
                    "Next Stop: $nextStop\n" +
                    "Distance covered: $coveredStr " + (if (isKm) "KM" else "Miles") + "\n" +
                    "Distance left: $leftStr " + (if (isKm) "KM" else "Miles") + "\n" +
                    "Progress: $progressPercent%"
        else
            "You have reached your destination."
        textView.text = progressText
    }

    // Convenience method to update progress when UI references are already known.
    private fun updateProgress() {
        val textView: TextView = findViewById(R.id.text_journey_progress)
        val progressBar: ProgressBar = findViewById(R.id.progress_journey)
        updateProgress(textView, progressBar, itinerary.size - 1)
    }
}
