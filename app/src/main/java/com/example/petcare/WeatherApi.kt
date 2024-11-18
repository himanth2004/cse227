package com.example.petcare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class WeatherApi : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_api)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getCurrentLocationAndOpenMap()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getCurrentLocationAndOpenMap()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied. Can't show location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocationAndOpenMap() {
        // Check if permission is granted again (just in case)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener(this, OnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("PetCareMap", "Latitude: $latitude, Longitude: $longitude")
                    openGoogleMaps(latitude, longitude)
                } else {
                    Toast.makeText(this, "Unable to retrieve location. Try again later.", Toast.LENGTH_SHORT).show()
                }
            })
            .addOnFailureListener { e ->
                Toast.makeText(this, "Location error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGoogleMaps(latitude: Double, longitude: Double) {
        val uri = Uri.parse("geo:$latitude,$longitude?q=pet+care")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps") // Ensure Google Maps is used
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show()
        }
    }
}
