package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations WHERE donorId = :donorId ORDER BY date DESC")
    fun getDonationsByDonor(donorId: String): Flow<List<DonationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDonations(donations: List<DonationEntity>)
}
