package com.crmv.tfg_shop.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.crmv.tfg_shop.R
import com.crmv.tfg_shop.viewModel.LoginScreenViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Restablecer contraseña
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetEmailSent by remember { mutableStateOf(false) }
    var resetEmailError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text("YouTOOshop", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(32.dp))
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Zodiac Image"
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text("Sign In", style = MaterialTheme.typography.headlineSmall)


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email@domain.com") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.signInWithEmailAndPassword(email, password) {
                    navController.navigate("HomeScreen")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Olvidaste tu contraseña?",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier.clickable { showResetPasswordDialog = true }
        )

        // Dialog para restablecer contraseña
        if (showResetPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showResetPasswordDialog = false },
                title = { Text("Restablecer Contraseña") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("email@domain.com") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (resetEmailSent) {
                            Text(
                                text = "Correo de restablecimiento enviado.",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        resetEmailError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetPassword(resetEmail) { success, errorMessage ->
                                if (success) {
                                    resetEmailSent = true
                                } else {
                                    resetEmailError = errorMessage
                                }
                            }
                        }
                    ) {
                        Text("Enviar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetPasswordDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("or")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("RegisterScreen") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(32.dp))

        val annotatedString = AnnotatedString.Builder("By clicking continue, you agree to our Terms of Service and Privacy Policy")
            .apply {
                addStringAnnotation(
                    tag = "TOS",
                    annotation = "terms_of_service",
                    start = 37,
                    end = 52
                )
                addStringAnnotation(
                    tag = "Privacy",
                    annotation = "privacy_policy",
                    start = 57,
                    end = 70
                )
            }.toAnnotatedString()

        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let { annotation ->
                    when (annotation.tag) {
                        "TOS" -> {
                            // Handle Terms of Service click
                        }
                        "Privacy" -> {
                            // Handle Privacy Policy click
                        }
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

}
