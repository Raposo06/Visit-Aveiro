package com.example.baseapplication.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.baseapplication.models.PointOfInterestModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
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
                modifier = Modifier.fillMaxSize().weight(1f),
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

    @Composable
        fun MapScreen() {
            val context = LocalContext.current
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            var location by remember { mutableStateOf<LatLng?>(null) }
            var hasLocationPermission by remember { mutableStateOf(false) }
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

            RequestLocationPermission(onPermissionGranted = {
                hasLocationPermission = true
            })

            LaunchedEffect(hasLocationPermission) {
                if (hasLocationPermission) {
                    try {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val locationRequest = LocationRequest.create().apply {
                                interval = 1000
                                fastestInterval = 500
                                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            }
                            val builder = LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                            val client: SettingsClient = LocationServices.getSettingsClient(context)
                            val task: Task<LocationSettingsResponse> =
                                client.checkLocationSettings(builder.build())

                            Log.d("Location", "Location Update: $locationRequest")
                            task.addOnSuccessListener {
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

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }


            var cameraPositionState: CameraPositionState? = null

            if (location != null) {
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location!!, 10f)
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

                        } else {
                            locationPermissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }
    }

