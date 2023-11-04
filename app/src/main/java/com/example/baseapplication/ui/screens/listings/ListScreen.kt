package com.example.baseapplication.ui.screens.listings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.baseapplication.consts.PoICollection
import com.example.baseapplication.models.PointOfInterestModel
import com.example.baseapplication.models.PointOfInterestTypeEnum
import com.google.firebase.firestore.FirebaseFirestore

private val TAG = "ListScreen"
@Composable
fun ListScreen(
    listType: PointOfInterestTypeEnum
) {
    var poiList:List<PointOfInterestModel> by remember{ mutableStateOf(listOf()) }

    LaunchedEffect(key1 = listType){
        FirebaseFirestore.getInstance().collection(PoICollection)
            .whereEqualTo("type", listType.name)
            .get()
            .addOnSuccessListener { documents ->
                poiList  = documents.mapNotNull { it.toObject(PointOfInterestModel::class.java) }
            }
            .addOnFailureListener{ exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

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
            )

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItem(
    poi: PointOfInterestModel,
    onPoIClick: (PointOfInterestModel) -> Unit,
    onGoToLocationClick: (PointOfInterestModel) -> Unit,
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

                Button(onClick = { onGoToLocationClick(poi) }) {
                    Icon(imageVector = Icons.Default.ArrowForward, "Go to location")
                }


            }
        }
    }
}