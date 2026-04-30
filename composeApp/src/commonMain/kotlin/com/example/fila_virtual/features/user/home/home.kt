package com.example.fila_virtual.features.user.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.data.Usuario

// Importamos tus componentes reutilizables y utilidades
import com.example.fila_virtual.components.InputField
import com.example.fila_virtual.core.LocalWindowSize
import com.example.fila_virtual.core.theme.*

@Composable
fun HomeView(usuario: Usuario?) {
    val windowSize = LocalWindowSize.current
    val horizontalPadding = windowSize.adaptiveDp(24).value.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- SECCIÓN FIJA (No se mueve al hacer scroll) ---
        Spacer(modifier = Modifier.height(16.dp))
        HomeHeader(padding = horizontalPadding)
        Spacer(modifier = Modifier.height(16.dp))

        // Uso de tu componente reutilizable InputField
        var searchQuery by remember { mutableStateOf("") }
//        Box(modifier = Modifier.padding(horizontal = horizontalPadding)) {
//            InputField(
//                label = "¿Qué se te antoja hoy?",
//                value = searchQuery,
//                onValueChange = { searchQuery = it },
//                leadingIcon = Icons.Default.Search,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- SECCIÓN DESPLAZABLE (Contenido principal) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader(title = "Categorías", actionText = null, padding = horizontalPadding)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryRow(padding = horizontalPadding)

            Spacer(modifier = Modifier.height(32.dp))
            SectionHeader(title = "Cafeterías Cercanas", actionText = "Ver todas", padding = horizontalPadding)
            Spacer(modifier = Modifier.height(16.dp))
            CafeteriasList(padding = horizontalPadding)

            Spacer(modifier = Modifier.height(32.dp))
            SectionHeader(title = "Recomendaciones para ti", actionText = null, padding = horizontalPadding)
            Spacer(modifier = Modifier.height(16.dp))
            RecommendationsList(padding = horizontalPadding)

            Spacer(modifier = Modifier.height(100.dp)) // Espacio extra para que el contenido no quede oculto tras la Nav Bar
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(padding: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Saludo tal cual lo solicitaste
        Text(
            text = "Hola, Bienvenido 👋",
            style = MaterialTheme.typography.titleLarge
        )

        // Carrito de compras con Badge
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
                    .clickable { /* Acción al abrir carrito */ },
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

@Composable
fun CategoryRow(padding: Dp) {
    val categories = listOf(
        "Comida" to Icons.Default.Restaurant,
        "Bebidas" to Icons.Default.LocalCafe,
        "Snacks" to Icons.Default.Fastfood,
        "Postres" to Icons.Default.Icecream
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        categories.forEach { (name, icon) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = LightSurface,
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        modifier = Modifier.padding(20.dp),
                        tint = Color(0xFFA93226)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(color = DarkGray, fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

@Composable
fun CafeteriasList(padding: Dp) {
    val cafeterias = listOf(
        Cafeteria("Cafetería Juan", "4.8", "1.2 km", listOf("Café", "Postres")),
        Cafeteria("Starbucks Campus", "4.5", "0.5 km", listOf("Bebidas", "Premium"))
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = padding),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cafeterias) { cafe ->
            CafeteriaCard(cafe)
        }
    }
}

@Composable
fun CafeteriaCard(cafe: Cafeteria) {
    Card(
        modifier = Modifier.width(260.dp),
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
                Text(text = cafe.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = TrafficYellow)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${cafe.rating} • ${cafe.distance}", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    cafe.tags.take(2).forEach { tag ->
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

@Composable
fun RecommendationsList(padding: Dp) {
    val products = listOf(
        Product("Hamburguesa Clásica", "El Rincón del Sabor", "$8.50"),
        Product("Croissant de Mantequilla", "Cafetería Juan", "$3.20")
    )

    Column(modifier = Modifier.padding(horizontal = padding)) {
        products.forEach { product ->
            RecommendationCard(product)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun RecommendationCard(product: Product) {
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
                    Text(text = product.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Text(text = product.store, style = MaterialTheme.typography.labelSmall)
                    Text(text = product.price, color = PrimaryOrange, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                }
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = PrimaryOrange) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                }
            }
        }
    }
}

// Modelos auxiliares
data class Cafeteria(val name: String, val rating: String, val distance: String, val tags: List<String>)
data class Product(val name: String, val store: String, val price: String)