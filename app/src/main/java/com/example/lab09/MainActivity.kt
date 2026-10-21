package com.example.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab09.ui.theme.Lab09Theme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab09Theme {
                ProgPrincipal9()
            }
        }
    }
}

@Composable
fun ProgPrincipal9() {
    // 1. Retrofit para DummyJSON (Posts y Productos)
    val urlBaseDummy = "https://dummyjson.com/"
    val retrofitDummy = Retrofit.Builder()
        .baseUrl(urlBaseDummy)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 2. Retrofit para Anfibios (Usando la URL alternativa de GitHub)
    val urlBaseAnfibios = "https://android-kotlin-fun-mars-server.appspot.com/"
    val retrofitAnfibios = Retrofit.Builder()
        .baseUrl(urlBaseAnfibios)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Creamos los servicios
    val servicioPosts = retrofitDummy.create(PostApiService::class.java)
    val servicioProductos = retrofitDummy.create(ProductApiService::class.java)
    val servicioAnfibios = retrofitAnfibios.create(AmphibianApiService::class.java)

    val navController = rememberNavController()

    Scaffold(
        topBar = { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        content = { paddingValues ->
            Contenido(paddingValues, navController, servicioPosts, servicioProductos, servicioAnfibios)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "JSONPlaceHolder Access",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    NavigationBar(containerColor = Color.LightGray) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = navController.currentDestination?.route == "inicio",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Posts") },
            label = { Text("Posts") },
            selected = navController.currentDestination?.route == "posts",
            onClick = { navController.navigate("posts") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = navController.currentDestination?.route == "productos",
            onClick = { navController.navigate("productos") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Pets, contentDescription = "Anfibios") },
            label = { Text("Anfibios") },
            selected = navController.currentDestination?.route == "anfibios",
            onClick = { navController.navigate("anfibios") }
        )
    }
}

@Composable
fun Contenido(
    pv: PaddingValues,
    navController: NavHostController,
    servicioPosts: PostApiService,
    servicioProductos: ProductApiService,
    servicioAnfibios: AmphibianApiService
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(pv)
    ) {
        NavHost(
            navController = navController,
            startDestination = "inicio"
        ) {
            composable("inicio") { ScreenInicio() }
            composable("posts") { ScreenPosts(navController, servicioPosts) }
            composable("productos") { ScreenProducts(servicioProductos, navController) }
            composable("carrito") { ScreenCart(navController) }
            composable("anfibios") { ScreenAmphibians(servicioAnfibios, navController) }
            // RUTA DE ANFIBIOS
            composable("anfibioDetalle/{name}", arguments = listOf(
                navArgument("name") { type = NavType.StringType }
            )) { backStackEntry ->
                val nombre = backStackEntry.arguments!!.getString("name")
                // Como ya tenemos la lista en memoria, podemos buscar el anfibio por su nombre
                // Para simplificar, haremos una llamada rápida al servicio
                var anfibioSeleccionado by remember { mutableStateOf<AmphibianModel?>(null) }
                LaunchedEffect(Unit) {
                    val lista = servicioAnfibios.getAmphibians()
                    anfibioSeleccionado = lista.find { it.name == nombre }
                }
                if (anfibioSeleccionado != null) {
                    ScreenAmphibianDetail(anfibioSeleccionado!!, navController)
                }
            }

            composable("productoDetalle/{id}", arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )) { backStackEntry ->
                val id = backStackEntry.arguments!!.getInt("id")
                var productoSeleccionado by remember { mutableStateOf<ProductModel?>(null) }
                LaunchedEffect(Unit) {
                    val respuesta = servicioProductos.getProducts()
                    productoSeleccionado = respuesta.products.find { it.id == id }
                }
                if (productoSeleccionado != null) {
                    ScreenProductDetail(productoSeleccionado!!, navController)
                }
            }

            composable("postsVer/{id}", arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )) {
                ScreenPost(navController, servicioPosts, it.arguments!!.getInt("id"))
            }

            composable("favoritos") { ScreenFavoritos(navController) }
        }
    }
}

@Composable
fun ScreenInicio() {
    Text("INICIO")
}