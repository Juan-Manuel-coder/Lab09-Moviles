package com.example.lab09

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyRow

// Paleta de colores temática Naturaleza
val FondoVerdeSuave = Color(0xFFE8F5E9)
val VerdeOscuro = Color(0xFF2E7D32)
val BordeMarron = Color(0xFF8D6E63)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAmphibians(servicio: AmphibianApiService, navController: NavHostController) {
    var listaAnfibios by remember { mutableStateOf<List<AmphibianModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroTipo by remember { mutableStateOf("Todos") }

    LaunchedEffect(cargando) {
        if (cargando) {
            try {
                listaAnfibios = servicio.getAmphibians()
                error = null
            } catch (e: Exception) {
                error = "No se pudo conectar con la API de Anfibios."
            } finally {
                cargando = false
            }
        }
    }

    // Lógica de filtrado (por nombre y por tipo)
    val anfibiosFiltrados = listaAnfibios.filter {
        (filtroTipo == "Todos" || it.type == filtroTipo) &&
                it.name.contains(textoBusqueda, ignoreCase = true)
    }

    // Obtener tipos únicos para los botones de filtro
    val tiposDisponibles = listOf("Todos") + listaAnfibios.map { it.type }.distinct()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("favoritos") },
                containerColor = BordeMarron,
                contentColor = Color.White,
                icon = { Text("❤️", fontSize = 20.sp) },
                text = { Text("Favoritos", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = FondoVerdeSuave
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoVerdeSuave)
        ) {
            // Barra superior con contador
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
                Text("Anfibios 🐸", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                // CONTADOR DINÁMICO
                Text("Mostrando: ${anfibiosFiltrados.size}", color = Color.White, fontSize = 12.sp)
            }

            // Buscador
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar por nombre...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = VerdeOscuro) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VerdeOscuro)
            )

            // Filtros por tipo (Chips deslizables)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tiposDisponibles) { tipo ->
                    FilterChip(
                        selected = filtroTipo == tipo,
                        onClick = { filtroTipo = tipo },
                        label = { Text(tipo) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VerdeOscuro,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VerdeOscuro)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = Color.Red)
                        Button(onClick = { cargando = true }) { Text("Reintentar") }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(anfibiosFiltrados) { anfibio ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(3.dp, BordeMarron, RoundedCornerShape(12.dp))
                                .clickable { navController.navigate("anfibioDetalle/${anfibio.name}") },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = anfibio.imgSrc,
                                    contentDescription = anfibio.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(2.dp, VerdeOscuro, RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(anfibio.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF212121))
                                    Text(anfibio.type, color = BordeMarron, fontSize = 14.sp)
                                }

                                // BOTÓN DE FAVORITO (Corazón)
                                IconButton(onClick = {
                                    FavoritosManager.toggleFavorito(anfibio)
                                }) {
                                    val esFav = FavoritosManager.esFavorito(anfibio)
                                    Text(
                                        text = if (esFav) "❤️" else "🤍", // Corazón lleno o vacío
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAmphibianDetail(anfibio: AmphibianModel, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoVerdeSuave)
    ) {
        // Barra superior con botón de regreso
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
            Text("Detalle del Anfibio", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen en grande con borde verde
            AsyncImage(
                model = anfibio.imgSrc,
                contentDescription = anfibio.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .border(4.dp, VerdeOscuro, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre y Tipo
            Text(
                text = anfibio.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = anfibio.type,
                fontSize = 18.sp,
                color = BordeMarron,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Descripción en una tarjeta
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Descripción:", fontWeight = FontWeight.Bold, color = VerdeOscuro, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(anfibio.description, fontSize = 15.sp, color = Color.DarkGray, lineHeight = 22.sp)
                }
            }
        }
    }
}