package com.example.wetpka.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wetpka.data.AppDatabase
import com.example.wetpka.data.MockData
import com.example.wetpka.model.Fish
import com.example.wetpka.model.User
import com.example.wetpka.model.WaterBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// To są nazwy naszych filtrów (pigułek u góry)
val filterOptions = listOf("Wszystkie", "Drapieżne", "Spokojnego żeru")

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AtlasScreen(onFishClick: (Int) -> Unit = {}) {
    // Stan: Przechowuje informację, która pigułka filtru jest aktualnie zaznaczona
    var selectedFilter by remember { mutableStateOf("Wszystkie") }

    // Stan: Przechowuje tekst wpisany w wyszukiwarkę (na razie nieaktywny, jak na makiecie)
    var searchQuery by remember { mutableStateOf("") }

    // Logika filtrowania: łączymy filtr kategorii i wyszukiwanie tekstowe
    val filteredFishes = remember(selectedFilter, searchQuery) {
        MockData.fishes.filter { fish ->
            val matchesFilter = selectedFilter == "Wszystkie" || fish.category == selectedFilter
            val matchesQuery = searchQuery.isBlank() ||
                fish.name.contains(searchQuery, ignoreCase = true) ||
                fish.latinName.contains(searchQuery, ignoreCase = true) ||
                fish.englishName.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Pasek górny z napisem "Atlas Ryb"
            TopAppBar(title = { Text("Atlas Ryb", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // 1. Wyszukiwarka (SearchBar) - Placeholder jak na makiecie
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Wyszukaj gatunek ryby...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ikona wyszukiwania") },
                shape = MaterialTheme.shapes.extraLarge, // Zaokrąglone rogi jak na makiecie
                singleLine = true
            )

            // 2. Filtrowanie (Pigułki/Chipsy) - Wiersz z opcjami filtrowania
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(text = filter) }
                    )
                }
            }

            // 3. Siatka (Grid) z rybami - Wypełnia resztę ekranu
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 kolumny jak na makiecie
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wyświetlamy tylko pofiltrowane ryby
                items(filteredFishes) { fish ->
                    FishGridItem(fish = fish, onClick = { onFishClick(fish.id) }) // To jest nasz pojedynczy kafelek ryby
                }
            }
        }
    }
}

