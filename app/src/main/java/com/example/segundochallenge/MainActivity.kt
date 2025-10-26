package com.example.segundochallenge

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ShopApp() }
    }
}

private object Routes {
    const val HOME = "home"
    const val MENU = "menu"
    const val SHOP_LIST = "shop_list"
    const val DETAIL = "detail"
    const val FAVOURITES = "favourites"

    const val SETTINGS = "settings"

    const val PROFILE = "profile"
}

@Parcelize
data class Product(
    val title: String,
    val price: String,
    val description: String,
    val imageUrl: String? = null,  // remotas
    val imageRes: Int? = null      // drawable local
) : Parcelable

/* ---------- VM de favoritos ---------- */
class FavouritesViewModel : ViewModel() {
    // lista observable por Compose
    val items = mutableStateListOf<Product>()
    fun add(p: Product) = items.add(p)
    fun removeAt(i: Int) = items.removeAt(i)
    fun clear() = items.clear()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopApp() {
    val bg = Color(0xFFF4ECEA)
    val brown = Color(0xFF7A3F2C)
    val blueLight = Color(0xFF8CA7D6)
    val peach = Color(0xFFFFE0D6)

    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Product) }
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route ?: Routes.HOME

    // VM de favoritos disponible en todo el árbol
    val favVM: FavouritesViewModel = viewModel()

    val titleInTopBar = when (route) {
        Routes.SHOP_LIST -> "Shop list"
        Routes.MENU -> "Menu"
        Routes.DETAIL -> nav.currentBackStackEntry?.savedStateHandle?.get<String>("title") ?: "Item"
        Routes.FAVOURITES -> "Favourites"
        Routes.SETTINGS -> "Settings"
        Routes.PROFILE -> "Profile"
        else -> "TITLE"
    }

    val showBottomBar = route != Routes.MENU

    MaterialTheme {
        Scaffold(
            containerColor = bg,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(titleInTopBar, color = Color(0xFF333333)) },
                    navigationIcon = {
                        val showBack = route == Routes.MENU || route == Routes.PROFILE
                        IconButton(onClick = {
                            if (showBack) nav.popBackStack()
                            else nav.navigate(Routes.MENU)
                        }) {
                            Icon(
                                if (showBack) Icons.AutoMirrored.Outlined.ArrowBack else Icons.Outlined.Menu,
                                contentDescription = if (showBack) "Back" else "Menu"
                            )
                        }
                    },
                    actions = {
                        if (route != Routes.MENU) {
                            IconButton(onClick = { /* perfil */ }) {
                                Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        scrolledContainerColor = Color.White
                    )
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    Surface(
                        tonalElevation = 6.dp,
                        shadowElevation = 6.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ) {
                        BottomAppBar(
                            containerColor = Color.White,
                            actions = {
                                BottomBarItem(
                                    icon = Icons.Outlined.Home,
                                    label = "Product",
                                    selected = selectedTab == BottomTab.Product,
                                    selectedColor = brown,
                                    unselectedColor = blueLight,
                                    onClick = { selectedTab = BottomTab.Product }
                                )
                                BottomBarItem(
                                    icon = Icons.Outlined.Search,
                                    label = "Search",
                                    selected = selectedTab == BottomTab.Search,
                                    selectedColor = brown,
                                    unselectedColor = blueLight,
                                    onClick = { selectedTab = BottomTab.Search }
                                )
                                Spacer(Modifier.weight(1f))
                                BottomBarItem(
                                    icon = Icons.Outlined.ShoppingCart,
                                    label = "Cart",
                                    selected = selectedTab == BottomTab.Cart,
                                    selectedColor = brown,
                                    unselectedColor = blueLight,
                                    onClick = { selectedTab = BottomTab.Cart }
                                )
                                BottomBarItem(
                                    icon = Icons.Outlined.Person,
                                    label = "Profile",
                                    selected = selectedTab == BottomTab.Profile,
                                    selectedColor = brown,
                                    unselectedColor = blueLight,
                                    onClick = {
                                        selectedTab = BottomTab.Profile
                                        nav.navigate(Routes.PROFILE) { launchSingleTop = true }
                                    }
                                )
                            },
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = { /* TODO */ },
                                    shape = CircleShape,
                                    containerColor = brown,
                                    contentColor = Color.White,
                                    elevation = FloatingActionButtonDefaults.elevation(
                                        defaultElevation = 8.dp,
                                        pressedElevation = 12.dp
                                    ),
                                    modifier = Modifier.shadow(12.dp, CircleShape)
                                ) {
                                    Icon(Icons.Outlined.Storefront, contentDescription = "Store")
                                }
                            }
                        )
                    }
                }
            }
        ) { inner ->
            Box(Modifier.padding(inner).fillMaxSize().background(bg)) {
                NavHost(navController = nav, startDestination = Routes.HOME) {

                    composable(Routes.HOME) { Box(Modifier.fillMaxSize().background(bg)) }

                    composable(Routes.MENU) {
                        MenuScreen(
                            bg = bg,
                            brown = brown,
                            peach = peach,
                            favouritesCount = favVM.items.size,
                            onShopListClick = { nav.navigate(Routes.SHOP_LIST) },
                            onFavouritesClick = { nav.navigate(Routes.FAVOURITES) },
                            onSettingsClick = { nav.navigate(Routes.SETTINGS) },
                            onProfileClick = { nav.navigate(Routes.PROFILE) }
                        )
                    }

                    composable(Routes.SHOP_LIST) {
                        ShopListScreen(
                            bg = bg,
                            brown = brown,
                            onAddFavourite = { prod ->
                                favVM.add(prod)
                                nav.navigate(Routes.FAVOURITES) {
                                    launchSingleTop = true
                                }
                            },
                            onBuy = { prod ->
                                nav.currentBackStackEntry?.savedStateHandle?.set("product", prod)
                                nav.currentBackStackEntry?.savedStateHandle?.set("title", prod.title)
                                nav.navigate(Routes.DETAIL)
                            }
                        )
                    }

                    composable(Routes.DETAIL) {
                        val product =
                            nav.previousBackStackEntry?.savedStateHandle?.get<Product>("product")
                        ItemDetailScreen(
                            product = product,
                            brown = brown,
                            onBack = { nav.popBackStack() }
                        )
                    }

                    composable(Routes.FAVOURITES) {
                        FavouritesScreen(
                            bg = bg,
                            brown = brown,
                            items = favVM.items
                        )
                    }

                    composable(Routes.SETTINGS) {
                        SettingsScreen(bg = bg, brown = brown)
                    }

                    composable(Routes.PROFILE) {
                        ProfileScreen(bg = bg, brown = brown)
                    }
                }
            }
        }
    }
}

