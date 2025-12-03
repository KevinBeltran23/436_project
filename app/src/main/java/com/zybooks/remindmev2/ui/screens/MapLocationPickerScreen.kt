package com.zybooks.remindmev2.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLocationPickerScreen(
    onLocationSelected: (Double, Double, String) -> Unit,
    onNavigateUp: () -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(35.3004, -120.6623), 10f) // Default to SLO or some place
    }
    
    var uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = false)) }
    var properties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = false)) }
    
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            properties = properties.copy(isMyLocationEnabled = true)
            uiSettings = uiSettings.copy(myLocationButtonEnabled = true)
            
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            properties = properties.copy(isMyLocationEnabled = true)
            uiSettings = uiSettings.copy(myLocationButtonEnabled = true)
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick Location") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Re-center on my location if permission granted
                        // Logic handled by GoogleMap MyLocation button if enabled, but we can force it
                    }) {
                        // Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val target = cameraPositionState.position.target
                // TODO: Reverse geocode to get name? For now, use coordinates string or "Selected Location"
                onLocationSelected(target.latitude, target.longitude, "Lat: ${target.latitude}, Lng: ${target.longitude}")
            }) {
                Icon(Icons.Default.Check, contentDescription = "Select")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                uiSettings = uiSettings
            )
            
            // Center Marker (fixed)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Center",
                modifier = Modifier.align(Alignment.Center),
                tint = androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}
