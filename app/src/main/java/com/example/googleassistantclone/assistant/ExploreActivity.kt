package com.example.googleassistantclone.assistant

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.googleassistantclone.MainActivity
import com.example.googleassistantclone.R
import com.example.googleassistantclone.UI_Utils.Utils
import com.example.googleassistantclone.UI_Utils.Utils.commands
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class ExploreActivity : AppCompatActivity() {
    var weather_url =
        "https://api.weatherbit.io/v2.0/current?lat=23.2836919&lon=77.4617912&key=e946fc3cd7b14f9cbe391855ca3315c3"
    private lateinit var temperature: TextView
    private lateinit var greetings: TextView
    private lateinit var today: TextView
    private lateinit var description: TextView
    private lateinit var weatherCardView: CardView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)
        Utils.setCustomActionBar(supportActionBar, this)
        temperature = findViewById(R.id.temperature)
        greetings = findViewById(R.id.greetings)
        today = findViewById(R.id.today)
        description = findViewById(R.id.description)
        weatherCardView = findViewById(R.id.weather_card_view)
        val chipGroup: ChipGroup = findViewById(R.id.chips_commands)
        for (command in commands) {
            val chip = Chip(this)
            chip.text = command.toString()
            chip.setButtonDrawable(R.drawable.shape)
            chip.setTextAppearance(R.style.chips)
            chip.setPadding(25, 10, 25, 10)
            chipGroup.addView(chip)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1
                )

            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1
                )
            }
        }

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Turn On Location")
            builder.setMessage("Location is Off, Please Try Turning On")
            builder.setPositiveButton("OK") { dialog, which ->
                val intent = Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(intent)
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                val intent = Intent(
                    this,
                    MainActivity::class.java
                )
                startActivity(intent)
            }
            val dialog: AlertDialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.darker_gray)
            dialog.show()
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        obtainLocation()
        weatherCardView.setOnClickListener {
            obtainLocation()
        }

        val c: Calendar = Calendar.getInstance()
        when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> {
                greetings.text = "Good Morning"
            }

            in 12..15 -> {
                greetings.text = "Good Afternoon"
            }

            in 16..20 -> {
                greetings.text = "Good Evening"
            }

            in 21..24 -> {
                greetings.text = "Good Night"
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun obtainLocation() {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(com.android.volley.Request.Method.GET, weather_url,
            { response ->
                try {
                    val obj = JSONObject(response)
                    val arr = obj.getJSONArray("data")
                    val obj2 = arr.getJSONObject(0)
                    temperature.text = obj2.getString("temp") + "°C" + obj2.getString("city_name")
                    today.text = "TODAY |" + obj2.getString("datetime")
                    description.text = obj2.getJSONObject("weather").getString("description")
                } catch (e: JSONException) {
                    Log.e("JSON Error", "Error parsing JSON response: $response", e)
                }
            },
            {
                Log.e("error", "not worked")
            })
        queue.add(stringRequest)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}