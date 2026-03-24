package com.example.wetpka.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.wetpka.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@Entity(tableName = "catches")
data class CatchRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerUsername: String = "localuser",
    val date: String,
    val time: String,
    val spotNumber: String,
    val fishSpecies: String,
    val pieces: Int,
    val totalWeight: Double,
    val length: Double
)

@Dao
interface CatchDao {
    @Query("SELECT * FROM catches WHERE ownerUsername = :owner ORDER BY id ASC")
    fun getCatchesByOwner(owner: String): Flow<List<CatchRecord>>

    @Insert
    suspend fun insertCatch(catchRecord: CatchRecord): Long

    @Update
    suspend fun updateCatch(catchRecord: CatchRecord): Int

    @Delete
    suspend fun deleteCatch(catchRecord: CatchRecord): Int
}

private fun hashPassword(password: String): String {
    val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

@Database(entities = [CatchRecord::class, User::class], version = 6, exportSchema = false)
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
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val dao = database.userDao()
                                    if (dao.getUserCount() == 0) {
                                        dao.insertUser(
                                            User(
                                                username = "jan.kowalski",
                                                passwordHash = "a15f8ae07675bfb96e084bfb4f52fb2c22091061aae86e0eb76a55f4e52dd74e",
                                                firstName = "Jan",
                                                lastName = "Kowalski",
                                                cardNumber = "PZW-2024-001234",
                                                district = "Okręg PZW Wrocław",
                                                validFrom = "01.01.2024",
                                                validTo = "31.12.2026",
                                                memberSince = "2018",
                                                membershipPaidTo = "06.2026",
                                                permitValidTo = "06.2026",
                                                seaPermitValidTo = "06.2026"
                                            )
                                        )
                                        dao.insertUser(
                                            User(
                                                username = "anna.nowak",
                                                passwordHash = "9591ffd2c23f288ebbc982f702553da8f64177f95cae92867be608533cb1cf74",
                                                firstName = "Anna",
                                                lastName = "Nowak",
                                                cardNumber = "PZW-2024-005678",
                                                district = "Okręg PZW Kraków",
                                                validFrom = "01.01.2025",
                                                validTo = "31.12.2026",
                                                memberSince = "2020",
                                                membershipPaidTo = "06.2026",
                                                permitValidTo = "12.2025",
                                                seaPermitValidTo = ""
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                CoroutineScope(Dispatchers.IO).launch {
                    instance.userDao().getUserCount()
                }
                instance
            }
        }
    }
}