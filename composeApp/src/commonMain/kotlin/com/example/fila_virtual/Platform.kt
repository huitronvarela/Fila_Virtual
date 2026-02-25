package com.example.fila_virtual

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform