package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AmbulanceDao {
    @Query("SELECT * FROM ambulances ORDER BY serviceName ASC")
    fun getAllAmbulances(): Flow<List<AmbulanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAmbulances(ambulances: List<AmbulanceEntity>)
}
