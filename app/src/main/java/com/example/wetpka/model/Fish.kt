package com.example.wetpka.model

data class Fish(
    val id: Int,
    val name: String,
    val latinName: String,
    val category: String,
    val protectionSize: String,
    val dailyLimit: String,
    val protectionPeriod: String,
    val imageResId: Int
)
