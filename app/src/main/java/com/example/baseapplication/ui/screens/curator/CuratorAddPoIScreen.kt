package com.example.baseapplication.ui.screens.curator

import CameraUtility
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.baseapplication.R
import com.example.baseapplication.utlis.PermissionLauncerFactory
import com.example.baseapplication.models.PointOfInterestTypeEnum
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.play.integrity.internal.y
import java.util.Locale

@Composable
fun CuratorAddPoIScreen(
    onSuccess: () -> Unit,
    viewModel: CuratorAddPoIViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    CuratorAddPoIContent(
        uiState,
        viewModel::onNameChange,
        viewModel::onTypeChange,
        viewModel::onImgUrlChange,
        viewModel::onImgDelete,
        viewModel::onLocationChange,
    ) {
        viewModel.submitPoI {
            Toast.makeText(context, "Place Created!", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CuratorAddPoIContent(
    uiState: CuratorAddPoIUiState,
    onNameChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onImgUrlChange: (Uri?) -> Unit,
    onImgDelete: () -> Unit,
    onlocationChange: (Double, Double, String) -> Unit,

    onCreatePoIClick: () -> Unit,
) {
    val TAG = "CuratorAddPoIContent"
    val context = LocalContext.current

    val categoryOptions = PointOfInterestTypeEnum.values();
    val (selectedOption, onOptionSelected) = remember {
        mutableStateOf(
            PointOfInterestTypeEnum.valueOf(
                uiState.poi.type
            )
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
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

    // CAMERA UTILITIES //
    val cameraUtility = CameraUtility(context)
    var uri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            //capturedImageUri =  uri
            onImgUrlChange(uri)
            isLoading=false
        }


    val cameraPermissionLauncher = PermissionLauncerFactory {
        uri = cameraUtility.createImageUri()!!
        cameraLauncher.launch(uri)
        //capturedImageUri=uri!!
        Log.d(TAG, "cameraPermissionLauncher uri:${uri.path}")

    }

    val onAddPhotoClick = {
        isLoading=true
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            uri = cameraUtility.createImageUri()!!
            cameraLauncher.launch(uri)
            //capturedImageUri=uri!!
            Log.d(TAG, "onAddPhotoClick uri:${uri.path}")
        } else {
            // Request a permission
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // LOCATION UTILITIES //
    val updateLocation = {
        isLoading=true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val geocoder = Geocoder(context, Locale.getDefault())
        val locationReq = fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
        locationReq.addOnCompleteListener {
            Log.d(TAG, "locationReq.addOnCompleteListener")
            val addresses = geocoder.getFromLocation(it.result.latitude, it.result.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                Log.d(TAG, "getAddressLine")
                onlocationChange(
                    it.result.latitude,
                    it.result.longitude,
                    addresses[0].getAddressLine(0)
                )
                isLoading=false
            }
        }
    }

    val locationPermissionLauncher = PermissionLauncerFactory {
        updateLocation()
    }

    val onAddLocationClick = {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            updateLocation()
        } else {
            // Request a permission
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            Modifier.width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.Start,
        )
        {

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    trackColor = MaterialTheme.colorScheme.secondary,

                    )
            }


            if (uiState.errMsg.isNotEmpty()) {
                Text(text = uiState.errMsg)

            }

            TextField(
                value = uiState.poi.name,
                onValueChange = onNameChange,
                label = { Text(text = "Name") },
                singleLine = true,
                shape = MaterialTheme.shapes.small.copy(CornerSize(8.dp)), // Define as bordas arredondadas
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    contentColorFor(backgroundColor = MaterialTheme.colorScheme.surfaceVariant),
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp)
            )

            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Categoria:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                categoryOptions.forEach { value ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (value == selectedOption),
                                onClick = {
                                    onOptionSelected(value)
                                    onTypeChange(value.name)
                                }
                            )
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (value == selectedOption),
                            onClick = { onOptionSelected(value) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = value.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(200.dp))

        if (uiState.localImageUri?.path?.isNotEmpty() == true) {
            Log.d(TAG, "Rendering Image: ${uiState.localImageUri}")

            AsyncImage(model = uiState.localImageUri.toString(), contentDescription = "Capture Image", modifier = Modifier
                .size(400.dp)
                .width(200.dp)
                .offset(y = (-130).dp))
            ElevatedButton(
                onClick = onImgDelete,
                shape = RoundedCornerShape(50), // Bordas arredondadas para o botão
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Cor de fundo do botão
                    contentColor = MaterialTheme.colorScheme.onPrimary // Cor do texto e ícone dentro do botão
                ),
                modifier = Modifier
                    //.fillMaxWidth() // Ocupar a largura máxima disponível
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Espaçamento externo
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Remove Photo",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
            }


        } else {
            ElevatedButton(
                onClick = { onAddPhotoClick() },
                shape = RoundedCornerShape(50), // Bordas arredondadas
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Cor de fundo do botão
                    contentColor = MaterialTheme.colorScheme.onPrimary // Cor do texto e ícone
                ),
                modifier = Modifier
                     // Ocupar a largura máxima disponível
                    .padding(horizontal = 8.dp, vertical = 8.dp) // Espaçamento externo
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, // Substitua pelo seu recurso de ícone de "check"
                    contentDescription = "Add", // Descrição para acessibilidade
                    modifier = Modifier.padding(end = 8.dp) // Espaço entre o ícone e o texto
                )
                Text(
                    text = "Adicionar Foto",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }

        if (uiState.poi.address.isEmpty()) {
            ElevatedButton(
                onClick = { onAddLocationClick() },
                shape = RoundedCornerShape(50), // Bordas arredondadas para um design suave
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Cor de fundo do botão
                    contentColor = MaterialTheme.colorScheme.onPrimary // Cor do texto e ícone dentro do botão
                ),
                modifier = Modifier
                    //.fillMaxWidth() // Ocupar a largura máxima disponível
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Espaçamento externo
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home, // Substitua pelo seu recurso de ícone de "check"
                    contentDescription = "Add", // Descrição para acessibilidade
                    modifier = Modifier.padding(end = 8.dp) // Espaço entre o ícone e o texto
                )
                Text(
                    text = "Adicionar Local",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
            }

        } else {
            Text(text = uiState.poi.address)
        }


        if (!isLoading) {
            ElevatedButton(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        onCreatePoIClick()
                        isLoading = false
                    }
                },
                // Estilos do botão, com uma cor de elevação para adicionarem profundidade
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp) // Espaço vertical para estética
            ) {
                Icon(
                    imageVector = Icons.Filled.Check, // Substitua pelo seu recurso de ícone de "check"
                    contentDescription = "Check", // Descrição para acessibilidade
                    modifier = Modifier.padding(end = 8.dp) // Espaço entre o ícone e o texto
                )
                Text(
                    text = "Criar Local",
                    style = MaterialTheme.typography.labelMedium, // Estilo de texto padrão para botões
                    fontSize = TextUnit(18f, TextUnitType.Sp) // Aumenta a letra para dar mais ênfase
                )
            }

        }
    }
}
