    package com.crmv.tfg_shop.screen

    import android.annotation.SuppressLint
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.text.ClickableText
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBack
    import androidx.compose.material.icons.filled.Menu
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.AnnotatedString
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import com.crmv.tfg_shop.Drawer.model.CustomDrawerState
    import com.crmv.tfg_shop.Drawer.model.opposite
    import com.crmv.tfg_shop.R
    import com.crmv.tfg_shop.viewModel.LoginScreenViewModel
    import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun RegisterScreen(navController: NavController, viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }



        Scaffold(

            topBar = {
                TopAppBar(
                    title = { Text(text = "")},
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("LoginScreen") }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "BackLogin"
                            )
                        }
                    }
                )
            }
        ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))



            Spacer(modifier = Modifier.height(32.dp))
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Zodiac Image"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("YouTOOshop", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))

            Text("Sign Up", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            var confirmationMessage by remember { mutableStateOf("") }

            Button(onClick = {
                viewModel.createUserWithEmailAndPassword(email, password, name) { success, errorMessage ->
                    if (success) {
                        navController.navigate("LoginScreen")
                        confirmationMessage = "Usuario registrado y datos guardados correctamente."
                    } else {
                        confirmationMessage = "Error al crear el usuario: $errorMessage"
                    }
                }
            },
                modifier = Modifier.fillMaxWidth()) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (confirmationMessage.isNotEmpty()) {
                Text(text = confirmationMessage)
            }

            Text("or register with")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Handle Google sign-in */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Google")
            }

            Spacer(modifier = Modifier.height(8.dp))

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
    }
