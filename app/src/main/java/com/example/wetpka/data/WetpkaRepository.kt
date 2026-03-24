package com.example.wetpka.data

import com.example.wetpka.model.User
import kotlinx.coroutines.flow.Flow

class WetpkaRepository(private val database: AppDatabase) {
    fun getCatchesByOwner(ownerUsername: String): Flow<List<CatchRecord>> {
        return database.catchDao().getCatchesByOwner(ownerUsername)
    }

    suspend fun insertCatch(record: CatchRecord) {
        database.catchDao().insertCatch(record)
    }

    suspend fun updateCatch(record: CatchRecord) {
        database.catchDao().updateCatch(record)
    }

    suspend fun deleteCatch(record: CatchRecord) {
        database.catchDao().deleteCatch(record)
    }

    suspend fun findUserById(id: Int): User? {
        return database.userDao().findById(id)
    }

    suspend fun findUserByUsername(username: String): User? {
        return database.userDao().findByUsername(username)
    }
}

