package com.moderndev.polyglot.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moderndev.polyglot.db.entity.LanguageCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(language: LanguageCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(languages: List<LanguageCacheEntity>)

    @Query("SELECT * FROM languages")
    fun getMany(): Flow<List<LanguageCacheEntity>>

    @Query("DELETE FROM languages")
    suspend fun clearAll()

}