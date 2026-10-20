package com.example.lab09

import androidx.compose.runtime.mutableStateListOf

object CarritoManager {
    val listaCarrito = mutableStateListOf<ProductModel>()

    fun agregarProducto(producto: ProductModel) {
        if (!listaCarrito.any { it.id == producto.id }) {
            listaCarrito.add(producto)
        }
    }

    fun limpiarCarrito() {
        listaCarrito.clear()
    }

    fun calcularSubtotal(): Double {
        return listaCarrito.sumOf { it.price }
    }
}