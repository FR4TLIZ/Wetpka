package com.example.wetpka.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wetpka.R
import com.example.wetpka.data.MockData
import com.example.wetpka.model.Fish

// To są nazwy naszych filtrów (pigułek u góry)
val filterOptions = listOf("Wszystkie", "Drapieżne", "Spokojnego żeru")

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AtlasScreen() {
    // Stan: Przechowuje informację, która pigułka filtru jest aktualnie zaznaczona
    var selectedFilter by remember { mutableStateOf("Wszystkie") }

    // Stan: Przechowuje tekst wpisany w wyszukiwarkę (na razie nieaktywny, jak na makiecie)
    var searchQuery by remember { mutableStateOf("") }

    // Logika filtrowania: Ta zmienna przechowuje tylko ryby pasujące do wybranej pigułki
    val filteredFishes = remember(selectedFilter) {
        if (selectedFilter == "Wszystkie") {
            MockData.fishes
        } else {
            MockData.fishes.filter { it.category == selectedFilter }
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
                    FishGridItem(fish = fish) // To jest nasz pojedynczy kafelek ryby
                }
            }
        }
    }
}

// Składnik UI dla pojedynczego kafelka w siatce (Zdjęcie + Nazwa + Nazwa łacińska)
@Composable
fun FishGridItem(fish: Fish) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Kafelki będą kwadratowe
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
                contentScale = ContentScale.Crop // Przytnij zdjęcie, żeby ładnie wypełniało przestrzeń
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

@Composable
fun MapScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tu będzie Mapa Łowisk")
    }
}

@Composable
fun LogbookScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tu będzie Rejestr Połowów")
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tu będzie Legitymacja")
    }
}