package com.example.wetpka.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

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
    fun insertCatch(catchRecord: CatchRecord): Long
}

// 3. Główna klasa Bazy Danych
@Database(entities = [CatchRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catchDao(): CatchDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "logbook_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}