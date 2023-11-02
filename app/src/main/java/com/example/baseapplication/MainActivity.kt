package com.example.baseapplication


import CameraUtility
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.example.baseapplication.ui.components.LocationInfo
import com.example.baseapplication.ui.theme.Buttons
import com.example.baseapplication.ui.screens.auth.SignInScreen
import com.example.baseapplication.ui.screens.auth.SignUpScreen
import com.example.baseapplication.ui.screens.curator.CuradorZoneScreen
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.*
import com.google.android.gms.location.LocationServices
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.Locale


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
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

@RequiresApi(Build.VERSION_CODES.R)
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
        composable("camera"){
            CameraApp()
        }

        composable(AppRoutes.CURATOR_MAINSCREEN.name){
            CuradorZoneScreen(noUserAction = { navController.navigate(AppRoutes.AUTH_SIGNIN.name){
                popUpTo(AppRoutes.CURATOR_MAINSCREEN.name) {
                    inclusive = true
                }
            } })
        }

        composable(AppRoutes.AUTH_SIGNIN.name){
            SignInScreen(popUpScreen = { navController.navigateUp() }, onSignupClick = {navController.navigate(AppRoutes.AUTH_SIGNUP.name)})
        }

        composable(AppRoutes.AUTH_SIGNUP.name){
            SignUpScreen(popUpScreen = { navController.navigateUp() })
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
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 370.dp),
            contentAlignment = Alignment.TopCenter
        ){
            Buttons.ElevatedButton(onClick = onBeginClick,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Buttons.ImageButton(R.drawable.leisure, "Lazer",160.dp,160.dp) {
                    navController.navigate("camera")
                    // Handle button click
                    Log.d("ImageButton", "Lazer Button Clicked!")
                }
                Buttons.ImageButton(R.drawable.history_culture, "História e Cultura",160.dp,160.dp) {
                    Log.d("ImageButton", "Culture Button Clicked!")

                }
            }

            Buttons.ImageButton(R.drawable.maps_pointer, "Where am I?",160.dp,160.dp) {
                navController.navigate("maps")

                Log.d("ImageButton", "Maps Button Clicked!")
            }
            Spacer(modifier = Modifier.height(20.dp))  // Adiciona espaço vertical entre os botões

            Buttons.ImageButton(R.drawable.events, "Events",280.dp,160.dp) {
                // Handle button click
                Log.d("ImageButton", "Events Button Clicked!")
            }

            Spacer(modifier = Modifier.height(20.dp))  // Adiciona espaço vertical entre os botões

            Buttons.ImageButton(R.drawable.events, "Zona Curador",280.dp,160.dp) {
                // Handle button click
                navController.navigate(AppRoutes.CURATOR_MAINSCREEN.name)
                Log.d("ImageButton", "Zona Curador Clicked!")
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
    val pointsofinterest = remember { mutableStateOf<List<LocationInfo>>(listOf()) }

    LaunchedEffect(Unit) {
        val firestore = Firebase.firestore
        firestore.collection("Points of Interest")
            .get()
            .addOnSuccessListener { documents ->
                pointsofinterest.value = documents.mapNotNull { it.toObject(LocationInfo::class.java) }
            }
    }

    RequestLocationPermission(onPermissionGranted = {
        hasLocationPermission = true
    })

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val locationRequest = LocationRequest.create().apply {
                        interval = 1000
                        fastestInterval = 500
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            locationResult ?: return
                            for (loc in locationResult.locations) {
                                location = LatLng(loc.latitude, loc.longitude)
                                //Log.d("Location", "Location Update: $location")
                                break
                            }
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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
            pointsofinterest.value.forEach { pointofinterest ->
                Marker(
                    state = MarkerState(position = LatLng(pointofinterest.latitude, pointofinterest.longitude)),
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
                    // Here you can show UI to explain why the permission is needed
                } else {
                    locationPermissionState.launchPermissionRequest()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraApp() {
    val context = LocalContext.current
    val cameraUtility = CameraUtility(context)
    val imageUrlState = remember { mutableStateOf<String?>(null) }  // Estado para armazenar o URL da imagem
    //Location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationState = remember { mutableStateOf<LatLng?>(null) }
    val addressState = remember { mutableStateOf<String?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    //Texto
    val showDialog = remember { mutableStateOf(false) }
    val locationName = remember { mutableStateOf("") }
    val categoryOptions = listOf("Lazer", "História")
    val selectedCategory = remember { mutableStateOf(categoryOptions[0]) }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessageState = remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()


    // Verificar permissões
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val isPermissionGranted = cameraPermissionState.status is PermissionStatus.Granted

    LaunchedEffect(cameraPermissionState.status) {
        if (!isPermissionGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Lançador para capturar imagem
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val imageUri = cameraUtility.imageUri
            showDialog.value = true

            // Fazer upload da imagem para o Firebase Storage
            val storage = Firebase.storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/${imageUri?.lastPathSegment}")
            val uploadTask = imageRef.putFile(imageUri!!)

            // Listener para o status do upload
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrlState.value = uri.toString()  // Armazenar o URL da imagem no estado
                }
            }.addOnFailureListener { e ->


            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize() // Faz a coluna ocupar todo o espaço disponível
            .padding(48.dp),
        verticalArrangement = Arrangement.Center, // Alinha os elementos na parte inferior
        horizontalAlignment = Alignment.CenterHorizontally, // Centraliza os elementos horizontalment

    ) {
        if (isPermissionGranted) {
            Buttons.ImageButton(
                image = R.drawable.camera, // Substitua pelo ID da sua imagem
                description = "Take Picture",
                width = 200.dp, // Substitua pela largura desejada
                height = 200.dp, // Substitua pela altura desejada
                onClick = {
                    cameraUtility.createImageUri()?.let { uri ->
                        takePictureLauncher.launch(uri)
                    }
                }
            )
        } else {
            Text("Camera permission is required.")
        }

    }

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
                        locationState.value = LatLng(locationResult.latitude, locationResult.longitude)
                    } else {
                        // Request location updates or handle null location
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val location = locationState.value

    if (location != null) {
        LaunchedEffect(location) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        addressState.value = address.getAddressLine(0) // Endereço completo
                    }
                }
            } catch (e: IOException) {
                // Trate a exceção
            }
        }
    }

    if (showDialog.value) {
        SnackbarHost(hostState = snackbarHostState)
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(text = "Nome do Local")
            },
            text = {
                Column {
                    TextField(
                        value = locationName.value,
                        onValueChange = { locationName.value = it },
                        label = { Text("Insira o nome do local") }
                    )
                    Spacer(modifier = Modifier.height(50.dp)) // Ajuste a altura conforme necessário

                    val categoryOptions = listOf("Lazer", "História")
                    val (selectedCategory, onCategorySelected) = remember {
                        mutableStateOf(
                            ""
                        )
                    }

                    Text("Categoria:") // Certifique-se de que este Text tenha um argumento 'text'
                    categoryOptions.forEach { category ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedCategory == category,
                                onClick = { onCategorySelected(category) }
                            )
                            Text(category) // Certifique-se de que este Text tenha um argumento 'text'
                        }
                    }
                }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false

                            // Obter o nome do local inserido
                            val nomeDoLocal = locationName.value
                            locationName.value = ""
                            val localType = selectedCategory.value
                            // Criar um objeto com todas as informações
                            val locationInfo = LocationInfo(
                                name = nomeDoLocal,
                                address = addressState.value.toString(),
                                latitude = location!!.latitude,
                                longitude = location!!.longitude,
                                imageUrl = imageUrlState.value.toString(),
                                localType = localType
                            )

                            // Salvar no Firestore
                            val firestore = Firebase.firestore
                            firestore.collection("Points of Interest")
                                .add(locationInfo)
                                .addOnSuccessListener {
                                    snackbarMessageState.value = "Local adicionado com sucesso!"
                                }
                                .addOnFailureListener {
                                    snackbarMessageState.value = "Erro ao adicionar local."
                                }
                        }
                    ) {
                        Text("Confirmar")
                    }
                })

        }
    val snackbarMessage = snackbarMessageState.value
    if (snackbarMessage != null) {
        Snackbar(
            action = {
                Button(onClick = { snackbarMessageState.value = null }) {
                    Text("OK")
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = snackbarMessage)
        }
    }


}






    @RequiresApi(Build.VERSION_CODES.R)
    @Preview(showBackground = true)
    @Composable
    fun WelcomeScreenPreview() {
        //WelcomeScreen(onBeginClick = {})
        //DetailScreen(navController = rememberNavController())
        CameraApp()
    }
