package com.example.baseapplication.ui.screens.curator

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baseapplication.R
import com.example.baseapplication.models.PointOfInterestModel


@Composable
fun CuradorZoneScreen(
    noUserAction: () -> Unit,
    onAddInterestPointClick: () -> Unit,
    onGoToLocationClick: (PointOfInterestModel) -> Unit,
    onEditClick: (PointOfInterestModel) -> Unit,
    viewModel: CuratorZoneViewModel = viewModel(),
) {
    val uiState by viewModel.uiState

    // Protected Area
    LaunchedEffect(uiState.user) {
        if (uiState.user == null) {
            noUserAction()
        } else {
            //viewModel.getEntries()
        }
    }

    CuradorZoneContent(
        uiState.user?.displayName ?: "No User",
        viewModel::onSignOutClick,
        poiList = uiState.points,
        onAddInterestPointClick,
        // todo: howver dont think it needs to be implemented, there is no purpose
        onPoIClick = {Log.d("CuratorPoIItem","on poi click")},
        onGoToLocationClick = onGoToLocationClick,
        onEditClick = onEditClick,
        onDeleteClick = viewModel::deletePoIEntry,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuradorZoneContent(
    userDisplayName: String,
    onSignOutClick: () -> Unit,

    poiList: List<PointOfInterestModel>,
    onAddInterestPointClick: () -> Unit,

    onPoIClick: (PointOfInterestModel) -> Unit,
    onGoToLocationClick: (PointOfInterestModel) -> Unit,
    onEditClick: (PointOfInterestModel) -> Unit,
    onDeleteClick: (PointOfInterestModel) -> Unit,
) {
    Image(
        painter = painterResource(id = R.drawable.city_of_aveiro),
        contentDescription = "Background Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
    )
    //TopAppBar(title = { Text(text = "Zona Curador") })
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Card to display user info and allow signout
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
            ) {

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .padding(horizontal = 4.dp),
            ){
                Text(text = userDisplayName, fontSize = 30.sp)
                Button(onClick = onSignOutClick) {
                    Icon(imageVector  = Icons.Default.ExitToApp, "Log Out")
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(text = "Pontos de Interesse", fontSize = 20.sp)
            Button(onClick = onAddInterestPointClick) {
                Icon(imageVector  = Icons.Default.Add, "Add point of interest")
            }
        }
        Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp,)

        poiList.forEach {
            CuratorPoIItem(
                poi = it,
                onPoIClick = onPoIClick,
                onGoToLocationClick = onGoToLocationClick,
                onEditClick = onEditClick,
                onDeleteClick= onDeleteClick,
                )
        }

    }
}


/*
@Preview
@Composable
fun CuratorZoneContentPreview() {
    CuradorZoneContent("John Doe", { print("OnSignoutclick") }, listOf(),{ print("onAddInterestPointClick") } ,{}, {}, {}, {},)
}

 */