/* ---------------- Pantalla del menú ---------------- */

@Composable
private fun MenuScreen(
    bg: Color,
    brown: Color,
    peach: Color,
    favouritesCount: Int,
    onShopListClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(bg).padding(16.dp)
    ) {
        Text("Title", fontSize = 22.sp, color = Color(0xFF3A3A3A))
        Spacer(Modifier.height(20.dp))
        Text("Section Header", fontSize = 14.sp, color = Color(0xFF7B6F6B))
        Spacer(Modifier.height(12.dp))

        val interaction = remember { MutableInteractionSource() }
        val pressed by interaction.collectIsPressedAsState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(if (pressed) Color(0xFFFFE0D6) else Color.Transparent)
                .clickable(interactionSource = interaction, indication = null) { onShopListClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(brown))
            Spacer(Modifier.width(12.dp))
            Text("Shop list", color = Color(0xFF2B2B2B))
        }

        Spacer(Modifier.height(20.dp))

        MenuRow(
            icon = Icons.Outlined.ChangeHistory,
            label = "Favourites",
            tint = brown,
            trailing = favouritesCount.toString(),
            onClick = onFavouritesClick
        )
        MenuRow(icon = Icons.Outlined.Person, label = "Profile", tint = brown, onClick = onProfileClick)
        MenuRow(icon = Icons.Outlined.Settings, label = "Settings", tint = brown, onClick = onSettingsClick)
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    label: String,
    tint: Color,
    trailing: String? = null,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint)
        Spacer(Modifier.width(16.dp))
        Text(label, color = Color(0xFF3A2E2A))
        Spacer(Modifier.weight(1f))
        if (trailing != null) Text(trailing, color = Color(0xFF3A2E2A))
    }
}

