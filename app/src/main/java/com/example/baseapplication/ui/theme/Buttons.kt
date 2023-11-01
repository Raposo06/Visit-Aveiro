package com.example.baseapplication.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Buttons {
    companion object {
        @Composable
        fun ElevatedButton(
            onClick: () -> Unit,
            modifier: Modifier = Modifier,
            enabled: Boolean = true,
            shape: Shape = ButtonDefaults.elevatedShape,
            colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
            elevation: ButtonElevation? = ButtonDefaults.elevatedButtonElevation(),
            border: BorderStroke? = null
        ) {
            androidx.compose.material3.ElevatedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = shape,
                colors = colors,
                elevation = elevation,
                border = border
            ) {
                Text("Begin")
            }
        }



        @Composable
        fun ImageButton(
            image: Int,
            description: String,
            width: Dp,
            height: Dp,
            onClick: () -> Unit
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Alinha horizontalmente no centro
                verticalArrangement = Arrangement.Center, // Alinha verticalmente no centro
                modifier = Modifier.padding(16.dp)
            ) {
                androidx.compose.material3.ElevatedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .width(width)
                        .height(height),
                    contentPadding = PaddingValues(0.dp), // Remove o preenchimento padrão do botão
                    shape = RoundedCornerShape(60.dp), // A forma desejada do botão
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 8.dp,
                        disabledElevation = 0.dp
                    )
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = description)

            }
        }

    }
}