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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

// Paleta de colores estilo Plaza Vea
val FondoRojo = Color(0xFFD32F2F)
val BordeAmarillo = Color(0xFFFFC107)
val TarjetaBlanca = Color(0xFFFFFFFF)
val TextoNegro = Color(0xFF212121)
val TextoGris = Color(0xFF757575)
val PrecioRojo = Color(0xFFD32F2F)
val FondoClaro = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenProducts(servicio: ProductApiService, navController: NavHostController) {
    var listaProductos by remember { mutableStateOf<List<ProductModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val respuesta = servicio.getProducts()
            listaProductos = respuesta.products
        } catch (e: Exception) {
            // Manejo de error
        } finally {
            cargando = false
        }
    }

    Scaffold(
        floatingActionButton = {
            // Botón flotante para ir al carrito
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("carrito") },
                containerColor = BordeAmarillo,
                contentColor = TextoNegro,
                icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito") },
                text = { Text("Ir al Carrito", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = FondoRojo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoRojo)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listaProductos) { producto ->
                        TarjetaProducto(producto) {
                            navController.navigate("productoDetalle/${producto.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaProducto(producto: ProductModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, BordeAmarillo, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaBlanca),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            AsyncImage(
                model = producto.thumbnail,
                contentDescription = producto.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .border(3.dp, BordeAmarillo, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    text = producto.title,
                    color = TextoNegro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Descripción
                Text(
                    text = producto.description,
                    color = TextoGris,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fila con Precio y Botón de Carrito
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Precio
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            tint = PrecioRojo,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$${producto.price}",
                            color = PrecioRojo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    // Botón con texto e ícono "Agregar"
                    Button(
                        onClick = { CarritoManager.agregarProducto(producto) },
                        colors = ButtonDefaults.buttonColors(containerColor = BordeAmarillo),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Agregar al carrito",
                            tint = TextoNegro,
                            modifier = Modifier.size(14.dp) // Ícono pequeño para que quepa
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Agregar",
                            color = TextoNegro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenProductDetail(producto: ProductModel, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .verticalScroll(rememberScrollState())
    ) {
        // Barra roja delgada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoRojo)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Detalle del Producto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = producto.thumbnail,
                contentDescription = producto.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(250.dp)
                    .border(4.dp, BordeAmarillo, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = producto.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextoNegro,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Marca: ${producto.brand ?: "Genérica"} | Categoría: ${producto.category ?: "Varios"}",
                fontSize = 14.sp,
                color = TextoGris
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = FondoRojo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$${producto.price}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Descripción:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoNegro,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = producto.description,
                fontSize = 14.sp,
                color = TextoGris,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
            // AQUI YA NO ESTÁ EL BOTÓN
        }
    }
}