/* --------------- Lista de productos ---------------- */

@Composable
private fun ShopListScreen(
    bg: Color,
    brown: Color,
    onAddFavourite: (Product) -> Unit,
    onBuy: (Product) -> Unit
) {
    val products = remember {
        listOf(
            Product(
                title = "Leather boots",
                price = "27,5 $",
                description = "Great warm shoes from the artificial leather. You can buy this model only in our shop",
                imageRes = R.drawable.leather_boots
            ),
            Product(
                title = "Casual sneakers",
                price = "19,9 $",
                description = "Comfortable everyday sneakers. Lightweight and durable.",
                imageUrl = "https://images.unsplash.com/photo-1549298916-b41d501d3772?q=80&w=1200&auto=format&fit=crop"
            ),
            Product(
                title = "Classic loafers",
                price = "34,0 $",
                description = "Elegant loafers suitable for office and casual wear.",
                imageUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=1200&auto=format&fit=crop"
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(bg).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { prod ->
            ProductCard(
                prod = prod,
                brown = brown,
                onAddFavourite = onAddFavourite,
                onBuy = onBuy
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun ProductCard(
    prod: Product,
    brown: Color,
    onAddFavourite: (Product) -> Unit,
    onBuy: (Product) -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val model: Any? = prod.imageRes ?: prod.imageUrl
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(model).crossfade(true).build(),
            contentDescription = prod.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )
        Column(Modifier.padding(16.dp)) {
            Text(prod.title, fontSize = 18.sp, color = Color(0xFF2B2B2B))
            Spacer(Modifier.height(4.dp))
            Text(prod.price, color = Color(0xFF6A6A6A))
            Spacer(Modifier.height(12.dp))
            Text(prod.description, color = Color(0xFF6A6A6A))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { onAddFavourite(prod) }, // <-- agrega a favoritos
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = brown)
                ) { Text("Add to favourite") }
                Button(
                    onClick = { onBuy(prod) },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = brown, contentColor = Color.White)
                ) { Text("Buy") }
            }
        }
    }
}

/* --------------- Pantalla de FAVOURITES ---------------- */

@Composable
private fun FavouritesScreen(bg: Color, brown: Color, items: List<Product>) {
    Column(
        Modifier.fillMaxSize().background(bg).padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    Text("No favourites yet", color = Color(0xFF6A6A6A))
                }
            } else {
                itemsIndexed(items) { idx, p ->
                    FavouriteRow(index = idx + 1, product = p, brown = brown)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        // Botón centrado “+ Buy”
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = { /* TODO: ir a cart o comprar */ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = brown, contentColor = Color.White)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add")
                Spacer(Modifier.width(6.dp))
                Text("Buy")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun FavouriteRow(index: Int, product: Product, brown: Color) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // círculo con número
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(brown),
                contentAlignment = Alignment.Center
            ) { Text(index.toString(), color = Color.White) }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.title, color = Color(0xFF2B2B2B))
                Spacer(Modifier.height(2.dp))
                Text(product.price, color = Color(0xFF6A6A6A))
            }

            val model: Any? = product.imageRes ?: product.imageUrl
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(model).crossfade(true).build(),
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp))
            )
        }
    }
}

/* ---------------- Pantalla de detalle ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemDetailScreen(product: Product?, brown: Color, onBack: () -> Unit) {
    val bg = Color(0xFFF4ECEA)
    val sizes = listOf("36","37","38","39","40","41","42","43","44")

    var expanded by remember { mutableStateOf(false) }
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var count by rememberSaveable { mutableStateOf("1") }

    Column(
        Modifier.fillMaxSize().background(bg).padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Select size", fontSize = 20.sp, color = Color(0xFF2B2B2B))
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = selectedSize ?: "Input",
                onValueChange = {},
                label = { Text("Label") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                sizes.forEach { size ->
                    DropdownMenuItem(text = { Text(size) }, onClick = {
                        selectedSize = size; expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        Text("Count of product", fontSize = 20.sp, color = Color(0xFF2B2B2B))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = count,
            onValueChange = { if (it.all { ch -> ch.isDigit() }) count = it },
            label = { Text("Input") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = brown)
            ) { Text("Back") }
            Button(
                onClick = { /* TODO: confirmar compra */ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = brown, contentColor = Color.White)
            ) { Text("Buy") }
        }
    }
}

