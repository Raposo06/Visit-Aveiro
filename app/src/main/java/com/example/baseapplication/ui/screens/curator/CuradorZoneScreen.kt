package com.example.baseapplication.ui.screens.curator

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun CuradorZoneScreen(
    noUserAction: () -> Unit,
    viewModel: CuratorZoneViewModel = viewModel()
) {
    // Protected Area
    LaunchedEffect(viewModel.uiState.value.user) {
        if (viewModel.uiState.value.user == null){
            noUserAction()
        }

    }
    Row(

    ) {
        Text("Hello")
        Button(onClick = viewModel::onSignOutClick) {
            Text(text = "log out")
        }
    }



}

