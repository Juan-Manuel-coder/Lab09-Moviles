package com.example.lab09

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.text.DecimalFormat
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCart(navController: NavHostController) {
    var tipoPago by remember { mutableStateOf("Tarjeta") }
    var mostrarExito by remember { mutableStateOf(false) }

    val totalPagar = CarritoManager.calcularSubtotal()
    val formatoDecimal = DecimalFormat("#,##0.00")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
    ) {
        // Barra de regreso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoRojo)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("Mi Carrito de Compras", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (CarritoManager.listaCarrito.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío.\nAgrega productos desde la tienda.", color = TextoGris, fontSize = 16.sp, textAlign = TextAlign.Center)
            }
        } else {
            // Usamos un LazyColumn para que la lista y el resumen se puedan desplazar
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. LISTA DE PRODUCTOS
                items(CarritoManager.listaCarrito) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().border(2.dp, BordeAmarillo, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = TarjetaBlanca)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = producto.thumbnail,
                                contentDescription = producto.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(producto.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextoNegro)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$${producto.price}", color = PrecioRojo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            IconButton(onClick = { CarritoManager.listaCarrito.remove(producto) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }

                // 2. MÉTODO DE PAGO
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Método de pago:", fontWeight = FontWeight.Bold, color = TextoNegro, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = tipoPago == "Tarjeta",
                            onClick = { tipoPago = "Tarjeta" },
                            label = { Text("Tarjeta") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = tipoPago == "Efectivo",
                            onClick = { tipoPago = "Efectivo" },
                            label = { Text("Efectivo") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. TOTAL Y BOTÓN DE PAGAR
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    if (!mostrarExito) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total a Pagar:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextoNegro)
                            Text("$${formatoDecimal.format(totalPagar)}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = FondoRojo)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { mostrarExito = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FondoRojo),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Pagar con $tipoPago", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    } else {
                        // Pantalla de Éxito
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("¡Pago Exitoso!", color = Color(0xFF00C853), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pagaste $${formatoDecimal.format(totalPagar)} con $tipoPago", color = TextoGris)
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    CarritoManager.limpiarCarrito()
                                    navController.popBackStack("productos", inclusive = false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BordeAmarillo)
                            ) {
                                Text("Seguir Comprando", color = TextoNegro, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}