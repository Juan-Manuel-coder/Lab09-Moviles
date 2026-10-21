package com.example.lab09

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFavoritos(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoVerdeSuave)
    ) {
        // Barra superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeOscuro)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("Mis Favoritos ❤️", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (FavoritosManager.listaFavoritos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes anfibios favoritos.\n¡Explora y guarda los que más te gusten!", color = VerdeOscuro, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(FavoritosManager.listaFavoritos) { anfibio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, BordeMarron, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = anfibio.imgSrc,
                                contentDescription = anfibio.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(anfibio.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                                Text(anfibio.type, color = BordeMarron, fontSize = 14.sp)
                            }
                            // Botón de eliminar de favoritos
                            IconButton(onClick = {
                                FavoritosManager.toggleFavorito(anfibio)
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}