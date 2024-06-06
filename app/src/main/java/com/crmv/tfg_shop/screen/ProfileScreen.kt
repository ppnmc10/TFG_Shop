package com.crmv.tfg_shop.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.crmv.tfg_shop.Drawer.coloredShadow
import com.crmv.tfg_shop.Drawer.component.CustomDrawer
import com.crmv.tfg_shop.Drawer.model.CustomDrawerState
import com.crmv.tfg_shop.Drawer.model.NavigationItem
import com.crmv.tfg_shop.Drawer.model.isOpened
import com.crmv.tfg_shop.Drawer.model.opposite
import com.crmv.tfg_shop.R
import com.crmv.tfg_shop.ui.theme.Coral
import com.crmv.tfg_shop.ui.theme.LightYellow
import com.crmv.tfg_shop.viewModel.LoginScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.skydoves.landscapist.glide.GlideImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.io.path.Path
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(navController: NavController) {
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }
    var selectedNavigationItem by remember { mutableStateOf(NavigationItem.Profile) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current.density

    val screenWidth = remember {
        derivedStateOf { (configuration.screenWidthDp * density).roundToInt() }
    }
    val offsetValue by remember { derivedStateOf { (screenWidth.value / 4.5).dp } }
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp,
        label = "Animated Offset"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f,
        label = "Animated Scale"
    )

    BackHandler(enabled = drawerState.isOpened()) {
        drawerState = CustomDrawerState.Closed
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        CustomDrawer(
            selectedNavigationItem = selectedNavigationItem,
            onNavigationItemClick = {
                selectedNavigationItem = it
            },
            onCloseClick = { drawerState = CustomDrawerState.Closed },
            navController = navController
        )

        MainContentProfile(
            modifier = Modifier
                .offset(x = animatedOffset)
                .scale(scale = animatedScale)
                .coloredShadow(
                    color = Color.Black,
                    alpha = 0.1f,
                    shadowRadius = 50.dp
                ),
            drawerState = drawerState,
            onDrawerClick = { drawerState = it },
            navController = navController,
            viewModel = LoginScreenViewModel()
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun MainContentProfile(
    modifier: Modifier = Modifier,
    drawerState: CustomDrawerState,
    onDrawerClick: (CustomDrawerState) -> Unit,
    navController: NavController, viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance().reference

    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf<String?>(null) }
    var userProducts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var userFavorites by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var selectedProduct by remember { mutableStateOf<Map<String, Any>?>(null) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var currentCameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var showingFavorites by remember { mutableStateOf(false) }
    val context = LocalContext.current





    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            currentUser?.uid?.let { userId ->
                // Subir la imagen a Firebase Storage
                val fileName = "profile_pictures/${userId}_${System.currentTimeMillis()}.jpg"
                val imageRef = storage.child(fileName)
                imageRef.putFile(uri)
                    .addOnSuccessListener {
                        // Obtener la URL de descarga de la imagen subida
                        imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            // Actualizar la URL de la imagen en Firestore
                            db.collection("users").document(userId)
                                .update("photoUrl", downloadUrl.toString())
                                .addOnSuccessListener {
                                    userPhotoUrl = downloadUrl.toString()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("ProfileScreen", "Error updating user photo", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("ProfileScreen", "Error uploading photo to Storage", e)
                    }
            }
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { userId ->
            db.collection("products").whereEqualTo("userId", userId).get()
                .addOnSuccessListener { result ->
                    userProducts = result.documents.map { doc ->
                        doc.data?.plus("id" to doc.id) ?: mapOf()
                    }
                }
        }
    }

    // Obtener productos y favoritos desde Firestore
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("name") ?: "Nombre no disponible"
                    userEmail = document.getString("email") ?: "Email no disponible"
                    userPhotoUrl = document.getString("photoUrl")
                    val favorites = document.get("favorites") as? List<Map<String, Any>> ?: emptyList()
                    userFavorites = favorites
                }
                .addOnFailureListener {
                    userName = "Error al obtener nombre"
                    userEmail = "Error al obtener email"
                }
            db.collection("products").whereEqualTo("userId", userId).get()
                .addOnSuccessListener { result ->
                    val productsList = mutableListOf<Map<String, Any>>()
                    for (document in result) {
                        document.data?.let { product ->
                            productsList.add(product)
                        }
                    }
                    userProducts = productsList
                }
                .addOnFailureListener { e ->
                    Log.w("ProfileScreen", "Error getting products: ", e)
                }
        }
    }

    fun AddFavorite(product: Map<String, Any>) {
        currentUser?.uid?.let { userId ->
            val userRef = db.collection("users").document(userId)
            if ((product["id"] as String) in userFavorites.map { it["id"] as String }) {
                userRef.update("favorites", FieldValue.arrayRemove(product))
                    .addOnSuccessListener {
                        userFavorites = userFavorites.filterNot { it["id"] == product["id"] }
                    }
            } else {
                userRef.update("favorites", FieldValue.arrayUnion(product))
                    .addOnSuccessListener {
                        userFavorites = userFavorites + product
                    }
            }
        }
    }

    fun deleteProduct(product: Map<String, Any>) {
        currentUser?.uid?.let { userId ->
            db.collection("products").document(product["id"].toString()).delete()
                .addOnSuccessListener {
                    userProducts = userProducts.filterNot { it["id"] == product["id"] }
                }
                .addOnFailureListener { e ->
                    Log.w("ProfileScreen", "Error deleting product: ", e)
                }
        }
    }

    Scaffold(

        modifier = modifier
            .clickable(enabled = drawerState == CustomDrawerState.Opened) {
                onDrawerClick(CustomDrawerState.Closed)
            },
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                navigationIcon = {
                    IconButton(onClick = { onDrawerClick(drawerState.opposite()) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Icon"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddProductDialog = true },
            ) {
                Icon(Icons.Filled.Add, "Agregar producto", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), // Baja la imagen de perfil para que no sea cubierta por el TopAppBar
                contentAlignment = Alignment.TopCenter
            ) {
                userPhotoUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = stringResource(id = R.string.app_name),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.Center)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .clickable {
                                galleryLauncher.launch("image/*")
                            }
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = stringResource(id = R.string.app_name),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.Center)
                        .padding(8.dp)
                        .clickable {
                            galleryLauncher.launch("image/*")
                        }
                        .graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                        }
                        .drawWithCache {
                            val path = androidx.compose.ui.graphics.Path()
                            path.addOval(
                                Rect(
                                    topLeft = Offset.Zero,
                                    bottomRight = Offset(size.width, size.height)
                                )
                            )
                            onDrawWithContent {
                                clipPath(path) {
                                    this@onDrawWithContent.drawContent()
                                }
                                val dotSize = size.width / 6f
                                drawCircle(
                                    Color.Black,
                                    radius = dotSize,
                                    center = Offset(
                                        x = size.width - dotSize,
                                        y = size.height - dotSize
                                    ),
                                    blendMode = BlendMode.Clear
                                )
                                drawCircle(
                                    Color(0xFFCCC2DC), radius = dotSize * 0.8f,
                                    center = Offset(
                                        x = size.width - dotSize,
                                        y = size.height - dotSize
                                    )
                                )
                            }
                        }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showingFavorites) "Favoritos" else "Productos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { showingFavorites = !showingFavorites }) {
                    Text(text = if (showingFavorites) "Ver productos" else "Ver favoritos")
                }
            }

            if (showingFavorites) {
                GridView(userFavorites, onItemClick = { selectedProduct = it }, onDeleteClick = { AddFavorite(it) })
            } else {
                GridView(
                    products = userProducts,
                    onItemClick = { product ->
                        navController.navigate("ProductDetail/${product["id"]}")
                    },
                    onDeleteClick = { product ->
                        deleteProduct(product)
                    }
                )
            }


            AnimatedVisibility(
                visible = showAddProductDialog,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AddProductDialog(
                    onDismiss = { showAddProductDialog = false },
                    onSave = { newProduct ->
                        currentUser?.uid?.let { userId ->
                            val productData = newProduct.toMutableMap()
                            productData["userId"] = userId
                            db.collection("products").add(productData)
                        }
                        showAddProductDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun GridView(products: List<Map<String, Any>>, onItemClick: (Map<String, Any>) -> Unit, onDeleteClick: (Map<String, Any>) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                items(products) { product ->
                    val imageUrl = product["images"]?.let { it as List<String> }?.firstOrNull()

                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        if (imageUrl != null) {
                            GlideImage(
                                imageModel = imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)  // Adjust height as needed
                                    .clip(RoundedCornerShape(20.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.Gray),  // Placeholder color
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No Image", color = Color.White)
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            IconButton(
                                onClick = { onDeleteClick(product) }, // Pass the product to onDeleteClick
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(50))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun DetailView(
    product: Map<String, Any>,
    onBack: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isFavorite: Boolean,
    modifier: Modifier
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
            IconButton(onClick = onBack) {
                androidx.compose.material.Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
        GlideImage(
            imageModel = product["images"]?.let { it as List<String> }?.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
        )
        Spacer(Modifier.height(16.dp))
        androidx.compose.material.Text(
            text = product["name"].toString(),
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        androidx.compose.material.Text(
            text = product["description"].toString(),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            IconButton(onClick = onFavoriteToggle) {
                androidx.compose.material.Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddProductDialog(onDismiss: () -> Unit, onSave: (Map<String, Any>) -> Unit) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(false) }
    var isFemale by remember { mutableStateOf(false) }
    val imageUris = remember { mutableStateListOf<Uri>() }
    val context = LocalContext.current
    var currentCameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val storage = FirebaseStorage.getInstance().reference

    // ShaderBox
    val shaderBrush = Brush.linearGradient(
        colors = listOf(LightYellow, Coral),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        imageUris.addAll(uris)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Add the image from camera to the list
            currentCameraImageUri?.let { imageUris.add(it) }
        }
    }

    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun takePicture() {
        val photoFile: File = createImageFile()
        currentCameraImageUri = FileProvider.getUriForFile(
            context,
            "com.your.package.fileprovider",
            photoFile
        )
        cameraLauncher.launch(currentCameraImageUri)
    }

    fun validateInputs(): Boolean {
        return productName.isNotBlank() && productDescription.isNotBlank() && productPrice.isNotBlank() && (isMale || isFemale)
    }

    fun uploadImagesAndSaveProduct() {
        if (!validateInputs()) {
            Toast.makeText(context, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUris.isNotEmpty()) {
            val imageUrls = mutableListOf<String>()
            imageUris.forEachIndexed { index, uri ->
                val fileName = "images/${System.currentTimeMillis()}_${index}.jpg"
                val imageRef = storage.child(fileName)
                imageRef.putFile(uri)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            imageUrls.add(downloadUrl.toString())
                            if (imageUrls.size == imageUris.size) {
                                onSave(
                                    mapOf(
                                        "name" to productName,
                                        "description" to productDescription,
                                        "price" to productPrice,
                                        "images" to imageUrls,
                                        "category" to when {
                                            isMale && isFemale -> "unisex"
                                            isMale -> "male"
                                            isFemale -> "female"
                                            else -> ""
                                        }
                                    )
                                )
                                onDismiss()
                            }
                        }
                    }
                    .addOnFailureListener {
                        // Handle any errors
                    }
            }
        } else {
            onSave(
                mapOf(
                    "name" to productName,
                    "description" to productDescription,
                    "price" to productPrice,
                    "images" to emptyList<String>(),
                    "category" to when {
                        isMale && isFemale -> "unisex"
                        isMale -> "male"
                        isFemale -> "female"
                        else -> ""
                    }
                )
            )
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agregar Producto",
                modifier = Modifier// Cambiar el color del texto a blanco
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Nombre") }, // Cambiar el color del texto a blanco
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    label = { Text("Talla") }, // Cambiar el color del texto a blanco
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    label = { Text("Precio") }, // Cambiar el color del texto a blanco
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Checkboxes para seleccionar Hombre o Mujer
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isMale,
                        onCheckedChange = {
                            isMale = it
                            if (isMale && isFemale) {
                                isFemale = false
                            }
                        }
                    )
                    Text(text = "Hombre")
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(
                        checked = isFemale,
                        onCheckedChange = {
                            isFemale = it
                            if (isMale && isFemale) {
                                isMale = false
                            }
                        }
                    )
                    Text(text = "Mujer")
                }

                LazyRow(
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(imageUris.size) { index ->
                        val uri = imageUris[index]
                        GlideImage(
                            imageModel = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .padding(8.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(imageVector = Icons.Filled.Image, contentDescription = "Galería") // Cambiar el color del icono a blanco
                    }
                    IconButton(onClick = { takePicture() }) {
                        Icon(imageVector = Icons.Filled.Camera, contentDescription = "Cámara") // Cambiar el color del icono a blanco
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                uploadImagesAndSaveProduct()
            },
                colors = ButtonDefaults.buttonColors(
                     // Establecer el color de fondo del botón como transparente
                    contentColor = Color.White,
                    // Cambiar el color del contenido del botón a blanco
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(50.dp)) // Redondear el botón
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Text("Guardar", color = Color.White) // Cambiar el color del texto a blanco
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar") // Cambiar el color del texto a blanco
            }
        },
    )
}
