package com.example.wetpka.data

import com.example.wetpka.R
import com.example.wetpka.model.Fish
import com.example.wetpka.model.WaterBody

object MockData {
    // Tymczasowo używamy domyślnej ikony Androida jako obrazka ryby (R.drawable.ic_launcher_foreground)
    // Kiedy wrzucisz zdjęcia, po prostu zmienisz to np. na R.drawable.szczupak
    val fishes = listOf(
        Fish(
            id = 1,
            name = "Szczupak pospolity",
            latinName = "Esox lucius",
            englishName = "Northern pike",
            category = "Drapieżne",
            protectionSize = "50 cm",
            dailyLimit = "2 szt.",
            protectionPeriod = "1 stycznia - 30 kwietnia",
            spawningTime = "wczesna wiosna (marzec-kwiecień)",
            regions = "cała Polska (powszechny), Ameryka Północna, Europa, Azja",
            habitat = "czyste, wolno płynące rzeki, jeziora nizinne, zbiorniki zaporowe z roślinnością",
            goodBites = "wiosna, jesień, rano, wieczorem",
            badBites = "upalne dni letnie, południe",
            imageResId = R.drawable.szczupak
        ),
        Fish(
            id = 2,
            name = "Okoń pospolity",
            latinName = "Perca fluviatilis",
            englishName = "European perch",
            category = "Drapieżne",
            protectionSize = "15 cm",
            dailyLimit = "5 kg",
            protectionPeriod = "brak",
            spawningTime = "wiosna (kwiecień-maj)",
            regions = "cała Polska",
            habitat = "jeziora, stawy, rzeki, zbiorniki zaporowe",
            goodBites = "późne lato, jesień, wczesny ranek",
            badBites = "środek upalnego dnia",
            imageResId = R.drawable.okon
        ),
        Fish(
            id = 3,
            name = "Karp",
            latinName = "Cyprinus carpio",
            englishName = "Common carp",
            category = "Spokojnego żeru",
            protectionSize = "30 cm",
            dailyLimit = "3 szt.",
            protectionPeriod = "brak",
            spawningTime = "późna wiosna, lato (maj-czerwiec)",
            regions = "cała Polska",
            habitat = "ciepłe, płytkie wody, stawy hodowlane, jeziora z mulistym dnem",
            goodBites = "ciepłe noce letnie, wczesny ranek",
            badBites = "nagłe ochłodzenia, zima",
            imageResId = R.drawable.karp
        ),
        Fish(
            id = 4,
            name = "Leszcz",
            latinName = "Abramis brama",
            englishName = "Common bream",
            category = "Spokojnego żeru",
            protectionSize = "25 cm",
            dailyLimit = "5 kg",
            protectionPeriod = "brak",
            spawningTime = "wiosna (maj-czerwiec)",
            regions = "cała Polska",
            habitat = "głębokie jeziora, duże rzeki o wolnym nurcie",
            goodBites = "letnie noce, wczesny świt",
            badBites = "bardzo zimna woda",
            imageResId = R.drawable.leszcz
        ),
        Fish(
            id = 5,
            name = "Sandacz",
            latinName = "Sander lucioperca",
            englishName = "Zander",
            category = "Drapieżne",
            protectionSize = "50 cm",
            dailyLimit = "2 szt.",
            protectionPeriod = "1 stycznia - 31 maja",
            spawningTime = "wiosna (kwiecień-maj)",
            regions = "cała Polska",
            habitat = "głębsze rzeki, zbiorniki zaporowe z twardym, żwirowym dnem",
            goodBites = "ciemne noce, świt, zmierzch",
            badBites = "słoneczne dni w płytkiej wodzie",
            imageResId = R.drawable.sandacz
        ),
        Fish(
            id = 6,
            name = "Płoć",
            latinName = "Rutilus rutilus",
            englishName = "Common roach",
            category = "Spokojnego żeru",
            protectionSize = "15 cm",
            dailyLimit = "5 kg",
            protectionPeriod = "Brak",
            spawningTime = "wiosna (kwiecień-maj)",
            regions = "cała Polska",
            habitat = "prawie wszystkie wody słodkie, od rzek po małe stawy",
            goodBites = "cały rok (najlepiej wiosna i jesień)",
            badBites = "skrajne upały",
            imageResId = R.drawable.ploc
        )
    )
    val waterBodies = listOf(
        WaterBody(1, "Rzeka Odra - Odcinek Wrocławski", "Dolnośląskie", "Długość: 850 km", "Okręg: PZW Wrocław", 51.1079, 17.0385, R.drawable.odra),
        WaterBody(2, "Zalew Mietkowski", "Dolnośląskie", "Wielkość: 9 km²", "Okręg: PZW Wrocław", 50.9667, 16.6167, R.drawable.mietkowski),
        WaterBody(3, "Staw Pilczycki", "Wrocław", "Wielkość: 8 ha", "Okręg: PZW Wrocław", 51.1411, 16.9422, R.drawable.pilczyce),
        WaterBody(4, "Jezioro Bajkał (Kamieniec)", "Dolnośląskie", "Wielkość: 60 ha", "Okręg: PZW Wrocław", 51.0768, 17.1824, R.drawable.bajkal),
        WaterBody(5, "Rzeka Bystrzyca", "Dolnośląskie", "Długość: 95 km", "Okręg: PZW Wrocław", 51.1444, 16.9328, R.drawable.bystrzyca),
        WaterBody(6, "Rzeka Oława", "Dolnośląskie", "Długość: 99 km", "Okręg: PZW Wrocław", 51.1033, 17.0549, R.drawable.olawa),
        WaterBody(7, "Kąpielisko Kopalnia", "Paniowice", "Wielkość: 18 ha", "łowisko komercyjne", 51.1897, 16.9381, R.drawable.kopalnia),
        WaterBody(8, "Staw Leśnicki", "Wrocław", "Wielkość: 3 ha", "Okręg: PZW Wrocław", 51.1481, 16.8617, R.drawable.lesnicki),
        WaterBody(9, "Rzeka Widawa", "Dolnośląskie", "Długość: 109 km", "Okręg: PZW Wrocław", 51.1683, 17.0425, R.drawable.widawa),
        WaterBody(10, "Zbiornik Sulistrowice", "Dolnośląskie", "Wielkość: 5 ha", "Okręg: PZW Wrocław", 50.8522, 16.6978, R.drawable.sulistrowice)
    )
}