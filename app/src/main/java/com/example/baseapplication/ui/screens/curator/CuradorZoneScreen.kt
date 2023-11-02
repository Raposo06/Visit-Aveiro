package com.example.baseapplication.ui.screens.curator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun CuradorZoneScreen(
    noUserAction: () -> Unit,
    viewModel: CuratorZoneViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    // Protected Area
    LaunchedEffect(uiState.user) {
        if (uiState.user == null) {
            noUserAction()
        }

    }
    CuradorZoneContent(
        uiState.user?.displayName ?: "No User",
        viewModel::onSignOutClick
    )
}


@Composable
fun CuradorZoneContent(
    userDisplayName: String,
    onSignOutClick: () -> Unit
) {
    Column {
        // Card to display user info and allow signout
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(4.dp).padding(horizontal = 4.dp),
            ){
                Text(text = userDisplayName, fontSize = 30.sp)
                Button(onClick = onSignOutClick) {
                    Icon(imageVector  = Icons.Default.ExitToApp, "Log Out")
                }
            }
        }

        
    }
}

@Preview
@Composable
fun CuratorZoneContentPreview() {
    CuradorZoneContent("John Doe", { print("OnSignoutclick") })
}