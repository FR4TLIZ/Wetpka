# WedPKA

Aplikacja mobilna dla wędkarzy zrzeszonych w PZW (Polskim Związku Wędkarskim), stworzona z myślą o okręgu wrocławskim. Ułatwia prowadzenie rejestru połowów, przeglądanie atlasu ryb oraz wyszukiwanie łowisk w regionie.

## Funkcjonalności

### Atlas ryb
- Przeglądanie gatunków ryb w siatce z animowanymi zdjęciami
- Filtrowanie na kategorie: Drapieżne / Spokojnego żeru / Wszystkie
- Wyszukiwanie po nazwie polskiej, łacińskiej lub angielskiej
- Szczegółowe karty gatunkowe z regulacjami wędkarskimi:
  - wymiar ochronny, limit dobowy, okres ochronny, czas tarła
  - regiony występowania, preferowane siedlisko
  - informacje o porach brania (dobre / słabe)

### Zbiorniki PZW
- Lista łowisk okręgu wrocławskiego (rzeki, jeziora, stawy, zbiorniki)
- Obliczanie odległości od aktualnej lokalizacji GPS użytkownika
- Wyszukiwarka po nazwie, regionie lub okręgu

### Rejestr połowów
- Praca w trybie lokalnym (bez konta) lub z logowaniem do konta PZW
- Dodawanie, edytowanie i usuwanie wpisów połowów
- Formularz z wyborem daty/czasu przez natywne date/time pickery
- Wybór gatunku bezpośrednio z atlasu (dropdown)
- Walidacja danych (waga, długość, zakaz wpisywania połowów z przyszłości)
- Sortowanie rekordów od najnowszego
- Wbudowany poradnik wideo

### Legitymacja / Profil
- Wyświetlanie danych członkowskich (imię, nazwisko, nr karty, okręg)
- Sprawdzanie ważności składki członkowskiej i zezwoleń (zwykłe i morskie)
- Statystyki ogólne: łączna liczba ryb, rekord wagowy, rekord długości

## Stos technologiczny

| Warstwa | Technologia |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Nawigacja | Navigation Compose |
| Architektura | MVVM (ViewModel + StateFlow) |
| Baza danych | Room (SQLite) |
| Lokalizacja | Google Play Services Location |
| Język | Kotlin |
| Build | Gradle (KTS) + KSP |

## Wymagania

- Android SDK 24+ (Android 7.0 Nougat)
- compileSdk 36
- Android Studio Hedgehog lub nowsze
- Urządzenie / emulator z Google Play Services (dla funkcji GPS)

## Budowanie projektu

Projekt do tej pory budowany i uruchamiany był w Android Studio.

APK debug znajdzie się w `app/build/outputs/apk/debug/app-debug.apk`.

## Struktura projektu

```
app/src/main/java/com/example/wetpka/
├── MainActivity.kt            # Punkt wejścia, nawigacja
├── model/
│   ├── Fish.kt                # Model danych ryby
│   ├── WaterBody.kt           # Model łowiska
│   └── User.kt                # Model użytkownika (Room Entity)
├── data/
│   ├── Database.kt            # Konfiguracja Room
│   ├── UserDao.kt             # DAO – zapytania do bazy
│   ├── MockData.kt            # Dane statyczne (ryby, łowiska)
│   ├── SessionStore.kt        # Przechowywanie sesji
│   └── WetpkaRepository.kt   # Repozytorium danych
├── viewmodel/
│   ├── LogbookViewModel.kt    # VM rejestru połowów
│   └── ProfileViewModel.kt   # VM profilu/logowania
└── ui/
    ├── Screens.kt             # Wszystkie ekrany Compose
    └── theme/                 # Kolory, typografia, temat
```

## Uwagi

- Dane ryb i łowisk są przechowywane statycznie w `MockData.kt` – projekt nie wymaga połączenia z siecią.
- Hasła użytkowników są przechowywane jako hasze (SHA-256) w lokalnej bazie Room.
- Aplikacja skierowana jest na rejon wrocławski PZW; łowiska można rozszerzyć edytując `MockData.waterBodies`.