/* ---------------- Bottom bar ---------------- */

private enum class BottomTab { Product, Search, Cart, Profile }

@Composable
private fun RowScope.BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
    val tint = if (selected) selectedColor else unselectedColor
    Column(
        modifier = Modifier.clickable { onClick() }.padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint)
        Spacer(Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp, color = tint)
    }
}

@Composable
private fun SettingsScreen(bg: Color, brown: Color) {
    var push by rememberSaveable { mutableStateOf(true) }
    var dark by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // ---- Account Settings ----
        Text("Account Settings", color = Color(0xFFAA9F99))
        Spacer(Modifier.height(8.dp))

        SettingsRow(label = "Edit profile", onClick = { /* TODO */ })
        Divider(thickness = 0.6.dp, color = Color(0x1F000000))
        SettingsRow(label = "Change password", onClick = { /* TODO */ })
        Divider(thickness = 0.6.dp, color = Color(0x1F000000))

        SettingsRow(label = "Push notifications") {
            Switch(
                checked = push,
                onCheckedChange = { push = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = brown,
                    uncheckedThumbColor = Color.White
                )
            )
        }
        Divider(thickness = 0.6.dp, color = Color(0x1F000000))

        SettingsRow(label = "Dark mode") {
            Switch(
                checked = dark,
                onCheckedChange = { dark = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = brown,
                    uncheckedThumbColor = Color.White
                )
            )
        }

        // ---- Separador de secciones ----
        Spacer(Modifier.height(16.dp))
        Divider(thickness = 0.8.dp, color = Color(0x1F000000))
        Spacer(Modifier.height(12.dp))

        // ---- More ----
        Text("More", color = Color(0xFFAA9F99))
        Spacer(Modifier.height(8.dp))

        SettingsRow(label = "About us", onClick = { /* TODO */ })
        Divider(thickness = 0.6.dp, color = Color(0x1F000000))
        SettingsRow(label = "Privacy policy", onClick = { /* TODO */ })
        Divider(thickness = 0.6.dp, color = Color(0x1F000000))
        SettingsRow(label = "Terms and conditions", onClick = { /* TODO */ })
    }
}

@Composable
private fun SettingsRow(
    label: String,
    onClick: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), color = Color(0xFF2B2B2B))
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                Icons.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0x99000000)
            )
        }
    }
}

@Composable
private fun ProfileScreen(bg: Color, brown: Color) {
    Column(
        Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar con “editar”
        Box(
            modifier = Modifier
                .size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // círculo “avatar”
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE9E2DF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Avatar",
                    tint = Color(0xFF9B8F8A),
                    modifier = Modifier.size(96.dp)
                )
            }
            // lapicito
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(brown),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Alejandro", color = Color(0xFF2B2B2B), fontSize = 20.sp)
        Text("UI UX DESIGN", color = Color(0xFF9B8F8A), fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))

        ProfileField(
            label = "E-Mail Address",
            value = "xxx@gmail.com",
            leading = { Icon(Icons.Outlined.AlternateEmail, null) },
            trailing = { Icon(Icons.Outlined.MailOutline, null) }
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            label = "Phone Number",
            value = "+5493123135",
            leading = { Icon(Icons.Outlined.Call, null) },
            trailing = { Icon(Icons.Outlined.Phone, null) }
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            label = "Web Site",
            value = "www.google.com",
            leading = { Icon(Icons.Outlined.Public, null) },
            trailing = { Icon(Icons.Outlined.Settings, null) }
        )
        Spacer(Modifier.height(10.dp))
        ProfileField(
            label = "Password",
            value = "xxxxxxxxxxxx",
            leading = { Icon(Icons.Outlined.Lock, null) },
            trailing = { Icon(Icons.Outlined.VisibilityOff, null) }
        )
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        leadingIcon = leading,
        trailingIcon = trailing,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
    )
}