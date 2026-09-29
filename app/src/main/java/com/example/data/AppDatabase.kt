package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ChargingDao
import com.example.data.dao.ExpenseDao
import com.example.data.model.ChargingRecord
import com.example.data.model.ExpenseRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(entities = [ChargingRecord::class, ExpenseRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chargingDao(): ChargingDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chaja_master_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.chargingDao(), database.expenseDao())
                    }
                }
            }

            private suspend fun populateInitialData(chargingDao: ChargingDao, expenseDao: ExpenseDao) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val now = Date()
                val oneHourAgo = Date(now.time - 3600 * 1000)
                val threeHoursAgo = Date(now.time - 3 * 3600 * 1000)
                val yesterday = Date(now.time - 24 * 3600 * 1000)

                chargingDao.insertRecords(
                    listOf(
                        ChargingRecord(
                            id = "CHG-00001",
                            customerName = "Ahmadu Bello",
                            phoneNumber = "08031234567",
                            brand = "Tecno",
                            model = "Spark 10 Pro",
                            color = "Black",
                            cableType = "Type-C",
                            staffName = "Musa Admin",
                            imei = "864291040123456",
                            condition = "Minor scratches, battery 12%",
                            locker = "Locker A-01",
                            fee = 200.0,
                            status = "Charging",
                            timestamp = dateFormat.format(oneHourAgo),
                            pickupTime = null
                        ),
                        ChargingRecord(
                            id = "CHG-00002",
                            customerName = "Fatima Sani",
                            phoneNumber = "08029876543",
                            brand = "iPhone",
                            model = "12 Pro",
                            color = "Gold",
                            cableType = "iPhone Lightning",
                            staffName = "Ibrahim (Shift A)",
                            imei = "359120491029384",
                            condition = "Screen guard cracked",
                            locker = "Locker A-02",
                            fee = 300.0,
                            status = "Ready",
                            timestamp = dateFormat.format(threeHoursAgo),
                            pickupTime = null
                        ),
                        ChargingRecord(
                            id = "CHG-00003",
                            customerName = "Aliyu Garba",
                            phoneNumber = "08145556677",
                            brand = "Samsung",
                            model = "Galaxy A14",
                            color = "Silver",
                            cableType = "Type-C",
                            staffName = "Sani (Shift B)",
                            imei = "354029194012845",
                            condition = "Good condition, battery 5%",
                            locker = "Locker B-01",
                            fee = 200.0,
                            status = "Delivered",
                            timestamp = dateFormat.format(yesterday),
                            pickupTime = dateFormat.format(Date(yesterday.time + 2 * 3600 * 1000))
                        )
                    )
                )

                expenseDao.insertExpenses(
                    listOf(
                        ExpenseRecord(
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now) + " 08:00",
                            category = "Fetur (Petrol)",
                            amount = 3500.0,
                            notes = "Lita 5 na Generator"
                        ),
                        ExpenseRecord(
                            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday) + " 10:30",
                            category = "Servicing / Gyara",
                            amount = 1500.0,
                            notes = "Gyaran Starter Jenareta"
                        )
                    )
                )
            }
        }
    }
}
