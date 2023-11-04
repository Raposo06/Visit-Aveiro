package com.example.baseapplication.ui.screens

import CameraUtility
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.baseapplication.R
import com.example.baseapplication.models.PointOfInterestModel
import com.example.baseapplication.ui.theme.Buttons
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale

class TakePhoto {


    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun CameraApp() {
        val context = LocalContext.current
        val cameraUtility = CameraUtility(context)
        val imageUrlState =
            remember { mutableStateOf<String?>(null) }  // Estado para armazenar o URL da imagem
        //Location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationState = remember { mutableStateOf<LatLng?>(null) }
        val addressState = remember { mutableStateOf<String?>(null) }
        var hasLocationPermission = remember { mutableStateOf(false) }
        //Texto
        val showDialog = remember { mutableStateOf(false) }
        val locationName = remember { mutableStateOf("") }
        val categoryOptions = listOf("Lazer", "História")
        val (selectedCategory, onCategorySelected) = remember { mutableStateOf<String?>(null) }
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

        MapScreen().RequestLocationPermission(onPermissionGranted = {
            hasLocationPermission.value = true
        })

        LaunchedEffect(hasLocationPermission) {
            if (hasLocationPermission.value) {
                try {
                    // Check if location permission is granted
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Now you can use await() since permission is granted
                        val locationResult = fusedLocationClient.lastLocation.await()
                        if (locationResult != null) {
                            locationState.value =
                                LatLng(locationResult.latitude, locationResult.longitude)
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
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
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


                        Text("Categoria:") // Certifique-se de que este Text tenha um argumento 'text'
                        categoryOptions.forEach { category ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = { onCategorySelected(category) }
                                )
                                Text(text = category) // Certifique-se de que este Text tenha um argumento 'text'
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false

                            // Obter o nome do local inserido
                            val localName = locationName.value
                            locationName.value = ""
                            val localType = selectedCategory.toString()
                            // Criar um objeto com todas as informações
                            val locationInfo = PointOfInterestModel(
                                name = localName,
                                address = addressState.value.toString(),
                                latitude = location!!.latitude,
                                longitude = location!!.longitude,
                                imageUrl = imageUrlState.value.toString(),
                                type = localType
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

}


