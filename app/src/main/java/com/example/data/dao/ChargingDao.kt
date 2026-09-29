package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChargingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargingDao {
    @Query("SELECT * FROM charging_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ChargingRecord>>

    @Query("SELECT * FROM charging_records WHERE status = 'Charging' ORDER BY timestamp DESC")
    fun getChargingRecords(): Flow<List<ChargingRecord>>

    @Query("SELECT * FROM charging_records WHERE status = 'Ready' ORDER BY timestamp DESC")
    fun getReadyRecords(): Flow<List<ChargingRecord>>

    @Query("SELECT * FROM charging_records WHERE status = 'Delivered' ORDER BY pickupTime DESC")
    fun getDeliveredRecords(): Flow<List<ChargingRecord>>

    @Query("SELECT * FROM charging_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: String): ChargingRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ChargingRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<ChargingRecord>)

    @Update
    suspend fun updateRecord(record: ChargingRecord)

    @Query("UPDATE charging_records SET status = :newStatus, pickupTime = :pickupTime WHERE id = :id")
    suspend fun updateStatus(id: String, newStatus: String, pickupTime: String? = null)

    @Delete
    suspend fun deleteRecord(record: ChargingRecord)

    @Query("DELETE FROM charging_records")
    suspend fun deleteAll()
}
