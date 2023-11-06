package com.example.baseapplication.ui.screens.curator

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baseapplication.R

@Composable
fun CuratorEditScreen(
    docId: String,
    onSucess: () -> Unit,
    viewModel: CuratorEditViewModel= viewModel(),
) {
    var isLoading by remember{ mutableStateOf(true) }
    var isUpdatingPoI by remember{ mutableStateOf(false) }

    val context= LocalContext.current
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = docId){
        viewModel.getPoI(docId){
            isLoading=false
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

    if(isLoading){
        // loading
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
    }
    else if(isUpdatingPoI){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A atualizar...",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
        }
    }
    else if (uiState.poi==null){
        // no data
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Erro ao carregar informação.",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
        }
    }
    else {
        CuratorAddPoIContent(
            uiState = curatorEditStateToAddState(uiState),
            onNameChange = viewModel::onNameChange,
            onTypeChange = viewModel::onTypeChange,
            onImgUrlChange = viewModel::onImgUrlChange,
            onImgDelete = viewModel::onImgDelete,
            onlocationChange = viewModel::onLocationChange,
        ) {
            isUpdatingPoI=true
            viewModel.submitPoI {
                isUpdatingPoI = false
                Toast.makeText(context, "Ponto de Interesse atualizado", Toast.LENGTH_SHORT).show()
                onSucess()
            }

        }
    }
}