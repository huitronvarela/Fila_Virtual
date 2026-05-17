package com.example.fila_virtual.features.user.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.data.Usuario
import com.example.fila_virtual.data.Establecimiento
import com.example.fila_virtual.data.Producto // 👇 IMPORTACIÓN NUEVA PARA TUS PLATILLOS
import com.example.fila_virtual.features.user.UserHomeViewModel

// Importamos tus componentes reutilizables y utilidades
import com.example.fila_virtual.components.InputField
import com.example.fila_virtual.components.SearchBar
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*

@Composable
fun HomeView(
    usuario: Usuario?,
    onCartClick: () -> Unit,
    onEstablecimientoClick: (Establecimiento) -> Unit
) {
    val windowSize = LocalWindowSize.current
    val horizontalPadding = windowSize.adaptiveDp(24).value.dp

    val viewModel = remember { UserHomeViewModel() }
    val categoriasDisponibles by viewModel.categoriasDisponibles.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val establecimientos by viewModel.establecimientosFiltrados.collectAsState()

    // 👇 ESCUCHAMOS LAS RECOMENDACIONES GLOBALES DESDE FIREBASE
    val listaRecomendaciones by viewModel.recomendaciones.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- SECCIÓN FIJA ---
        Spacer(modifier = Modifier.height(16.dp))
        HomeHeader(padding = horizontalPadding, onCartClick = onCartClick)
        Spacer(modifier = Modifier.height(16.dp))

        var searchQuery by remember { mutableStateOf("") }
        Box(modifier = Modifier.padding(horizontal = horizontalPadding)) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- SECCIÓN DESPLAZABLE ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(title = "Categorías", actionText = null, padding = horizontalPadding)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryRow(
                categorias = categoriasDisponibles,
                seleccion = categoriaSeleccionada,
                onSelect = { viewModel.seleccionarCategoria(it) },
                padding = horizontalPadding
            )

            Spacer(modifier = Modifier.height(32.dp))
            SectionHeader(title = "Cafeterías Cercanas", actionText = "Ver todas", padding = horizontalPadding)
            Spacer(modifier = Modifier.height(16.dp))
            CafeteriasList(
                establecimientos = establecimientos,
                padding = horizontalPadding,
                onEstablecimientoClick = onEstablecimientoClick
            )

            Spacer(modifier = Modifier.height(32.dp))
            SectionHeader(title = "Recomendaciones para ti", actionText = null, padding = horizontalPadding)
            Spacer(modifier = Modifier.height(16.dp))

            // 👇 PASAMOS LA LISTA REAL A TU COMPONENTE VISUAL
            RecommendationsList(productosConLocal = listaRecomendaciones, padding = horizontalPadding)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(padding: Dp, onCartClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hola, Bienvenido 👋",
            style = MaterialTheme.typography.titleLarge
        )

        BadgedBox(
            badge = {
                Badge(
                    containerColor = PrimaryOrange,
                    contentColor = Color.White
                ) { Text("3") }
            }
        ) {
            Surface(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable { onCartClick() },
                color = BorderGray.copy(alpha = 0.4f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Carrito",
                    modifier = Modifier.padding(10.dp),
                    tint = DarkGray
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String?, padding: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = LocalWindowSize.current.adaptiveSp(18),
                fontWeight = FontWeight.Bold
            )
        )
        if (actionText != null) {
            Text(
                text = actionText,
                color = PrimaryOrange,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable { /* Acción */ }
            )
        }
    }
}

fun getIconForCategory(categoria: String): ImageVector {
    return when(categoria.lowercase()) {
        "comida", "comidas" -> Icons.Default.Restaurant
        "bebidas", "bebida", "café", "cafe" -> Icons.Default.LocalCafe
        "snacks", "botanas" -> Icons.Default.Fastfood
        "postres", "postre" -> Icons.Default.Icecream
        "todos" -> Icons.Default.Menu
        else -> Icons.Default.Category
    }
}

@Composable
fun CategoryRow(
    categorias: List<String>,
    seleccion: String,
    onSelect: (String) -> Unit,
    padding: Dp
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = padding),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categorias.size) { index ->
            val name = categorias[index]
            val isSelected = name == seleccion

            Column(
                modifier = Modifier.clickable { onSelect(name) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryOrange else LightSurface,
                    shadowElevation = if (isSelected) 4.dp else 2.dp
                ) {
                    Icon(
                        imageVector = getIconForCategory(name),
                        contentDescription = name,
                        modifier = Modifier.padding(20.dp),
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSelected) PrimaryOrange else DarkGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun CafeteriasList(
    establecimientos: List<Establecimiento>,
    padding: Dp,
    onEstablecimientoClick: (Establecimiento) -> Unit
) {
    if (establecimientos.isEmpty()) {
        Text(
            text = "Buscando establecimientos...",
            modifier = Modifier.padding(horizontal = padding),
            color = Color.Gray
        )
    } else {
        LazyRow(
            contentPadding = PaddingValues(horizontal = padding),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(establecimientos.size) { index ->
                CafeteriaCard(
                    establecimiento = establecimientos[index],
                    onClick = { onEstablecimientoClick(establecimientos[index]) }
                )
            }
        }
    }
}

@Composable
fun CafeteriaCard(establecimiento: Establecimiento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ExtraLightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = MediumGray)
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = establecimiento.nombre.ifEmpty { "Local sin nombre" },
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = TrafficYellow)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "4.5 • En el campus", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    establecimiento.categorias.take(2).forEach { tag ->
                        Text(
                            text = tag,
                            modifier = Modifier
                                .background(SoftOrangeBg, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = PrimaryOrange)
                        )
                    }
                }
            }
        }
    }
}

// 👇 LISTA DINÁMICA DE RECOMENDACIONES
@Composable
fun RecommendationsList(productosConLocal: List<Pair<Producto, String>>, padding: Dp) {
    Column(modifier = Modifier.padding(horizontal = padding)) {
        if (productosConLocal.isEmpty()) {
            Text(
                text = "Buscando las mejores recomendaciones...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            productosConLocal.forEach { (producto, nombreLocal) ->
                RecommendationCard(producto = producto, storeName = nombreLocal)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// 👇 TARJETA REAL ENLAZADA A FIREBASE
@Composable
fun RecommendationCard(producto: Producto, storeName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF1E1E24)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = producto.nombre, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Text(text = storeName, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = "$${producto.precio}", color = PrimaryOrange, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                }
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = PrimaryOrange) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                }
            }
        }
    }
}