package com.example.baseapplication.ui.screens.mapping

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.baseapplication.R
import com.example.baseapplication.models.PointOfInterestModel
import com.example.baseapplication.utlis.PermissionLauncerFactory
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private val TAG = "MapScreen3"

@SuppressLint("MissingPermission")
@Composable
fun MapScreen3() {

    val pointsofinterest = remember { mutableStateOf<List<PointOfInterestModel>>(listOf()) }

    LaunchedEffect(Unit) {
        val firestore = Firebase.firestore
        firestore.collection("Points of Interest")
            .get()
            .addOnSuccessListener { documents ->
                pointsofinterest.value =
                    documents.mapNotNull { it.toObject(PointOfInterestModel::class.java) }
                Log.d(TAG, "updated document, #: ${pointsofinterest.value.size}")
            }
    }

    MapContent(usePreciseLocation = true, points = pointsofinterest.value)

}

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun MapContent(
    usePreciseLocation: Boolean,
    points: List<PointOfInterestModel>,
) {

    val context = LocalContext.current

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

    var location by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(hasLocationPermission) {
        if(hasLocationPermission){

            val currentLocationReq =LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)

            currentLocationReq.addOnCompleteListener{
                if(it.isSuccessful){
                    if (it.result == null)
                        Toast.makeText(context, "Cannot get location.", Toast.LENGTH_SHORT).show()
                    else {
                        location= it.result
                        Log.d(TAG, "Got current location: $location")
                    }


                } else {
                    Log.d(TAG, "getCurrentLocation failed: ${it.exception}")
                }
            }
            currentLocationReq.addOnFailureListener {
                Log.d(TAG, "currentLocationReq failed: ${it}")
            }
        } else {
            onRequestPermission()
        }
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

    if (points.isEmpty()) {
        // loading points
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A carregar...",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
        }
    } else if (location != null) {
        GoogleMapWrapper(cameraCenter = location!!, points = points)

    } else if(!hasLocationPermission){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Esta funcionalidade requer as seguinte permissão:")
                Text(text = " - Localizacao")
                Button(onClick = { onRequestPermission() }) {
                    Text(text = "Requerer Permissoes")
                }
            }
        }

    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Erro ao carregar mapa",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
        }
    }
}

@RequiresPermission(
    allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun GoogleMapWrapper(
    cameraCenter: Location,
    points: List<PointOfInterestModel>,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(cameraCenter.latitude, cameraCenter.longitude),
            20f
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.HYBRID,
            isTrafficEnabled = false
        )
    ) {

        points.forEach { point ->
            Marker(
                state = MarkerState(position = LatLng(point.latitude, point.longitude)),
                title = point.name,
                snippet = point.address
            )
        }
    }
}