// Składnik UI dla pojedynczego kafelka w siatce (Zdjęcie + Nazwa + Nazwa łacińska)
@Composable
fun FishGridItem(fish: Fish, onClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Kafelki będą kwadratowe
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Zdjęcie Ryby
            Image(
                painter = painterResource(id = fish.imageResId),
                contentDescription = "Zdjęcie: ${fish.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Zdjęcie zajmuje górną część kafelka
                    .padding(4.dp), // Delikatny margines wewnątrz karty
                contentScale = ContentScale.Fit // Przytnij zdjęcie, żeby ładnie wypełniało przestrzeń
            )

            // Tekst na dole kafelka
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally // Centrujemy napisy jak na makiecie
            ) {
                // Nazwa Polska
                Text(
                    text = fish.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1 // Jeśli nazwa jest za długa, utnij ją, nie przenoś
                )

                // Nazwa Łacińska
                Text(
                    text = fish.latinName,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary, // Używamy koloru pomocniczego dla łaciny
                    maxLines = 1 // Jeśli nazwa jest za długa, utnij ją, nie przenoś
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishDetailScreen(fishId: Int, onBackClick: () -> Unit) {
    val fish = MockData.fishes.find { it.id == fishId } ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Szczegóły: ${fish.name}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // Zdjęcie ryby na pełną szerokość
            Image(
                painter = painterResource(id = fish.imageResId),
                contentDescription = "Zdjęcie: ${fish.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Karta z nazwami
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = fish.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        text = fish.latinName,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = fish.englishName,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Karta "Regulacje Wędkarskie"
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Regulacje wędkarskie",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RegulationItem(
                            modifier = Modifier.weight(1f),
                            title = "Wymiar ochronny",
                            value = fish.protectionSize
                        )
                        RegulationItem(
                            modifier = Modifier.weight(1f),
                            title = "Limit dobowy",
                            value = fish.dailyLimit
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RegulationItem(
                            modifier = Modifier.weight(1f),
                            title = "Okres ochronny",
                            value = fish.protectionPeriod
                        )
                        RegulationItem(
                            modifier = Modifier.weight(1f),
                            title = "Czas tarła",
                            value = fish.spawningTime
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Karta "Informacje i Siedlisko"
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Informacje i siedlisko",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    InfoItem(label = "Regiony występowania", value = fish.regions)
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(label = "Preferowane akweny", value = fish.habitat)
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(label = "Dobre brania", value = fish.goodBites)
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(label = "Słabe brania", value = fish.badBites)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RegulationItem(modifier: Modifier = Modifier, title: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var userLocation by remember { mutableStateOf<android.location.Location?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredWaterBodies = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            MockData.waterBodies
        } else {
            MockData.waterBodies.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.region.contains(searchQuery, ignoreCase = true) ||
                it.district.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Pomocnicza funkcja do pobierania lokalizacji z fallbackiem
    fun fetchLocation() {
        val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                } else {
                    // lastLocation może być null – żądamy aktualnej lokalizacji
                    val cts = com.google.android.gms.tasks.CancellationTokenSource()
                    fusedLocationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cts.token
                    ).addOnSuccessListener { freshLocation ->
                        userLocation = freshLocation
                    }
                }
            }
        } catch (e: SecurityException) { e.printStackTrace() }
    }

    // Rejestrator pozwolenia na lokalizację
    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
        if (isGranted) {
            fetchLocation()
        }
    }

    // Prosimy o uprawnienia przy starcie ekranu
    LaunchedEffect(Unit) {
        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        val isGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context, permission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            locationPermissionGranted = true
            fetchLocation()
        } else {
            locationPermissionLauncher.launch(permission)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zbiorniki PZW", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            // Pasek wyszukiwania
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Szukaj zbiornika...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Szukaj") },
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true
            )

            // Lista łowisk
            androidx.compose.foundation.lazy.LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredWaterBodies) { waterBody ->
                    WaterBodyCard(waterBody, userLocation, locationPermissionGranted)
                }
            }
        }
    }
}

