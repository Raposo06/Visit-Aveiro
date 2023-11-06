package com.example.baseapplication.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.example.baseapplication.models.PointOfInterestModel
import com.example.baseapplication.utlis.PermissionLauncerFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.concurrent.TimeUnit

class MapScreen {

    @Composable
    fun GetMapByLocation(
        latitude: Double,
        longitude: Double,
        onImmersiveCameraClick: () -> Unit,
    ) {

        val context = LocalContext.current
        var location by remember { mutableStateOf(LatLng(latitude, longitude)) }
        val pointsOfInterest = remember { mutableStateOf<List<PointOfInterestModel>>(listOf()) }

        LaunchedEffect(Unit) {
            val firestore = Firebase.firestore
            firestore.collection("Points of Interest")
                .get()
                .addOnSuccessListener { documents ->
                    pointsOfInterest.value =
                        documents.mapNotNull { it.toObject(PointOfInterestModel::class.java) }
                }
        }


        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, 20f) // Set your desired zoom level
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                // Place a marker at the provided location
                Marker(
                    state = MarkerState(position = location),
                    title = "Selected Location",
                    snippet = "Lat: $latitude, Long: $longitude"
                )

                // Place additional points of interest if needed
                /*pointsOfInterest.value.forEach { pointOfInterest ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                pointOfInterest.latitude,
                                pointOfInterest.longitude
                            )
                        ),
                        title = pointOfInterest.name,
                        snippet = pointOfInterest.address
                    )
                }*/
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                onClick = onImmersiveCameraClick,
            ) {
                Text(text = "Usar navegacao imersiva")
            }
        }

    }

    @SuppressLint("NotConstructor")
    @Composable
    fun MapScreen() {
        val context = LocalContext.current
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        var location by remember { mutableStateOf<LatLng?>(null) }
        val pointsofinterest = remember { mutableStateOf<List<PointOfInterestModel>>(listOf()) }

        LaunchedEffect(Unit) {
            val firestore = Firebase.firestore
            firestore.collection("Points of Interest")
                .get()
                .addOnSuccessListener { documents ->
                    pointsofinterest.value =
                        documents.mapNotNull { it.toObject(PointOfInterestModel::class.java) }
                }
        }

        var hasLocationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val locationPermissionLauncher = PermissionLauncerFactory {
            hasLocationPermission = true
        }

        val onRequestPermission = {
            if (!hasLocationPermission) {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        LaunchedEffect(key1 = hasLocationPermission) {
            onRequestPermission()
        }

        if (hasLocationPermission) {
            try {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val locationRequest =
                        LocationRequest.Builder(10000L)  // interval in milliseconds
                            .setPriority(PRIORITY_HIGH_ACCURACY)
                            .build()


                    Log.d("Location", "Location Update: $locationRequest")

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            locationResult ?: return
                            for (loc in locationResult.locations) {
                                location = LatLng(loc.latitude, loc.longitude)
                                Log.d("Location", "Location Update: $location")
                                break
                            }
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )


                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }




        var cameraPositionState: CameraPositionState? = null

        if (location != null) {
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location!!, 20f)
            }
        }

        if (cameraPositionState != null) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                location?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "You're here!"
                    )
                }
                pointsofinterest.value.forEach { pointofinterest ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                pointofinterest.latitude,
                                pointofinterest.longitude
                            )
                        ),
                        title = pointofinterest.name,
                        snippet = pointofinterest.address
                    )
                }
            }
        }
    }

    @SuppressLint("PermissionLaunchedDuringComposition")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestLocationPermission(onPermissionGranted: () -> Unit) {
        val locationPermissionState = rememberPermissionState(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        LaunchedEffect(locationPermissionState.status) {
            when (locationPermissionState.status) {
                PermissionStatus.Granted -> {
                    onPermissionGranted()
                }

                is PermissionStatus.Denied -> {
                    if ((locationPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                        Log.d("Location", "NO LOCATIONNNNN")

                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                }
            }
        }
    }
}






