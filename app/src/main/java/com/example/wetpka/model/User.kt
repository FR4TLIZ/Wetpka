package com.example.wetpka.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String,
    val cardNumber: String,           // Numer legitymacji
    val district: String,             // Okręg PZW
    val validFrom: String,            // Data ważności od
    val validTo: String,              // Data ważności do
    val memberSince: String,          // Członek od roku
    val membershipPaidTo: String = "",  // Składka opłacona do (MM.yyyy), puste = brak info
    val permitValidTo: String = "",     // Zezwolenie zwykłe ważne do (MM.yyyy), puste = BRAK
    val seaPermitValidTo: String = ""   // Zezwolenie morskie ważne do (MM.yyyy), puste = BRAK
)