@Composable
fun WaterBodyCard(waterBody: WaterBody, userLocation: android.location.Location?, permissionGranted: Boolean) {
    // Liczenie odległości
    var distanceText = "Lokalizacja niedostępna"
    if (permissionGranted && userLocation != null) {
        val targetLocation = android.location.Location("").apply {
            latitude = waterBody.latitude
            longitude = waterBody.longitude
        }
        val distanceInMeters = userLocation.distanceTo(targetLocation)
        val distanceInKm = distanceInMeters / 1000
        distanceText = String.format(java.util.Locale.getDefault(), "%.1f km od Ciebie", distanceInKm)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {

            // Obrazek (Miniatura)
            Image(
                painter = painterResource(id = waterBody.imageResId),
                contentDescription = waterBody.name,
                modifier = Modifier.size(80.dp).padding(4.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Teksty
            Column(modifier = Modifier.weight(1f)) {
                Text(text = waterBody.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = waterBody.region, fontSize = 14.sp)
                Text(text = waterBody.sizeInfo, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                Text(text = waterBody.district, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }

            // Odległość na samym dole po prawej
            Column(horizontalAlignment = Alignment.End) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = distanceText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { com.example.wetpka.data.AppDatabase.getDatabase(context) }
    val catchDao = db.catchDao()
    val catches by catchDao.getAllCatches().collectAsState(initial = emptyList())

    val sortedCatches = catches.sortedByDescending { record ->
        try {
            java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                .parse("${record.date} ${record.time}")?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showVideoPlayer by remember { mutableStateOf(false) }
    var recordToEdit by remember { mutableStateOf<com.example.wetpka.data.CatchRecord?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rejestr połowów", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        recordToEdit = null
                        showAddDialog = true
                    }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Add,
                            contentDescription = "Dodaj połów",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(horizontal = 16.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp).padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.large,
                onClick = { showVideoPlayer = true }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = com.example.wetpka.R.drawable.rejestr),
                        contentDescription = "Tło poradnika",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_media_play),
                            contentDescription = "Play", modifier = Modifier.size(64.dp), tint = androidx.compose.ui.graphics.Color.White
                        )
                        Text(
                            text = "Poradnik wideo: jak prowadzić rejestr",
                            color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Data", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(1.5f))
                Text("Godz.", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.9f))
                Text("Łowisko", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(1.1f))
                Text("Gatunek", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(1.2f))
                Text("Szt.", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.6f))
                Text("Waga", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.9f))
                Text("Dł.", fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.8f))
            }

            androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sortedCatches) { record ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                recordToEdit = record
                                showAddDialog = true
                            }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(record.date, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(1.5f))
                            Text(record.time, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.9f))
                            Text(record.spotNumber, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(1.1f))
                            Text(record.fishSpecies, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(1.2f))
                            Text("${record.pieces}", fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.6f))
                            Text("${record.totalWeight}", fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.9f))
                            Text("${record.length}", fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.weight(0.8f))
                        }
                    }
                }
            }
        }
    }

    if (showVideoPlayer) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showVideoPlayer = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black)) {
                androidx.compose.ui.viewinterop.AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        android.widget.VideoView(ctx).apply {
                            setMediaController(android.widget.MediaController(ctx).apply { setAnchorView(this@apply) })
                            setVideoURI(android.net.Uri.parse("android.resource://${ctx.packageName}/${com.example.wetpka.R.raw.vid}"))
                            start()
                        }
                    }
                )
                IconButton(onClick = { showVideoPlayer = false }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.Close, contentDescription = "Zamknij", tint = androidx.compose.ui.graphics.Color.Black, modifier = Modifier.size(32.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        val currentDate = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())

        var date by remember { mutableStateOf(recordToEdit?.date ?: currentDate) }
        var time by remember { mutableStateOf(recordToEdit?.time ?: currentTime) }
        var spot by remember { mutableStateOf(recordToEdit?.spotNumber ?: "") }
        var species by remember { mutableStateOf(recordToEdit?.fishSpecies ?: com.example.wetpka.data.MockData.fishes[0].name) }
        var pieces by remember { mutableStateOf(recordToEdit?.pieces?.toString() ?: "1") }
        var weight by remember { mutableStateOf(recordToEdit?.totalWeight?.toString() ?: "") }
        var length by remember { mutableStateOf(recordToEdit?.length?.toString() ?: "") }

        var expanded by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        val calendar = java.util.Calendar.getInstance()

        // 1. Zabezpieczenie Kalendarza (maxDate ustawione na teraz)
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date = String.format(java.util.Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth, month + 1, year)
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }

        val timePickerDialog = android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                time = String.format(java.util.Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE),
            true
        )

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                recordToEdit = null
            },
            title = { Text(if (recordToEdit == null) "Dodaj nowy połów" else "Edytuj połów") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (errorMessage != null) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                    }

                    Box {
                        OutlinedTextField(
                            value = date, onValueChange = {}, label = { Text("Data") },
                            readOnly = true, singleLine = true, modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("") }
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
                    }

                    Box {
                        OutlinedTextField(
                            value = time, onValueChange = {}, label = { Text("Godzina") },
                            readOnly = true, singleLine = true, modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("") }
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { timePickerDialog.show() })
                    }

                    OutlinedTextField(value = spot, onValueChange = { spot = it }, label = { Text("Nr łowiska") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Box {
                        OutlinedTextField(
                            value = species, onValueChange = {}, label = { Text("Gatunek (Z Atlasu)") },
                            readOnly = true, trailingIcon = { IconButton(onClick = { expanded = true }) { Text("▼") } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            com.example.wetpka.data.MockData.fishes.forEach { fish ->
                                DropdownMenuItem(text = { Text(fish.name) }, onClick = { species = fish.name; expanded = false })
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = pieces, onValueChange = { pieces = it },
                            label = { Text("Sztuki", fontSize = 10.sp, maxLines = 1, softWrap = false) },
                            modifier = Modifier.weight(0.7f), singleLine = true
                        )
                        OutlinedTextField(
                            value = weight, onValueChange = { weight = it },
                            label = { Text("Waga (kg)", fontSize = 10.sp, maxLines = 1, softWrap = false) },
                            modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = length, onValueChange = { length = it },
                            label = { Text("Długość (cm)", fontSize = 10.sp, maxLines = 1, softWrap = false) },
                            modifier = Modifier.weight(1.2f), singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p = pieces.toIntOrNull() ?: 1
                    val w = weight.replace(",", ".").toDoubleOrNull()
                    val l = length.replace(",", ".").toDoubleOrNull()

                    if (w == null || w < 0.01 || w > 999.0) { errorMessage = "Waga musi być liczbą z przedziału 0.01 - 999 kg."; return@Button }
                    if (l == null || l < 1.0 || l > 999.0) { errorMessage = "Długość musi być liczbą z przedziału 1 - 999 cm."; return@Button }

                    // 2. Walidacja Czasu z przyszłości
                    val selectedDateTime = try {
                        java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).parse("$date $time")
                    } catch (e: Exception) { null }

                    if (selectedDateTime != null && selectedDateTime.after(java.util.Date())) {
                        errorMessage = "Nie można zapisać połowu z przyszłości!"
                        return@Button
                    }

                    val roundedWeight = kotlin.math.round(w * 10.0) / 10.0
                    val roundedLength = kotlin.math.round(l * 10.0) / 10.0

                    val currentRecordId = recordToEdit?.id ?: 0
                    val isEditMode = recordToEdit != null

                    val finalSpot = if (spot.trim().isEmpty()) "-" else spot.trim()

                    val newRecord = com.example.wetpka.data.CatchRecord(
                        id = currentRecordId,
                        date = date, time = time, spotNumber = finalSpot, fishSpecies = species,
                        pieces = p, totalWeight = roundedWeight, length = roundedLength
                    )

                    coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        if (!isEditMode) {
                            val mediaPlayer = android.media.MediaPlayer.create(context, com.example.wetpka.R.raw.gulp)
                            mediaPlayer.setVolume(1.0f, 1.0f)
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { it.release() }
                            catchDao.insertCatch(newRecord)
                        } else {
                            catchDao.updateCatch(newRecord)
                        }
                    }

                    showAddDialog = false
                    recordToEdit = null
                    errorMessage = null
                }) { Text("Zapisz") }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (recordToEdit != null) {
                        TextButton(onClick = {
                            val recordToDelete = recordToEdit
                            if (recordToDelete != null) {
                                coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                    catchDao.deleteCatch(recordToDelete)
                                }
                            }
                            showAddDialog = false
                            recordToEdit = null
                        }) { Text("Usuń", color = MaterialTheme.colorScheme.error) }
                    }

                    TextButton(onClick = {
                        showAddDialog = false
                        recordToEdit = null
                        errorMessage = null
                    }) { Text("Anuluj") }
                }
            }
        )
    }
}

