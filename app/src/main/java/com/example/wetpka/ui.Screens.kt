package com.example.wetpka.ui

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wetpka.data.CatchRecord
import com.example.wetpka.data.MockData
import com.example.wetpka.model.Fish
import com.example.wetpka.model.WaterBody
import com.example.wetpka.viewmodel.LogbookViewModel
import com.example.wetpka.viewmodel.ProfileViewModel

val filterOptions = listOf("Wszystkie", "Drapieżne", "Spokojnego żeru")

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AtlasScreen(onFishClick: (Int) -> Unit = {}) {
    var selectedFilter by remember { mutableStateOf("Wszystkie") }

    var searchQuery by remember { mutableStateOf("") }

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
            TopAppBar(title = { Text("Atlas ryb", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Wyszukaj gatunek ryby...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ikona wyszukiwania") },
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true
            )

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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredFishes) { fish ->
                    FishGridItem(fish = fish, onClick = { onFishClick(fish.id) })
                }
            }
        }
    }
}

@Composable
fun FishGridItem(fish: Fish, onClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = fish.imageResId),
                contentDescription = "Zdjęcie: ${fish.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fish.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )

                Text(
                    text = fish.latinName,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1
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

            Image(
                painter = painterResource(id = fish.imageResId),
                contentDescription = "Zdjęcie: ${fish.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

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

    fun fetchLocation() {
        val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                } else {
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

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
        if (isGranted) {
            fetchLocation()
        }
    }

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

            Image(
                painter = painterResource(id = waterBody.imageResId),
                contentDescription = waterBody.name,
                modifier = Modifier.size(80.dp).padding(4.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = waterBody.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = waterBody.region, fontSize = 14.sp)
                Text(text = waterBody.sizeInfo, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                Text(text = waterBody.district, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }

            Column(horizontalAlignment = Alignment.End) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(text = distanceText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookScreen(onNavigateToLogin: () -> Unit = {}) {
    val logbookViewModel: LogbookViewModel = viewModel()
    val uiState by logbookViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                logbookViewModel.refreshSession()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.loggedInUsername != null) {
        LogbookContentScreen(
            ownerUsername = uiState.loggedInUsername!!,
            onLogout = { logbookViewModel.logout() },
            logbookViewModel = logbookViewModel
        )
    } else if (uiState.useLocal) {
        LogbookContentScreen(
            ownerUsername = "localuser",
            onLogout = { logbookViewModel.leaveLocalMode() },
            logbookViewModel = logbookViewModel
        )
    } else {
        LogbookChoiceScreen(
            onChooseLocal = { logbookViewModel.chooseLocal() },
            onChooseLogin = onNavigateToLogin
        )
    }
}

@Composable
fun LogbookChoiceScreen(onChooseLocal: () -> Unit, onChooseLogin: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2EAF1)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Color(0xFF1E5370)
                )
                Text(
                    text = "Rejestr połowów",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Zaloguj się, aby synchronizować rejestr z kontem, lub kontynuuj lokalnie.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onChooseLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5370))
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Zaloguj się", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onChooseLocal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Użyj lokalnie", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E5370))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookContentScreen(
    ownerUsername: String,
    onLogout: () -> Unit,
    logbookViewModel: LogbookViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val catches by logbookViewModel.catches.collectAsState(initial = emptyList())

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
    var recordToEdit by remember { mutableStateOf<CatchRecord?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Rejestr połowów", fontWeight = FontWeight.Bold)
                        Text(
                            text = if (ownerUsername == "localuser") "Tryb lokalny" else "Zalogowano: $ownerUsername",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        recordToEdit = null
                        showAddDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Dodaj połów",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    if (ownerUsername != "localuser") {
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Wyloguj",
                                tint = Color.Red
                            )
                        }
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

                    val newRecord = CatchRecord(
                        id = currentRecordId,
                        ownerUsername = ownerUsername,
                        date = date, time = time, spotNumber = finalSpot, fishSpecies = species,
                        pieces = p, totalWeight = roundedWeight, length = roundedLength
                    )

                    if (!isEditMode) {
                        val mediaPlayer = android.media.MediaPlayer.create(context, com.example.wetpka.R.raw.gulp)
                        mediaPlayer.setVolume(1.0f, 1.0f)
                        mediaPlayer.start()
                        mediaPlayer.setOnCompletionListener { it.release() }
                    }
                    logbookViewModel.saveCatch(newRecord, isEditMode)

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
                                logbookViewModel.deleteCatch(recordToDelete)
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

@Composable
fun ProfileScreen() {
    val profileViewModel: ProfileViewModel = viewModel()
    val uiState by profileViewModel.uiState.collectAsState()
    val catches by profileViewModel.catches.collectAsState(initial = emptyList())
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                profileViewModel.loadSession()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.loggedInUser != null) {
        LegitymacjaScreen(
            user = uiState.loggedInUser!!,
            catches = catches,
            onLogout = { profileViewModel.logout() }
        )
    } else {
        LoginScreen(
            onLogin = { username, password -> profileViewModel.login(username, password) },
            errorMessage = uiState.loginError,
            onClearError = { profileViewModel.clearLoginError() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    errorMessage: String?,
    onClearError: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2EAF1))
    ) {
        Image(
            painter = painterResource(id = com.example.wetpka.R.drawable.icon),
            contentDescription = "Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
                .size(100.dp)
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
                    onValueChange = { username = it; onClearError() },
                    label = { Text("Nazwa użytkownika") },
                    leadingIcon = { Icon(painterResource(id = android.R.drawable.ic_dialog_email), contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E5370),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; onClearError() },
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
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onLogin(username, password) }
                    ),
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
                    onClick = { onLogin(username, password) },
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

private fun isDateValid(mmYyyy: String): Boolean {
    if (mmYyyy.isBlank()) return false
    return try {
        val parts = mmYyyy.split(".")
        val month = parts[0].toInt()
        val year = parts[1].toInt()
        val cal = java.util.Calendar.getInstance()
        val currentYear = cal.get(java.util.Calendar.YEAR)
        val currentMonth = cal.get(java.util.Calendar.MONTH) + 1
        year > currentYear || (year == currentYear && month >= currentMonth)
    } catch (e: Exception) { false }
}

private fun formatMonthYear(mmYyyy: String): String {
    if (mmYyyy.isBlank()) return ""
    return try {
        val parts = mmYyyy.split(".")
        val month = parts[0].toInt()
        val year = parts[1].toInt()
        val monthNames = listOf(
            "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec",
            "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"
        )
        "${monthNames[month - 1]} $year"
    } catch (e: Exception) { mmYyyy }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegitymacjaScreen(user: com.example.wetpka.model.User, catches: List<CatchRecord>, onLogout: () -> Unit) {
    val totalFish = catches.sumOf { it.pieces }
    val heaviestCatch = catches.maxByOrNull { it.totalWeight }
    val longestCatch = catches.maxByOrNull { it.length }

    val membershipValid = isDateValid(user.membershipPaidTo)
    val membershipText = if (user.membershipPaidTo.isBlank()) "Brak informacji"
        else formatMonthYear(user.membershipPaidTo)

    val permitValid = if (user.permitValidTo.isBlank()) null else isDateValid(user.permitValidTo)
    val seaPermitValid = if (user.seaPermitValidTo.isBlank()) null else isDateValid(user.seaPermitValidTo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Legitymacja", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Wyloguj",
                            tint = Color.Red
                        )
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
                        Text("${user.firstName} ${user.lastName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Przynależność: ${user.district}", fontSize = 14.sp)
                        Text("Nr Karty: ${user.cardNumber}", fontSize = 14.sp)
                    }
                }
            }

            PermitCard(
                iconId = android.R.drawable.ic_menu_myplaces,
                title = "Opłacenie składek",
                label = "Składka członkowska: opłacona do",
                dateText = membershipText,
                isValid = membershipValid
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_agenda),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF1E5370)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Ważność zezwolenia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Text("Zezwolenie zwykłe: ", fontSize = 13.sp, color = Color.DarkGray)
                        if (permitValid == null) {
                            Text("BRAK", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        } else {
                            val pColor = if (permitValid) Color(0xFF2E7D32) else Color.Red
                            Text("Ważne do ", fontSize = 13.sp, color = Color.DarkGray)
                            Text(formatMonthYear(user.permitValidTo) + if (permitValid) " ✓" else " ✗", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = pColor)
                        }
                    }

                    Row {
                        Text("Zezwolenie morskie: ", fontSize = 13.sp, color = Color.DarkGray)
                        if (seaPermitValid == null) {
                            Text("BRAK", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        } else {
                            val sColor = if (seaPermitValid) Color(0xFF2E7D32) else Color.Red
                            Text("Ważne do ", fontSize = 13.sp, color = Color.DarkGray)
                            Text(formatMonthYear(user.seaPermitValidTo) + if (seaPermitValid) " ✓" else " ✗", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = sColor)
                        }
                    }
                }
            }

            if (catches.isEmpty()) {
                InfoCard(
                    iconId = android.R.drawable.star_on,
                    title = "Statystyki ogólne",
                    line1 = "",
                    line2 = "Dodaj połowy do rejestru aby zobaczyć statystyki"
                )
            } else {
                InfoCard(
                    iconId = android.R.drawable.star_on,
                    title = "Statystyki ogólne",
                    line1 = "Statystyki połowów",
                    line2 = "Liczba złowionych ryb (ogółem): $totalFish\nLiczba wpisów w rejestrze: ${catches.size}"
                )

                if (heaviestCatch != null) {
                    InfoCard(
                        iconId = android.R.drawable.ic_menu_sort_by_size,
                        title = "Rekord wagowy",
                        line1 = "Najcięższa ryba",
                        line2 = "${heaviestCatch.fishSpecies}: ${heaviestCatch.totalWeight} kg\nZłowiono: ${heaviestCatch.date}, łowisko ${heaviestCatch.spotNumber}"
                    )
                }

                if (longestCatch != null) {
                    InfoCard(
                        iconId = android.R.drawable.ic_menu_edit,
                        title = "Rekord długości",
                        line1 = "Najdłuższa ryba",
                        line2 = "${longestCatch.fishSpecies}: ${longestCatch.length} cm\nZłowiono: ${longestCatch.date}, łowisko ${longestCatch.spotNumber}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PermitCard(iconId: Int, title: String, label: String, dateText: String, isValid: Boolean) {
    val statusColor = if (isValid) Color(0xFF2E7D32) else Color.Red
    val statusIcon = if (isValid) "✓" else "✗"
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
                Text(label, fontSize = 14.sp)
                Row {
                    Text(
                        dateText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    Text(
                        " $statusIcon",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
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

