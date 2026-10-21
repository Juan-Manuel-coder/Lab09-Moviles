package com.example.lab09

import androidx.compose.runtime.mutableStateListOf

object FavoritosManager {
    val listaFavoritos = mutableStateListOf<AmphibianModel>()

    fun toggleFavorito(anfibio: AmphibianModel) {
        if (listaFavoritos.any { it.name == anfibio.name }) {
            listaFavoritos.removeAll { it.name == anfibio.name }
        } else {
            listaFavoritos.add(anfibio)
        }
    }

    fun esFavorito(anfibio: AmphibianModel): Boolean {
        return listaFavoritos.any { it.name == anfibio.name }
    }
}