// ===================== EKRAN LEGITYMACJA (z logowaniem) =====================

// Pomocnicza funkcja do zapisu/odczytu zalogowanego użytkownika
private fun saveLoggedInUserId(context: android.content.Context, userId: Int) {
    context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        .edit().putInt("logged_in_user_id", userId).apply()
}

private fun getLoggedInUserId(context: android.content.Context): Int {
    return context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        .getInt("logged_in_user_id", -1)
}

private fun clearLoggedInUser(context: android.content.Context) {
    context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        .edit().remove("logged_in_user_id").apply()
}

// Lokalna funkcja hashowania (omija problem z importami od Copilota)
private fun localHashPassword(password: String): String {
    val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

@Composable
fun ProfileScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { com.example.wetpka.data.AppDatabase.getDatabase(context) }

    // Wymuszona pełna ścieżka do modelu User
    var loggedInUser by remember { mutableStateOf<com.example.wetpka.model.User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val savedId = getLoggedInUserId(context)
        if (savedId != -1) {
            loggedInUser = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                db.userDao().findById(savedId)
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (loggedInUser != null) {
        LegitymacjaScreen(
            user = loggedInUser!!,
            onLogout = {
                clearLoggedInUser(context)
                loggedInUser = null
            }
        )
    } else {
        LoginScreen(
            onLoginSuccess = { user ->
                saveLoggedInUserId(context, user.id)
                loggedInUser = user
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (com.example.wetpka.model.User) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { com.example.wetpka.data.AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2EAF1))
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_crop),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .size(64.dp),
            tint = Color(0xFF1E5370)
        )

        Text(
            text = "Logowanie",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 160.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center)
                .offset(y = (-40).dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = null },
                    label = { Text("Adres e-mail lub Nr Karty PZW") },
                    leadingIcon = { Icon(painterResource(id = android.R.drawable.ic_dialog_email), contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E5370),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Hasło") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        val icon = if (passwordVisible) android.R.drawable.ic_menu_view else android.R.drawable.ic_secure
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(painter = painterResource(id = icon), contentDescription = "Pokaż hasło")
                        }
                    },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E5370),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            // Pełna ścieżka do modelu User rozwiązuje problem type mismatch!
                            val user: com.example.wetpka.model.User? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                db.userDao().findByUsername(username.trim())
                            }

                            if (user != null && user.passwordHash == localHashPassword(password)) {
                                onLoginSuccess(user)
                            } else {
                                errorMessage = "Nieprawidłowe dane logowania."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5370))
                ) {
                    Text("Zaloguj się", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegitymacjaScreen(user: com.example.wetpka.model.User, onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Legitymacja Wędkarska", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wyloguj")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Opcjonalne pole */ }) {
                        Icon(painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size), contentDescription = "Waga")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE2EAF1))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE2EAF1))
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC5D1B8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Imię: ${user.firstName} ${user.lastName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Przynależność: ${user.district}", fontSize = 14.sp)
                        Text("Nr Karty: ${user.cardNumber}", fontSize = 14.sp)
                    }
                }
            }

            InfoCard(
                iconId = android.R.drawable.ic_menu_myplaces,
                title = "Opłacenie składek",
                line1 = "Składka członkowska: Opłacona do",
                line2 = "czerwca 2026 ✓"
            )

            InfoCard(
                iconId = android.R.drawable.ic_menu_agenda,
                title = "Ważność Zezwolenia",
                line1 = "Ważność zezwoleń",
                line2 = "Zezwolenie zwykłe: Ważne do czerwca 2026\nZezwolenie morskie: Ważne do czerwca 2026"
            )

            InfoCard(
                iconId = android.R.drawable.star_on,
                title = "Statystyki Ogólne",
                line1 = "Statystyki połowów",
                line2 = "Liczba złowionych ryb (ogółem): 128\nW tym sezonie: 45"
            )

            InfoCard(
                iconId = android.R.drawable.ic_menu_sort_by_size,
                title = "Rekord Wagowy",
                line1 = "Najcięższa ryba",
                line2 = "Szczupak Pospolity: 8.2 kg\nZłowiono: 12.05.2023, Jezioro Śniardwy"
            )

            InfoCard(
                iconId = android.R.drawable.ic_menu_edit,
                title = "Rekord Długości",
                line1 = "Najdłuższa ryba",
                line2 = "Karp Pospolity: 95 cm\nZłowiono: 20.08.2023, Rzeka Odra"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoCard(iconId: Int, title: String, line1: String, line2: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF1E5370)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(line1, fontSize = 14.sp)
                Text(line2, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}