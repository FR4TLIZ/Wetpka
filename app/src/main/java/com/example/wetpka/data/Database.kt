package com.example.wetpka.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.wetpka.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


// 1. Tabela w bazie danych
@Entity(tableName = "catches")
data class CatchRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val time: String,
    val spotNumber: String,
    val fishSpecies: String,
    val pieces: Int,
    val totalWeight: Double,
    val length: Double
)

// 2. Narzędzie do operowania na bazie (DAO)
@Dao
interface CatchDao {
    @Query("SELECT * FROM catches ORDER BY id ASC")
    fun getAllCatches(): Flow<List<CatchRecord>>

    @Insert
    suspend fun insertCatch(catchRecord: CatchRecord): Long

    @Update
    suspend fun updateCatch(catchRecord: CatchRecord): Int

    @Delete
    suspend fun deleteCatch(catchRecord: CatchRecord): Int
}

// Pomocnicza funkcja hashowania haseł
private fun hashPassword(password: String): String {
    val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// 3. Główna klasa Bazy Danych
@Database(entities = [CatchRecord::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catchDao(): CatchDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "logbook_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val dao = database.userDao()
                                    if (dao.getUserCount() == 0) {
                                        dao.insertUser(
                                            User(
                                                username = "jan.kowalski",
                                                passwordHash = hashPassword("haslo123"),
                                                firstName = "Jan",
                                                lastName = "Kowalski",
                                                cardNumber = "PZW-2024-001234",
                                                district = "Okręg PZW Wrocław",
                                                validFrom = "01.01.2024",
                                                validTo = "31.12.2026",
                                                memberSince = "2018"
                                            )
                                        )
                                        dao.insertUser(
                                            User(
                                                username = "anna.nowak",
                                                passwordHash = hashPassword("wędka456"),
                                                firstName = "Anna",
                                                lastName = "Nowak",
                                                cardNumber = "PZW-2024-005678",
                                                district = "Okręg PZW Kraków",
                                                validFrom = "01.01.2025",
                                                validTo = "31.12.2026",
                                                memberSince = "2020"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}