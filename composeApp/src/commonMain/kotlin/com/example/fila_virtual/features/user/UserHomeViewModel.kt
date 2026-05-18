package com.example.fila_virtual.features.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fila_virtual.data.Establecimiento
import com.example.fila_virtual.data.Producto
import com.example.fila_virtual.repository.EstablecimientoRepository
import com.example.fila_virtual.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserHomeViewModel : ViewModel() {

    private val establecimientoRepo = EstablecimientoRepository()
    private val productoRepo = ProductoRepository() // 👇 Conectamos los productos

    // 1. Traer TODOS los establecimientos en tiempo real desde Firestore
    private val _todosLosEstablecimientos = establecimientoRepo.getEstablecimientos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Estado para saber qué categoría presionó el usuario
    private val _categoriaSeleccionada = MutableStateFlow("Todos")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada

    // 3. Extraer categorías dinámicamente
    val categoriasDisponibles: StateFlow<List<String>> = _todosLosEstablecimientos.map { lista ->
        val categoriasExtraidas = lista.flatMap { it.categorias }.distinct().sorted()
        listOf("Todos") + categoriasExtraidas
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("Todos")
    )

    // 4. Lista final filtrada que se pintará en la pantalla
    val establecimientosFiltrados: StateFlow<List<Establecimiento>> = combine(
        _todosLosEstablecimientos,
        _categoriaSeleccionada
    ) { lista, categoria ->
        if (categoria == "Todos") {
            lista.filter { it.activo }
        } else {
            lista.filter { it.activo && it.categorias.contains(categoria) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 👇 5. ESTA ES LA VARIABLE QUE FALTABA PARA EL TOP 5
    val recomendaciones: StateFlow<List<Pair<Producto, String>>> = combine(
        _todosLosEstablecimientos,
        productoRepo.getProductosGlobales()
    ) { locales, productos ->
        productos.filter { it.disponible }.map { producto ->
            val nombreLocal = locales.find { it.id == producto.establecimientoId }?.nombre ?: "Local"
            Pair(producto, nombreLocal)
        }.take(5) // Solo mostramos el Top 5
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }
}