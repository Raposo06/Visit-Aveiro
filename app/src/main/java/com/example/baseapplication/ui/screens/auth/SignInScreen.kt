package com.example.baseapplication.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon


@Composable
fun SignInScreen(
    popUpScreen: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: SignInViewModel = viewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState

    SignInScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInClick = {viewModel.onSignInClick(popUpScreen) },
        onForgotPasswordClick = viewModel::onForgotPasswordClick,
        onSignupClick = onSignupClick
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreenContent(
    modifier: Modifier = Modifier,
    uiState: SiginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit
) {

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        if(uiState.errMsg.isNotEmpty()){
            Text(text = uiState.errMsg)
        }
        
        TextField(uiState.email,  onEmailChange, label = { Text("Email") })
        TextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text(text= "Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Person
                else Icons.Outlined.Person

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                Button(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
                }
            })

        Button(onSignInClick, content = { Text(text = "Sign In")})
        Button(onForgotPasswordClick, content = { Text(text = "Forgot Password")})
        Button(onSignupClick, content = { Text(text = "Create Account")})
    }
}
