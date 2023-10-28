package com.example.baseapplication.ui.components

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.baseapplication.effects.LocationUpdatesEffect
import com.example.projandroid1.effects.SensorData
import com.example.projandroid1.effects.SensorDataEffect
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@RequiresPermission(
    allOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA],
)
@Composable
fun ImmersiveCamera(
    targetLocation: Location,
    usePreciseLocation: Boolean,
    verbose: Boolean,
) {


    // The location request that defines the location updates
    val locationRequest ={
        // Define the accuracy based on your needs and granted permissions
        val priority = if (usePreciseLocation) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        LocationRequest.Builder(priority, TimeUnit.SECONDS.toMillis(3)).build()
    }

    // Keeps track of received location updates as text
    var locationUpdates by remember { mutableStateOf("") }
    var currLocation by remember { mutableStateOf(targetLocation) }

    // Only register the location updates effect when we have a request
    LocationUpdatesEffect(locationRequest) { result ->
        // For each result update the text
        for (currentLocation in result.locations) {
            currLocation= currentLocation
            if(verbose) {
                locationUpdates = "${System.currentTimeMillis()}:\n" +
                        "- @lat: ${currentLocation.latitude}\n" +
                        "- @lng: ${currentLocation.longitude}\n" +
                        "- Accuracy: ${currentLocation.accuracy}\n\n"
            }
        }
    }

    var sensorData by remember { mutableStateOf<SensorData?>(null) }
    val azimuth = Math.toDegrees((sensorData?.azimuth ?: 0.0)).roundToInt()

    SensorDataEffect {
        sensorData = it
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(text = "Distance: ${currLocation.distanceTo(targetLocation)} meters")
        }

        if(verbose){
            item {
                Text(text = "Sensordata: azimuth: ${azimuth} up/down:${Math.toDegrees(sensorData?.pitch?.toDouble() ?: 0.0).roundToInt()} ")
            }

            item {
                Text(text = "angle: ${azimuth-currLocation.bearingTo(targetLocation)}")
            }

            item {
                Text(text = locationUpdates)
            }
        }

        item{
            Box(
                contentAlignment = Alignment.Center
            ) {
                CameraLiveFeed()
                Slider(
                    value = -azimuth+currLocation.bearingTo(targetLocation),
                    onValueChange = {},
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Red,//.colorScheme.secondary,
                        activeTrackColor = Color.Transparent,// MaterialTheme.colorScheme.secondaryContainer,
                        inactiveTrackColor = Color.Transparent,// MaterialTheme.colorScheme.secondaryContainer,
                    ),

                    valueRange = -20f..20f
                )
            }
        }

    }
}