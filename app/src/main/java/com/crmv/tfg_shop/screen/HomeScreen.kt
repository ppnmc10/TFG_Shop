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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.crmv.tfg_shop.Drawer.component.CustomDrawer
import com.crmv.tfg_shop.Drawer.model.CustomDrawerState
import com.crmv.tfg_shop.Drawer.model.NavigationItem
import com.crmv.tfg_shop.Drawer.model.isOpened
import com.crmv.tfg_shop.Drawer.model.opposite
import com.crmv.tfg_shop.R
import com.crmv.tfg_shop.navigation.AppScreen
import com.crmv.tfg_shop.screen.HombreScreen
import com.crmv.tfg_shop.ui.theme.Coral
import com.crmv.tfg_shop.ui.theme.LightYellow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.skydoves.landscapist.glide.GlideImage
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(navController: NavController) {
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

        MainContentHome(
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
fun MainContentHome(
    modifier: Modifier = Modifier,
    drawerState: CustomDrawerState,
    onDrawerClick: (CustomDrawerState) -> Unit,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var allProducts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var selectedProduct by remember { mutableStateOf<Map<String, Any>?>(null) }

    val contextForToast = LocalContext.current.applicationContext

    // Obtener todos los productos desde Firestore
    LaunchedEffect(Unit) {
        db.collection("products").get()
            .addOnSuccessListener { result ->
                allProducts = result.documents.map { doc ->
                    doc.data?.plus("id" to doc.id) ?: mapOf()
                }.filterNot { product ->
                    // Filtrar los productos del usuario actual
                    product["userId"] == currentUser?.uid
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
                title = { Text(text = "Home") },
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
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            item {
                                // Sección "Chica"
                                SectionHombre(
                                    imageRes = R.drawable.chica,
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .size(150.dp),
                                    navController = navController
                                )
                            }
                            item {
                                // Sección "Chico"
                                SectionMujer(
                                    imageRes = R.drawable.chico,
                                    modifier = Modifier
                                        .size(150.dp),
                                    navController = navController
                                )
                            }
                        }
                        Divider(modifier = Modifier.padding(top = 16.dp))
                    }

                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)) {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Fixed(2), // Dos columnas
                                verticalItemSpacing = 4.dp,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    items(allProducts.size) { index ->
                                        val product = allProducts[index]
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


@Composable
fun SectionHombre(
    imageRes: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .clickable { navController.navigate(AppScreen.HombreScreen.route) }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
        )
        Button(
            onClick = { navController.navigate(AppScreen.HombreScreen.route) },
            modifier = Modifier
                .background(Color.Transparent)
        ) {
            Text(text = "Hombre")
        }
    }
}


@Composable
fun SectionMujer(
    imageRes: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .clickable { navController.navigate(AppScreen.MujerScreen.route) }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
        )
        Button(
            onClick = { navController.navigate(AppScreen.HombreScreen.route) },
            modifier = Modifier
                .background(Color.Transparent)
        ) {
            Text(text = "Mujer")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ProductDetailScreen(
    product: Map<String, Any>,
    onAddToCartClick: (Map<String, Any>) -> Unit,
    onBackClick: () -> Unit,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var isFavorite by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(0) }
        var userName by remember { mutableStateOf("") }
        var userPhotoUrl by remember { mutableStateOf<String?>(null) }
    var isInCart by remember { mutableStateOf(false) }
    val contextForToast = LocalContext.current.applicationContext


    // Verificar si el producto ya está en la lista de favoritos y obtener contador de me gustas
    LaunchedEffect(product) {
        currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val favorites = document.get("favorites") as? List<Map<String, Any>> ?: emptyList()
                    isFavorite = favorites.any { it["id"] == product["id"] }
                }

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val cart = document.get("cart") as? List<Map<String, Any>> ?: emptyList()
                    isInCart = cart.any { it["id"] == product["id"] }
                }
        }

        // Obtener información del usuario que publicó el producto
        val userId = product["userId"]?.toString()
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    userName = document.getString("name") ?: ""
                    userPhotoUrl = document.getString("photoUrl")
                }
        }

        // Obtener contador de me gustas
        val productId = product["id"]?.toString()
        if (productId != null) {
            db.collection("products").document(productId).get()
                .addOnSuccessListener { document ->
                    likesCount = (document.get("likesCount") as? Long)?.toInt() ?: 0
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = product["name"].toString()) },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val images = product["images"] as? List<String> ?: emptyList()
            Box {
                if (images.isNotEmpty()) {
                    val pagerState = rememberPagerState()
                    HorizontalPager(
                        count = images.size,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) { page ->
                        GlideImage(
                            imageModel = images[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                } else {
                    GlideImage(
                        imageModel = product["images"]?.let { it as List<String> }?.firstOrNull(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.White, RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White, RoundedCornerShape(50))
                ) {
                    IconButton(
                        onClick = {
                            currentUser?.uid?.let { userId ->
                                val userRef = db.collection("users").document(userId)
                                val productRef = db.collection("products").document(product["id"].toString())
                                if (isFavorite) {
                                    userRef.update("favorites", FieldValue.arrayRemove(product))
                                        .addOnSuccessListener {
                                            isFavorite = false
                                            likesCount--
                                            productRef.update("likesCount", FieldValue.increment(-1))
                                        }
                                } else {
                                    userRef.update("favorites", FieldValue.arrayUnion(product))
                                        .addOnSuccessListener {
                                            isFavorite = true
                                            likesCount++
                                            productRef.update("likesCount", FieldValue.increment(1))
                                        }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Black else Color.Gray
                        )
                    }
                    Text(text = likesCount.toString(), modifier = Modifier.padding(end = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(modifier = Modifier.padding(bottom = 16.dp))

            // Información del usuario que publicó el producto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                    navController.navigate("ChatScreen/${product["userId"]}")                }
            ) {
                if (userPhotoUrl != null) {
                    Image(
                        painter = rememberImagePainter(data = userPhotoUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("ChatScreen/${product["userId"]}")
                        },
                        modifier = Modifier
                            .size(48.dp) // Tamaño del círculo
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = product["name"].toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = "$${product["price"].toString()}",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = product["description"].toString(),
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (isInCart) {
                        currentUser?.uid?.let { userId ->
                            val userRef = db.collection("users").document(userId)
                            userRef.update("cart", FieldValue.arrayRemove(product))
                                .addOnSuccessListener {
                                    isInCart = false
                                    Toast.makeText(contextForToast,"Product deleted form cart", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        onAddToCartClick(product)
                        currentUser?.uid?.let { userId ->
                            val userRef = db.collection("users").document(userId)
                            userRef.update("cart", FieldValue.arrayUnion(product))
                                .addOnSuccessListener {
                                    isInCart = true
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(50.dp)) // Redondear el botón
                    .background(MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.elevatedButtonElevation(20.dp)
            ) {
                Text(text = if (isInCart) "Remove from Cart" else "Add to Cart")
            }
        }
    }
}
