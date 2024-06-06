package com.crmv.tfg_shop.screen

import ProductDetailScreen
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.crmv.tfg_shop.Drawer.component.CustomDrawer
import com.crmv.tfg_shop.Drawer.model.CustomDrawerState
import com.crmv.tfg_shop.Drawer.model.NavigationItem
import com.crmv.tfg_shop.Drawer.model.isOpened
import com.crmv.tfg_shop.Drawer.model.opposite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HombreScreen(navController: NavController){
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }
    var selectedNavigationItem by remember { mutableStateOf(NavigationItem.Home) }

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

        MainContentHombre(
            modifier = Modifier
                .offset(x = animatedOffset)
                .scale(scale = animatedScale)
                .shadow(4.dp)
                .clip(RoundedCornerShape(16.dp)),
            drawerState = drawerState,
            onDrawerClick = { drawerState = it },
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainContentHombre(
    modifier: Modifier = Modifier,
    drawerState: CustomDrawerState,
    onDrawerClick: (CustomDrawerState) -> Unit,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var maleProducts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var selectedProduct by remember { mutableStateOf<Map<String, Any>?>(null) }

    val contextForToast = LocalContext.current.applicationContext

    // Obtener todos los productos desde Firestore
    LaunchedEffect(Unit) {
        try {
            val result = db.collection("products")
                .whereEqualTo("category", "male")
                .get()
                .await()

            maleProducts = result.documents.map { doc ->
                doc.data?.plus("id" to doc.id) ?: mapOf()
            }
        } catch (e: Exception) {
            // Manejar error
            println("Error fetching male products: ${e.message}")
        }
    }

    Scaffold(
        modifier = modifier
            .clickable(enabled = drawerState == CustomDrawerState.Opened) {
                onDrawerClick(CustomDrawerState.Closed)
            },
        topBar = {
            TopAppBar(
                title = { Text(text = "Sección de hombre") },
                navigationIcon = {
                    IconButton(onClick = { onDrawerClick(drawerState.opposite()) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Icon"
                        )
                    }
                }
            )
        }
    ) {
        AnimatedContent(targetState = selectedProduct, transitionSpec = { fadeIn() with fadeOut() }) { product ->
            if (product == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            text = "Tienda",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)) {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(1), // Dos columnas
                                verticalItemSpacing = 4.dp,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    items(maleProducts.size) { index ->
                                        val product = maleProducts[index]
                                        Box(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .background(Color.White, RoundedCornerShape(10.dp))
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .clickable {
                                                    selectedProduct = product
                                                }
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(16.dp)
                                            ) {
                                                GlideImage(
                                                    imageModel = product["images"]?.let { it as List<String> }?.firstOrNull(),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1f)
                                                        .clip(RoundedCornerShape(10.dp))
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = product["name"].toString(),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black,
                                                    modifier = Modifier.padding(4.dp)
                                                )
                                                Text(
                                                    text = "$${product["price"].toString()}",
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    modifier = Modifier.padding(4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                ProductDetailScreen(
                    navController = navController,
                    product = product,
                    onAddToCartClick = { productToAdd ->
                        // Agregar el producto al carrito del usuario
                        currentUser?.uid?.let { userId ->
                            db.collection("users").document(userId)
                                .update("cart", FieldValue.arrayUnion(productToAdd))
                                .addOnSuccessListener {
                                    // Producto añadido al carrito exitosamente
                                    Toast.makeText(contextForToast,"Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(contextForToast,"Se produjo un error al agregar el producto al carrito", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    onBackClick = { selectedProduct = null }
                )
            }
        }
    }
}
