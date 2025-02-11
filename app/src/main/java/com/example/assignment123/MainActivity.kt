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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val spinnerDestination: Spinner = findViewById(R.id.spinner_destination)
        val spinnerLocation: Spinner = findViewById(R.id.spinner_location)


        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.location_options,
            android.R.layout.simple_spinner_item
        )


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinnerDestination.adapter = adapter
        spinnerLocation.adapter = adapter


        val btnGo: Button = findViewById(R.id.btn_go)
        btnGo.setOnClickListener {

            val from = spinnerLocation.selectedItem.toString()
            val destination = spinnerDestination.selectedItem.toString()


            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("FROM", from)
            intent.putExtra("TO", destination)
            startActivity(intent)
        }
    }
}
