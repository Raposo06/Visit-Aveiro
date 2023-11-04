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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
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
        viewModel::onLocationChange,
    ) { viewModel.submitPoI {
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
    onlocationChange: (Double, Double, String) -> Unit,

    onCreatePoIClick: () -> Unit,
) {
    val TAG = "CuratorAddPoIContent"
    val context = LocalContext.current

    val categoryOptions = PointOfInterestTypeEnum.values();
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(categoryOptions [0] ) }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val cameraUtility = CameraUtility(context)


    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    var uri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }



    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            //capturedImageUri =  uri
            onImgUrlChange(uri)
        }


    val cameraPermissionLauncher = PermissionLauncerFactory {
        uri = cameraUtility.createImageUri()!!
        cameraLauncher.launch(uri)
        //capturedImageUri=uri!!
        Log.d(TAG, "cameraPermissionLauncher uri:${capturedImageUri.path}")

    }

    val oncamclick ={
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            uri = cameraUtility.createImageUri()!!
            cameraLauncher.launch(uri)
            //capturedImageUri=uri!!
            Log.d(TAG, "oncamclick uri:${capturedImageUri.path}")
        } else {
            // Request a permission
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }



    val updateLocation ={
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val geocoder = Geocoder(context, Locale.getDefault())
        val locationReq = fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
        locationReq.addOnCompleteListener {
            Log.d(TAG, "locationReq.addOnCompleteListener")
            val addresses = geocoder.getFromLocation(it.result.latitude, it.result.longitude, 1)
            if (addresses != null && addresses.isNotEmpty() ) {
                Log.d(TAG, "getAddressLine")
                onlocationChange(it.result.latitude, it.result.longitude, addresses[0].getAddressLine(0))
            }
        }
    }

    val locationPermissionLauncher = PermissionLauncerFactory {
        updateLocation()
    }

    val onAddLocationClick ={
        isLoading=true
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            updateLocation()
        } else {
            // Request a permission
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        isLoading=false
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column (
            Modifier.width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.Start,
        )
        {
            if(uiState.errMsg.isNotEmpty()){
                Text(text = uiState.errMsg)

            }

            TextField(value =uiState.poi.name, onValueChange =onNameChange, label = { Text(text = "Name")})

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


        if(uiState.localImageUri?.path?.isNotEmpty() == true){
            Text(text = uiState.localImageUri.path!!)
            AsyncImage(model = uiState.localImageUri.path!!, contentDescription = "Picture")

        } else {
            Button(onClick = { oncamclick() }) {
                Text(text = "Adicionar Foto")
            }
        }

        if(uiState.poi.address.isEmpty()){
            Button(onClick = {onAddLocationClick()})
            {
                Text(text = "Adicionar Local")
            }
        } else {
            Text(text = uiState.poi.address)
        }

        if(isLoading){
            Text(text = "Loading...")
        }

        if(!isLoading ){
            Button(onClick = {
                    //set
                    isLoading=true
                    onCreatePoIClick()
                    isLoading=false },

                )
            {
                Text(text = "Criar Local")
            }
        }


    }
}

@Composable
fun PermissionLauncerFactory(onSuccess: ()->Unit): ManagedActivityResultLauncher<String, Boolean> {
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

