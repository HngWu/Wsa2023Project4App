package com.example.wsa2023project4app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Base64
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.ui.input.pointer.pointerInput
import com.example.wsa2023project4app.Api.GetMunicipalities
import com.example.wsa2023project4app.Api.GetMunicipality
import com.example.wsa2023project4app.Api.GetTouristSpots
import com.example.wsa2023project4app.Api.Login
import com.example.wsa2023project4app.Models.Municipality
import com.example.wsa2023project4app.Models.TouristSpot
import com.example.wsa2023project4app.Models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import kotlin.div
import kotlin.text.compareTo
import kotlin.times
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import coil3.Image
import kotlinx.coroutines.withContext
import kotlin.div
import kotlin.text.compareTo
import kotlin.times

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val colorScheme = lightColorScheme(
            primary = Color(0xFFEF3340),
            onPrimary = Color.White,
            // Add other color customizations if needed
        )
        setContent {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = androidx.compose.material3.Typography(),
                content = {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("login") { LoginScreen(navController, this@MainActivity) }
                        composable("home") {HomeScreen(navController, this@MainActivity)}
                        composable("touristSpot/{name}") { backStackEntry ->
                            TouristSpotScreen(name = backStackEntry.arguments?.getString("name")?.toString() ?: "", navController = navController)
                        }
                    }


                }
            )
        }
    }
}

