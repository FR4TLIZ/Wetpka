package com.example.wetpka.model

data class Fish(
    val id: Int,
    val name: String,
    val latinName: String,
    val englishName: String, // Dodano angielską nazwę
    val category: String, // "Drapieżne" lub "Spokojnego żeru"
    val protectionSize: String,
    val dailyLimit: String,
    val protectionPeriod: String,
    val spawningTime: String, // Czas tarła
    val regions: String, // Regiony występowania
    val habitat: String, // Preferowane akweny
    val goodBites: String, // Dobre brania
    val badBites: String, // Słabe brania
    val imageResId: Int
)