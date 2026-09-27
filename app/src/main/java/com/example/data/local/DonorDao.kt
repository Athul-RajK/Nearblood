package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY name ASC")
    fun getAllDonors(): Flow<List<DonorEntity>>

    @Query("SELECT * FROM donors WHERE uid = :uid LIMIT 1")
    suspend fun getDonorById(uid: String): DonorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: DonorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDonors(donors: List<DonorEntity>)

    @Update
    suspend fun updateDonor(donor: DonorEntity)

    @Query("DELETE FROM donors WHERE uid = :uid")
    suspend fun deleteDonor(uid: String)
}
