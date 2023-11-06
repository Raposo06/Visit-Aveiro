package com.example.baseapplication.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.baseapplication.utlis.PermissionLauncerFactory
import com.example.baseapplication.R
import com.example.baseapplication.ui.components.ImmersiveCamera
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@Composable
fun ImmersiveCameraScreen(
    latitude: Double,
    longitude: Double,
    verbose: Boolean = false,
    onStopImmersiveCamera: () -> Unit,
) {
    var hasCameraPermission by remember {
        mutableStateOf(false)
    }
    var hasLocationPermission by remember {
        mutableStateOf(false)
    }

    val cameraPermissionLauncher = PermissionLauncerFactory {
        hasCameraPermission = true
    }

    val locationPermissionLauncher = PermissionLauncerFactory {
        hasLocationPermission = true
    }

    val onRequestPermission = {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(key1 = hasCameraPermission, key2 = hasLocationPermission) {
        onRequestPermission()
    }

    Image(
        painter = painterResource(id = R.drawable.city_of_aveiro),
        contentDescription = "Background Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop // Crop the image to fill the screen
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {

        if (hasCameraPermission && hasLocationPermission) {
            val targetLocation = Location("")
            targetLocation.latitude = latitude
            targetLocation.longitude = longitude
            val targetLocationLatLng = LatLng(latitude, longitude)

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(targetLocationLatLng, 20f) // Set your desired zoom level
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL,
                        isTrafficEnabled = false,

                    )
                ) {
                    // Place a marker at the provided location
                    Marker(
                        state = MarkerState(position = targetLocationLatLng),
                        title = "Selected Location",
                        snippet = "Lat: $latitude, Long: $longitude"
                    )
                }
                ImmersiveCamera(
                    targetLocation = targetLocation,
                    usePreciseLocation = true,
                    verbose = verbose
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    onClick = onStopImmersiveCamera,
                ) {
                    Text(text = "Parar navegacao imersiva")
                }
            }

        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Esta funcionalidade requer as seguintes permissoes:")
                if (!hasCameraPermission) {
                    Text(text = " - Camera")
                }
                if (!hasLocationPermission) {
                    Text(text = " - Localizacao")
                }

                Button(onClick = { onRequestPermission() }) {
                    Text(text = "Requerer Permissoes")
                }
            }
        }
    }
}