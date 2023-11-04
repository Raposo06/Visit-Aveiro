package com.example.baseapplication.ui.screens.listings

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.baseapplication.R
import com.example.baseapplication.consts.PoICollection
import com.example.baseapplication.models.PointOfInterestModel
import com.example.baseapplication.models.PointOfInterestTypeEnum
import com.example.baseapplication.ui.screens.MapScreen
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private val TAG = "ListScreen"
@Composable
fun ListScreen(
    listType: PointOfInterestTypeEnum,
    navController: NavController
) {

    var poiList:List<PointOfInterestModel> by remember{ mutableStateOf(listOf()) }
    var isLoading by remember {
        mutableStateOf(true)
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


    LaunchedEffect(key1 = listType){
        FirebaseFirestore.getInstance().collection(PoICollection)
            .whereEqualTo("type", listType.name)
            .get()
            .addOnSuccessListener { documents ->
                poiList  = documents.mapNotNull { it.toObject(PointOfInterestModel::class.java) }
                isLoading=false
            }
            .addOnFailureListener{ exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                isLoading=false
            }
    }
    if(isLoading){
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
    } else {
        if (poiList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ainda não existem pontos de interesse disponíveis.",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
            }
        } else {

            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                poiList.forEach {
                    ListItem(
                        poi = it,
                        onPoIClick = { },
                        onGoToLocationClick = { },
                        navController = navController
                    )

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    poi: PointOfInterestModel,
    onPoIClick: (PointOfInterestModel) -> Unit,
    onGoToLocationClick: (PointOfInterestModel) -> Unit,
    navController: NavController
) {

    var extended by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(16.dp),
        onClick = { onPoIClick(poi) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)

        ) {
            // Image
            AsyncImage(
                model = poi.imageUrl,
                contentDescription = "image of ${poi.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { extended = !extended }
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(if (extended) 1f else 16f / 9f),
                contentScale = ContentScale.FillWidth,

                )
            Divider()
            Text(text = poi.name, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = poi.type,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Divider(
                color = MaterialTheme.colorScheme.tertiary,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onGoToLocationClick(poi) })
            ) {

                Text(
                    text = poi.address,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )

                Button(onClick = {
                    navController.navigate("map/${poi.latitude}/${poi.longitude}")
                }) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Go to location")
                    Text("  Location")
                }


            }
        }
    }
}