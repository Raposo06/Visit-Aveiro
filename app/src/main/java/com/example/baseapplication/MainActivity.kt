package com.example.baseapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baseapplication.ui.theme.BaseApplicationTheme
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.IOException
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.tasks.await
import com.google.android.gms.location.LocationRequest



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BaseApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(onBeginClick = {
                navController.navigate("details")
            })
        }
        composable("details") {
            DetailScreen(navController)
        }

        composable("maps"){
            MapScreen2()
        }

    }
}



@Composable
fun WelcomeScreen(onBeginClick: () -> Unit) {
    val navController = rememberNavController()
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.city_of_aveiro), // Replace 'your_image_name' with the name of your image
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
                .padding(top = 230.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bem Vindo a",
                    style = TextStyle(
                        shadow = Shadow(color = Color.Black, blurRadius = 10f),
                        color = Color.White,
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    )
                )

                Text(
                    text = "Aveiro",
                    style = TextStyle(
                        shadow = Shadow(color = Color.Black, blurRadius = 10f),
                        color = Color.White,
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    )
                )

                //Spacer(modifier = Modifier.height(100.dp))



            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 370.dp),
            contentAlignment = Alignment.TopCenter
        ){
            ElevatedButton(onClick = onBeginClick,
                colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black),
                modifier = Modifier
                    .padding(16.dp)
                    .width(150.dp)
                    .height(55.dp)
            )
        }
    }
}

@Composable
fun DetailScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                ImageButton(R.drawable.leisure, "Lazer",160.dp,160.dp) {
                    // Handle button click
                    Log.d("ImageButton", "Lazer Button Clicked!")
                }
                ImageButton(R.drawable.history_culture, "História e Cultura",160.dp,160.dp) {
                    Log.d("ImageButton", "Culture Button Clicked!")

                }
            }
            }


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 270.dp)
        ){

            ImageButton(R.drawable.maps_pointer, "Where am I?",160.dp,160.dp) {
                navController.navigate("maps")

                Log.d("ImageButton", "Maps Button Clicked!")
            }
            Spacer(modifier = Modifier.height(20.dp))  // Adiciona espaço vertical entre os botões

            ImageButton(R.drawable.events, "Events",280.dp,160.dp) {
                // Handle button click
                Log.d("ImageButton", "Events Button Clicked!")
            }
        }
    }
}


@Composable
fun MapScreen() {


    val context = LocalContext.current
    val addressString = "Rua Azurara, Mangualde"
    val geocoder = Geocoder(context)
    var location by remember { mutableStateOf<LatLng?>(null) }

    // Perform geocoding in a coroutine to avoid NetworkOnMainThreadException
    LaunchedEffect(addressString) {
        withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocationName(addressString, 1)
                if (addresses != null) {
                    Log.d("Geocoding", "Addresses found: ${addresses.size}")
                }else{
                    Log.d("Geocoding", "Addresses found: 0")

                }
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    location = LatLng(address.latitude, address.longitude)
                }else {
                    Log.d("Geocoding", "No addresses found for: $addressString")
                }
            } catch (e: IOException) {
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
                    title = "Localização Geocodificada",
                    snippet = addressString
                )
            }
        }
    }
}



@Composable
fun MapScreen2() {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var location by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    RequestLocationPermission(onPermissionGranted = {
        hasLocationPermission = true
    })

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                // Check if location permission is granted
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Now you can use await() since permission is granted
                    val locationResult = fusedLocationClient.lastLocation.await()
                    if (locationResult != null) {
                        location = LatLng(locationResult.latitude, locationResult.longitude)
                    } else {
                        // Request location updates or handle null location
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
                    title = "Localização Geocodificada",
                    snippet = "addressString"
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
                    // Here you can show UI to explain why the permission is needed
                } else {
                    locationPermissionState.launchPermissionRequest()
                }
            }
        }
    }
}




@Composable
fun ElevatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.elevatedShape,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.elevatedButtonElevation(),
    border: BorderStroke? = null
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border
    ) {
        Text("Begin")
    }
}

@Composable
fun ImageButton(image: Int, description: String, width: Dp,height: Dp,onClick: () -> Unit ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, // Alinha horizontalmente no centro
        verticalArrangement = Arrangement.Center, // Alinha verticalmente no centro
        modifier = Modifier.padding(16.dp)){
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .width(width)
            .height(height),
        contentPadding = PaddingValues(0.dp), // Remove o preenchimento padrão do botão
        shape = RoundedCornerShape(60.dp), // A forma desejada do botão
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = description)

    }
}





@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    //WelcomeScreen(onBeginClick = {})
    DetailScreen(navController= rememberNavController())
}