fun decodeBase64Image(base64Str: String): ImageBitmap? {
    return try {
        // Decode Base64 string into byte array
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        // Convert byte array to Bitmap
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        // Convert Bitmap to ImageBitmap
        bitmap?.let { Bitmap.createScaledBitmap(it, it.width* 1, it.height * 2, true).asImageBitmap() }    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}

fun decodeNormalBase64Image(base64Str: String): ImageBitmap? {
    return try {
        // Decode Base64 string into byte array
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        // Convert byte array to Bitmap
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        // Convert Bitmap to ImageBitmap
        bitmap?.let { Bitmap.createScaledBitmap(it, it.width, it.height, true).asImageBitmap() }    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristSpotScreen(name:String, navController: NavController) {
    var touristSpotList by remember { mutableStateOf<List<TouristSpot>?>(null) }
    var municipality by remember { mutableStateOf(Municipality(0, "", "", "","","")) }
    var showDialog by remember { mutableStateOf(false) }
    var filteredTouristSpotList by remember { mutableStateOf<List<TouristSpot>?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showDescriptionOfMunicipalityDialog by remember { mutableStateOf(false) }
    var selectedTouristSpot by remember { mutableStateOf<TouristSpot?>(null) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = GetMunicipality().getFunction(name)
                if (response != null) {
                    withContext(Dispatchers.Main) {
                        municipality = response
                    }                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    if (municipality.id != 0) {
        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                try {

                    val response = GetTouristSpots().getFunction(name)


                if (response != null) {
                    withContext(Dispatchers.Main) {
                        touristSpotList = response as List<TouristSpot>?
                        filteredTouristSpotList = touristSpotList
                    }

                }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(touristSpotList?.firstOrNull()?.name ?: "") },
            text = {
                Column {
                    Image(
                        bitmap = decodeBase64Image(selectedTouristSpot?.picture ?: "") ?: ImageBitmap(1, 1),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(selectedTouristSpot?.description ?: "")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
    if (showDescriptionOfMunicipalityDialog) {
        AlertDialog(
            onDismissRequest = { showDescriptionOfMunicipalityDialog = false },
            title = { Text(touristSpotList?.firstOrNull()?.name ?: "") },
            text = {
                Column {
                    Image(
                        bitmap = decodeNormalBase64Image(municipality.logo ?: "") ?: ImageBitmap(1, 1),
                        contentDescription = null,
                        modifier = Modifier.size(300.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(municipality.description ?: "")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDescriptionOfMunicipalityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }


    if (municipality.id == 0) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.width(70.dp))
                            Text(
                                text = "Login",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF3F3F3),
                        titleContentColor = Color(0xFFEF3340)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        bitmap = decodeNormalBase64Image(municipality.logo!!) ?: ImageBitmap(1, 1),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = municipality.name)
                        Text(text = municipality.touristSpot + " Tourist Destinations")
                        Button(
                            onClick = {
                                showDescriptionOfMunicipalityDialog = true
                            }
                        ) {
                            Text("Read History")
                        }
                    }
                }


                Text("Tourist Spots in ${municipality.name}", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        filteredTouristSpotList = touristSpotList?.filter { spot ->

                            spot.description!!.contains(searchQuery, ignoreCase = true)
                        }
                    },
                    label = { Text("Search Amenities/Services") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(filteredTouristSpotList ?: emptyList()) { touristSpot ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                if (touristSpot.picture != null) {
                                    Image(
                                        bitmap = decodeNormalBase64Image(touristSpot.picture!!) ?: ImageBitmap(1, 1),
                                        contentDescription = null,
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(text = "Php: " + touristSpot.entranceFee)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Location: " + touristSpot.name)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Rating" + touristSpot.rating)
                                    Spacer(modifier = Modifier.height(50.dp))
                                    Button(
                                        onClick = {
                                            selectedTouristSpot = touristSpot
                                            showDialog = true

                                        }
                                    ) {
                                        Text("Read More")
                                    }
                                }
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Back")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MunicipalityItem(municipality: Municipality, navController: NavController) {

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Number of tourist spots") },
            text = { Text(municipality.touristSpot) },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }


    Column(

    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .combinedClickable(
                    onClick = {
                        showDialog = true


                    },
                    onDoubleClick = {
                        navController.navigate("touristSpot/${municipality.id}")
                    }


                )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    bitmap = decodeBase64Image(municipality.map!!) ?: ImageBitmap(1, 1),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                Text(text = municipality.name, style = TextStyle(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                //Text(text = municipality.description)
            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, context: Context) {
    var municipality by remember { mutableStateOf(Municipality(0, "", "", "","","")) }
    var Mname by remember { mutableStateOf("") }
    var changeMunicipality by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = GetMunicipalities().getFunction("Donsol")
            municipality = response as Municipality
        }
    }

    if(showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Number of tourist spots") },
            text = { Text(municipality.touristSpot) },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }


    if (changeMunicipality && Mname != "Error") {
        LaunchedEffect(Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = GetMunicipalities().getFunction(Mname)
                if (response != null)
                {
                    municipality = response as Municipality

                }



                changeMunicipality = false
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.width(60.dp))
                        Text(
                            text = "Map Of Sorsogon",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F3F3),
                    titleContentColor = Color(0xFFEF3340)
                )
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (municipality.id == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Box {
                                Image(
                                    bitmap = decodeBase64Image(municipality.map!!) ?: ImageBitmap(1, 1),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = { offset ->
                                                    val width = size.width
                                                    val height = size.height
                                                    val x = offset.x
                                                    val y = offset.y
                                                    val municipality = when {
                                                        (x < width / 10 * 3 && x > width / 10 * 1) && (y < height / 14 * 6 && y > height / 14 * 3) -> "Donsol"
                                                        (x < width / 10 * 4 && x > width / 10 * 3) && (y < height / 14 * 5 && y > height / 14 * 3) -> "Pilar"
                                                        (x < width / 10 * 6 && x > width / 10 * 4) && (y < height / 14 * 7 && y > height / 14 * 3) -> "Castilla"
                                                        (x < width / 10 * 8 && x > width / 10 * 6) && (y < height / 14 * 4 && y > height / 14 * 2) -> "Sorsogon"
                                                        (x < width / 10 * 10 && x > width / 10 * 8) && (y < height / 14 * 3 && y > height / 14 * 2) -> "Prieto Diaz"
                                                        (x < width / 10 * 10 && x > width / 10 * 8) && (y < height / 14 * 6 && y > height / 14 * 4) -> "Gubat"
                                                        (x < width / 10 * 6 && x > width / 10 * 5) && (y < height / 14 * 9 && y > height / 14 * 7) -> "Magallanes"
                                                        (x < width / 10 * 7 && x > width / 10 * 6) && (y < height / 14 * 9 && y > height / 14 * 7) -> "Juban"
                                                        (x < width / 10 * 8 && x > width / 10 * 7) && (y < height / 14 * 8 && y > height / 14 * 6) -> "Casiguran"
                                                        (x < width / 10 * 9 && x > width / 10 * 8) && (y < height / 14 * 8 && y > height / 14 * 6) -> "Barcelona"
                                                        (x < width / 10 * 7 && x > width / 10 * 5) && (y < height / 14 * 11 && y > height / 14 * 9) -> "Bulan"
                                                        (x < width / 10 * 8 && x > width / 10 * 7) && (y < height / 14 * 10 && y > height / 14 * 9) -> "Irosin"
                                                        (x < width / 10 * 9 && x > width / 10 * 8) && (y < height / 14 * 10 && y > height / 14 * 8) -> "Bulusan"
                                                        (x < width / 10 * 9 && x > width / 10 * 7) && (y < height / 14 * 11 && y > height / 14 * 10) -> "Sta Magdalena"
                                                        (x < width / 10 * 8 && x > width / 10 * 7) && (y < height / 14 * 13 && y > height / 14 * 11) -> "Matnog"
                                                        else -> "Error"
                                                    }
                                                    Mname = municipality
                                                    changeMunicipality = true
                                                    showDialog = true
                                                },
                                                onDoubleTap = { offset ->
                                                    val width = size.width
                                                    val height = size.height
                                                    val x = offset.x
                                                    val y = offset.y
                                                    val municipalitySelected = when {
                                                        (x < width / 10 * 3 && x > width / 10 * 1) && (y < height / 14 * 6 && y > height / 14 * 3) -> "Donsol"
                                                        (x < width / 10 * 4 && x > width / 10 * 3) && (y < height / 14 * 5 && y > height / 14 * 3) -> "Pilar"
                                                        (x < width / 10 * 6 && x > width / 10 * 4) && (y < height / 14 * 7 && y > height / 14 * 3) -> "Castilla"
                                                        (x < width / 10 * 8 && x > width / 10 * 6) && (y < height / 14 * 4 && y > height / 14 * 2) -> "Sorsogon"
                                                        (x < width / 10 * 10 && x > width / 10 * 8) && (y < height / 14 * 3 && y > height / 14 * 2) -> "Prieto Diaz"
                                                        (x < width / 10 * 10 && x > width / 10 * 8) && (y < height / 14 * 6 && y > height / 14 * 4) -> "Gubat"
                                                        (x < width / 10 * 6 && x > width / 10 * 5) && (y < height / 14 * 9 && y > height / 14 * 7) -> "Magallanes"
                                                        (x < width / 10 * 7 && x > width / 10 * 6) && (y < height / 14 * 9 && y > height / 14 * 7) -> "Juban"
                                                        (x < width / 10 * 8 && x > width / 10 * 7) && (y < height / 14 * 8 && y > height / 14 * 6) -> "Casiguran"
                                                        (x < width / 10 * 9 && x > width / 10 * 8) && (y < height / 14 * 8 && y > height / 14 * 6) -> "Barcelona"
                                                        (x < width / 10 * 7 && x > width / 10 * 5) && (y < height / 14 * 11 && y > height / 14 * 9) -> "Bulan"
                                                        (x < width / 10 * 8 && x > width / 10 * 7) && (y < height / 14 * 10 && y > height / 14 * 9) -> "Irosin"
                                                        (x < width / 10 * 9 && x > width / 10 * 8) && (y < height / 14 * 10 && y > height / 14 * 8) -> "Bulusan"
                                                        (x < width / 10 * 9 && x > width / 10 * 7) && (y < height / 14 * 11 && y > height / 14 * 10) -> "Sta Magdalena"
                                                        (x < width / 10 * 8 && x > width / 10 * 7) && (y < height / 14 * 13 && y > height / 14 * 11) -> "Matnog"
                                                        else -> "Error"
                                                    }

                                                    if (municipalitySelected != "Error" && municipalitySelected != "") {
                                                        navController.navigate("touristSpot/${municipalitySelected}")
                                                    }

                                                }
                                            )
                                        },
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Municipality: ${municipality.name}", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                                Text("Tourist Spot: ${municipality.touristSpot}", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp))

                            }
                        }
                    }
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var captchaInput by remember { mutableStateOf("") }
    var captchaCode by remember { mutableStateOf(generateCaptcha()) }
    var errorMessage by remember { mutableStateOf("") }
    var isValidUser by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.width(70.dp))
                        Text(
                            text = "Login",
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                        },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F3F3),
                    titleContentColor = Color(0xFFEF3340)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))


            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CaptchaImage(
                    text = captchaCode,
                    width = 600,
                    height = 600,
                    textSize = 100f,
                    textColor = Color.Black,
                    backgroundColor = Color.White,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.width(18.dp))

                Button(onClick = { captchaCode = generateCaptcha() }) {
                    Text("Regenerate\nCaptcha")
                }
            }




            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = captchaInput,
                onValueChange = { captchaInput = it },
                label = { Text("Enter Captcha") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        var login = User(username, password)
                        val response = Login().postFunction(login)
                        isValidUser = response
                    }
                    if (isValidUser && captchaInput == captchaCode) {
                        navController.navigate("home")
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        errorMessage = "Invalid credentials or captcha!"
                    }
                }) {
                    Text("Login")
                }

                Button(onClick = {
                    username = ""
                    password = ""
                    captchaInput = ""
                    captchaCode = generateCaptcha()
                    errorMessage = ""
                }) {
                    Text("Cancel")
                }
            }
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}


fun generateCaptcha(): String {
    val upperCase = ('A'..'Z').random()
    val lowerCase = ('a'..'z').random()
    val number = ('0'..'9').random()
    val specialChar = listOf('!', '@', '#', '$', '%', '&', '*').random()
    val remainingChars = ('a'..'z').shuffled().take(2).joinToString("")

    val captcha = listOf(upperCase, lowerCase, number, specialChar, remainingChars).shuffled().joinToString("")

    return captcha
}

fun createCaptchaImage(
    text: String,
    width: Int = 300,
    height: Int = 100,
    textSize: Float = 40f,
    textColor: Int = Color.Black.toArgb(),
    backgroundColor: Int = Color.White.toArgb()
): ImageBitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val random = Random.Default

    // Background
    canvas.drawColor(backgroundColor)

    // Add noise (dots)
    val noisePaint = Paint().apply {
        color = Color.Gray.toArgb()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    repeat(100) {
        val x = random.nextFloat() * width
        val y = random.nextFloat() * height
        canvas.drawCircle(x, y, 1f, noisePaint)
    }

    // Add random lines
    val linePaint = Paint().apply {
        color = Color.LightGray.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    repeat(5) {
        val path = Path()
        path.moveTo(0f, random.nextFloat() * height)

        // Create curved line
        for (i in 0..3) {
            val x = width * (i + 1) / 4f
            val y = random.nextFloat() * height
            val controlX = width * i / 4f + random.nextFloat() * (width / 4f)
            val controlY = random.nextFloat() * height
            path.quadTo(controlX, controlY, x, y)
        }

        canvas.drawPath(path, linePaint)
    }

    // Setup text paint
    val textPaint = Paint().apply {
        this.textSize = textSize
        color = textColor
        isAntiAlias = true
        style = Paint.Style.FILL
        isFakeBoldText = true
    }

    // Calculate spacing
    val totalTextWidth = textPaint.measureText(text)
    val charSpacing = (width - 40) / text.length // Leave 20px padding on each side
    val startX = 20f // Start with 20px padding

    // Draw each character with controlled distortion
    text.forEachIndexed { index, char ->
        val charWidth = textPaint.measureText(char.toString())

        // Calculate center position for this character
        val charCenterX = startX + (index * charSpacing) + (charSpacing - charWidth) / 2
        val baselineY = height / 2f + textSize / 3 // Adjust baseline to vertically center text

        canvas.save()

        // Apply minor rotation (reduced from previous version)
        val rotation = random.nextFloat() * 20 - 10 // Rotation between -10 and 10 degrees
        canvas.rotate(
            rotation,
            charCenterX + charWidth / 2,
            baselineY
        )

        // Add subtle wave effect (reduced amplitude)
        val yOffset = Math.sin(index.toDouble() * 0.5) * 5 // Reduced amplitude from 10 to 5

        // Draw character
        canvas.drawText(
            char.toString(),
            charCenterX,
            baselineY + yOffset.toFloat(),
            textPaint
        )

        canvas.restore()
    }

    return bitmap.asImageBitmap()
}

@Composable
fun CaptchaImage(
    text: String,
    modifier: Modifier = Modifier,
    width: Int = 300,
    height: Int = 100,
    textSize: Float = 40f,
    textColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    val imageBitmap = createCaptchaImage(
        text = text,
        width = width,
        height = height,
        textSize = textSize,
        textColor = textColor.toArgb(),
        backgroundColor = backgroundColor.toArgb()
    )

    Canvas(
        modifier = modifier.size(
            with(LocalDensity.current) {
                width.toDp()
            },
            with(LocalDensity.current) {
                height.toDp()
            }
        )
    ) {
        drawImage(imageBitmap)
    }
}


