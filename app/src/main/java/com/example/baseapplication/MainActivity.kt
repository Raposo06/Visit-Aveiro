package com.example.baseapplication


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baseapplication.ui.theme.BaseApplicationTheme
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import androidx.compose.runtime.*

import com.example.baseapplication.models.PointOfInterestTypeEnum
import com.example.baseapplication.ui.screens.ImmersiveCameraScreen
import com.example.baseapplication.ui.screens.MapScreen
import com.example.baseapplication.ui.theme.Buttons
import com.example.baseapplication.ui.screens.auth.SignInScreen
import com.example.baseapplication.ui.screens.auth.SignUpScreen
import com.example.baseapplication.ui.screens.curator.CuradorZoneScreen
import com.example.baseapplication.ui.screens.curator.CuratorAddPoIScreen
import com.example.baseapplication.ui.screens.curator.CuratorEditScreen
import com.example.baseapplication.ui.screens.listings.ListScreen
import kotlinx.coroutines.*


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BaseApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    AppNavigator()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    // todo: change initial route back to welcome page
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(onBeginClick = {
                navController.navigate("details")
            })
        }
        composable("details") {
            DetailScreen(navController)
        }

        composable("maps"){
            MapScreen().MapScreen()
        }


        composable(AppRoutes.LAZER_SCREEN.name){
            ListScreen(listType = PointOfInterestTypeEnum.Lazer, navController = navController)
        }

        composable(AppRoutes.HISTORIA_SCREEN.name){
            ListScreen(listType = PointOfInterestTypeEnum.Historia, navController = navController)
        }

        composable(AppRoutes.GASTRONOMIA_SCREEN.name){
            ListScreen(listType = PointOfInterestTypeEnum.Gastronomia, navController = navController)
        }

        composable(AppRoutes.CURATOR_MAINSCREEN.name){
            CuradorZoneScreen(
                noUserAction = { navController.navigate(AppRoutes.AUTH_SIGNIN.name){
                popUpTo(AppRoutes.CURATOR_MAINSCREEN.name) {
                    inclusive = true
                }
            }},
                onAddInterestPointClick = {
                    navController.navigate(AppRoutes.CURATOR_ADDPOI.name)
                },

                onGoToLocationClick = {
                    navController.navigate("${AppRoutes.MAP_SCREEN.name}/${it.latitude}/${it.longitude}")
                },
                onEditClick = {
                    navController.navigate(AppRoutes.CURATOR_EDITPOI.name+"/${it.poi_uid}")
                }
            )
        }

        composable(AppRoutes.CURATOR_ADDPOI.name){
            CuratorAddPoIScreen(
                onSuccess = { navController.navigateUp() },
            )
        }

        composable(AppRoutes.CURATOR_EDITPOI.name+"/{docId}"){
            val docId = it.arguments?.getString("docId") ?: ""
            CuratorEditScreen(
                docId,
                onSucess = {
                    navController.navigateUp()
                }
            )
        }

        composable(AppRoutes.AUTH_SIGNIN.name){
            SignInScreen(popUpScreen = { navController.navigateUp() }, onSignupClick = {navController.navigate(AppRoutes.AUTH_SIGNUP.name)})
        }

        composable(AppRoutes.AUTH_SIGNUP.name){
            SignUpScreen(popUpScreen = { navController.navigateUp() })
        }

        composable("${AppRoutes.MAP_SCREEN.name}/{latitude}/{longitude}") { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            MapScreen().GetMapByLocation(
                latitude,
                longitude
            ) {
                navController.navigate("${AppRoutes.IMMERSIVE_CAMERA.name}/$latitude/$longitude" )
            }
        }

        composable(AppRoutes.IMMERSIVE_CAMERA.name+"/{latitude}/{longitude}"){
            val latitude = it.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude = it.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            ImmersiveCameraScreen(latitude = latitude, longitude = longitude, false) { navController.navigateUp() }
        }
    }
}



@Composable
fun WelcomeScreen(onBeginClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 230.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bem Vindo a",
                    style = TextStyle(
                        shadow = Shadow(color = Color.Black, blurRadius = 10f),
                        color = Color.White,
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    )
                )
                Text(
                    text = "Aveiro",
                    style = TextStyle(
                        shadow = Shadow(color = Color.Black, blurRadius = 10f),
                        color = Color.White,
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 370.dp),
            contentAlignment = Alignment.TopCenter
        ){
            Buttons.ElevatedButton(onClick = onBeginClick,
                colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black),
                modifier = Modifier
                    .padding(16.dp)
                    .width(150.dp)
                    .height(55.dp)
            )
        }
    }
}

@Composable
fun DetailScreen(navController: NavController) {

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
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
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Buttons.ImageButton(R.drawable.leisure, "Lazer",160.dp,160.dp) {
                    navController.navigate(AppRoutes.LAZER_SCREEN.name)
                    // Handle button click
                    Log.d("ImageButton", "Lazer Button Clicked!")
                }
                Buttons.ImageButton(R.drawable.history_culture, "História e Cultura",160.dp,160.dp) {
                    navController.navigate(AppRoutes.HISTORIA_SCREEN.name)
                    Log.d("ImageButton", "Culture Button Clicked!")

                }
            }

            /*
            Buttons.ImageButton(R.drawable.events, "Events",280.dp,160.dp) {
                // Handle button click
                Log.d("ImageButton", "Events Button Clicked!")
            }

            Spacer(modifier = Modifier.height(20.dp))  // Adiciona espaço vertical entre os botões
*/
            Buttons.ImageButton(R.drawable.gastronomy, "Gastronomy",160.dp,160.dp) {
                navController.navigate(AppRoutes.GASTRONOMIA_SCREEN.name)
                // Handle button click
                Log.d("ImageButton", "Gastronomy Button Clicked!")
            }

            Spacer(modifier = Modifier.height(20.dp))  // Adiciona espaço vertical entre os botões

            Buttons.ImageButton(R.drawable.maps_pointer, "Where am I?",280.dp,160.dp) {
                navController.navigate("maps")

                Log.d("ImageButton", "Maps Button Clicked!")
            }
            Spacer(modifier = Modifier.height(20.dp))  // Adiciona espaço vertical entre os botões

            Buttons.ImageButton(R.drawable.curador, "Zona Curador",280.dp,160.dp) {
                // Handle button click
                navController.navigate(AppRoutes.CURATOR_MAINSCREEN.name)
                Log.d("ImageButton", "Zona Curador Clicked!")
            }
        }
    }
}













    @RequiresApi(Build.VERSION_CODES.R)
    @Preview(showBackground = true)
    @Composable
    fun WelcomeScreenPreview() {
        //WelcomeScreen(onBeginClick = {})
        //DetailScreen(navController = rememberNavController())
        //CameraApp()
    }
