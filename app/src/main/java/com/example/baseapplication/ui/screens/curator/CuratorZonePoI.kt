package com.example.baseapplication.ui.screens.curator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.baseapplication.models.PointOfInterestModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuratorPoIItem(
    poi: PointOfInterestModel,
    onPoIClick: (PointOfInterestModel) -> Unit,
    onGoToLocationClick: (PointOfInterestModel) -> Unit,
    onEditClick: (PointOfInterestModel) -> Unit,
    onDeleteClick: (PointOfInterestModel) -> Unit,
    navController: NavController
) {

    var extended by remember {mutableStateOf(false)}

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
                    .aspectRatio(if (extended) 1f else 16f/9f),
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
            Divider(
                color = MaterialTheme.colorScheme.tertiary,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onGoToLocationClick(poi) })
            ) {
                Button(onClick = { onEditClick(poi) }) {
                    Text(text = "Edit")
                    Icon(imageVector = Icons.Default.Create, "Edit Entry")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = { onDeleteClick(poi) }
                ) {
                    Text(text = "Delete")
                    Icon(imageVector = Icons.Default.Delete, "Delete Entry")
                }
            }

        }
    }
}
/*
@Preview
@Composable
fun CuratorPoIItemPreview() {
    CuratorPoIItem(poi = PointOfInterestModel(
        name = "Refugio do Drinks",
        userid = "uid",
        type = "type",
        address = "Rua de Calouste Gulbenkian, 3810-074 Aveiro",
        imageUrl = "https://lh5.googleusercontent.com/p/AF1QipM53htCYlBapyhCJTubMWx-upwqoYv9SdyRnIu7=w408-h306-k-no",
        longitude = -8.656334,
        latitude = 40.636413
    ), {}, {}, {}) {}
}
*/
