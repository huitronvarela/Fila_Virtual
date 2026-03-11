package com.example.fila_virtual.features.user.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fila_virtual.core.data.Usuario

@Composable
fun HomeView(usuario: Usuario?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        HomeHeader(nombre = usuario?.nombre ?: "Bienvenido")
        SearchBar()
        SectionHeader(title = "Categorías", actionText = "VER TODAS")
        CategoryList()
        Spacer(modifier = Modifier.height(24.dp))
        SectionHeader(title = "Establecimientos destacados", actionText = "VER TODOS")
        EstablishmentList()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HomeHeader(nombre: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Hola,", color = Color.Gray, fontSize = 16.sp)
            Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
        }
        
        Box {
            Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = Color(0xFFF5F5F7)) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.padding(10.dp), tint = Color(0xFF2D3436))
            }
            Surface(modifier = Modifier.size(18.dp).align(Alignment.TopEnd), shape = CircleShape, color = Color(0xFFFF5722)) {
                Text("2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.wrapContentSize(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
        placeholder = { Text("¿Qué se te antoja hoy?", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F7),
            unfocusedContainerColor = Color(0xFFF5F5F7),
            focusedBorderColor = Color(0xFFFF5722),
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color(0xFFFF5722)
        )
    )
}

@Composable
fun SectionHeader(title: String, actionText: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        Text(text = actionText, color = Color(0xFFFF5722), fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun CategoryList() {
    val categories = listOf(
        "Pizza" to Icons.Default.LocalPizza,
        "Burgers" to Icons.Default.LunchDining,
        "Sushi" to Icons.Default.SetMeal,
        "Tacos" to Icons.Default.Fastfood,
        "Postres" to Icons.Default.Icecream
    )
    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(categories) { (name, icon) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(modifier = Modifier.size(70.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFFFFF1EE)) {
                    Icon(icon, contentDescription = name, modifier = Modifier.padding(20.dp), tint = Color(0xFFFF5722))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = name, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun EstablishmentList() {
    val places = listOf(
        Place("La Trattoria Italiana", "4.8", "Pizza · Italiana · $$", Icons.Default.Restaurant),
        Place("Big Burger Station", "4.6", "Hamburguesas · Americana · $$", Icons.Default.LunchDining),
        Place("Sakura Sushi House", "4.9", "Sushi · Asiática · $$$", Icons.Default.SetMeal)
    )
    places.forEach { place ->
        EstablishmentCard(place)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class Place(val name: String, val rating: String, val tags: String, val icon: ImageVector)

@Composable
fun EstablishmentCard(place: Place) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxHeight().width(120.dp).background(Color(0xFFF5F5F7)), contentAlignment = Alignment.Center) {
                Icon(place.icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.LightGray)
            }
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                Text(text = place.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = place.rating, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                }
                Text(text = place.tags, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
