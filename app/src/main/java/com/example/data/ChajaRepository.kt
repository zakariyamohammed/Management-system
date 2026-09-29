package com.example.data

import com.example.data.dao.ChargingDao
import com.example.data.dao.ExpenseDao
import com.example.data.model.ChargingRecord
import com.example.data.model.ExpenseRecord
import kotlinx.coroutines.flow.Flow

class ChajaRepository(
    private val chargingDao: ChargingDao,
    private val expenseDao: ExpenseDao
) {
    val allRecords: Flow<List<ChargingRecord>> = chargingDao.getAllRecords()
    val chargingRecords: Flow<List<ChargingRecord>> = chargingDao.getChargingRecords()
    val readyRecords: Flow<List<ChargingRecord>> = chargingDao.getReadyRecords()
    val deliveredRecords: Flow<List<ChargingRecord>> = chargingDao.getDeliveredRecords()
    val allExpenses: Flow<List<ExpenseRecord>> = expenseDao.getAllExpenses()

    suspend fun getRecordById(id: String): ChargingRecord? = chargingDao.getRecordById(id)

    suspend fun insertRecord(record: ChargingRecord) = chargingDao.insertRecord(record)

    suspend fun insertRecords(records: List<ChargingRecord>) = chargingDao.insertRecords(records)

    suspend fun updateRecord(record: ChargingRecord) = chargingDao.updateRecord(record)

    suspend fun updateStatus(id: String, newStatus: String, pickupTime: String? = null) =
        chargingDao.updateStatus(id, newStatus, pickupTime)

    suspend fun deleteRecord(record: ChargingRecord) = chargingDao.deleteRecord(record)

    suspend fun insertExpense(expense: ExpenseRecord) = expenseDao.insertExpense(expense)

    suspend fun insertExpenses(expenses: List<ExpenseRecord>) = expenseDao.insertExpenses(expenses)

    suspend fun deleteExpense(expense: ExpenseRecord) = expenseDao.deleteExpense(expense)

    suspend fun clearAllData() {
        chargingDao.deleteAll()
        expenseDao.deleteAll()
    }
}
