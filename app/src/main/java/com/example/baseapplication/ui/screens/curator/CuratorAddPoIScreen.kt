package com.example.baseapplication.ui.screens.curator

import CameraUtility
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.baseapplication.models.PointOfInterestTypeEnum
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
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
                label = { Text(text = "Name") })

            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(text = "Categoria:")

                categoryOptions.forEach { value ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (value == selectedOption),
                                onClick = {
                                    onOptionSelected(value)
                                    onTypeChange(value.name)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        RadioButton(
                            selected = (value == selectedOption),
                            onClick = { onOptionSelected(value) }
                        )
                        Text(
                            text = value.name,
                            style = MaterialTheme.typography.bodyLarge.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }


        if (uiState.localImageUri?.path?.isNotEmpty() == true) {
            Log.d(TAG, "Rendering Image: ${uiState.localImageUri}")

            AsyncImage(model = uiState.localImageUri.toString(), contentDescription = "")
            Button(onClick = onImgDelete) {
                Text(text = "Remove Photo")
            }


        } else {
            Button(onClick = { onAddPhotoClick() }) {
                Text(text = "Adicionar Foto")
            }
        }

        if (uiState.poi.address.isEmpty()) {
            Button(onClick = { onAddLocationClick() })
            {
                Text(text = "Adicionar Local")
            }
        } else {
            Text(text = uiState.poi.address)
        }


        if (!isLoading) {
            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        onCreatePoIClick()
                        isLoading = false
                    }

                },

                )
            {
                Text(text = "Criar Local")
            }
        }
    }
}

@Composable
fun PermissionLauncerFactory(onSuccess: () -> Unit): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            onSuccess()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}

