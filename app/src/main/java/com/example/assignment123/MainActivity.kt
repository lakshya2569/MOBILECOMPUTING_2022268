package com.example.assignment123

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Set up window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the Spinners
        val spinnerDestination: Spinner = findViewById(R.id.spinner_destination)
        val spinnerLocation: Spinner = findViewById(R.id.spinner_location)

        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.location_options, // Reference to the string-array in strings.xml
            android.R.layout.simple_spinner_item // Default layout for spinner items
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the Spinners
        spinnerDestination.adapter = adapter
        spinnerLocation.adapter = adapter

        // Initialize the "GO" Button (ensure your activity_main.xml contains a button with id "btn_go")
        val btnGo: Button = findViewById(R.id.btn_go)
        btnGo.setOnClickListener {
            // Get the selected values from the spinners.
            // spinnerLocation represents the departure ("FROM") and spinnerDestination the destination ("TO")
            val from = spinnerLocation.selectedItem.toString()
            val destination = spinnerDestination.selectedItem.toString()

            // Create an intent to launch SecondActivity, passing the selected data
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("FROM", from)
            intent.putExtra("TO", destination)
            startActivity(intent)
        }
    }
}
