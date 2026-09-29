package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChajaRepository
import com.example.data.model.ChargingRecord
import com.example.data.model.ExpenseRecord
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ChajaTab(val titleHausa: String, val titleEnglish: String) {
    OVERVIEW("Taswira", "Lockers & Overview"),
    ADMIT("Karɓi Waya", "Admit Phone"),
    ACTIVE("Chaja & Shirye", "Active & Ready"),
    EXPENSES("Fetur & Riba", "Expenses & Profit"),
    DIRECTORY("Log & Rahoto", "Directory & Reports")
}

data class CustomerStat(
    val name: String,
    val phone: String,
    val visitsCount: Int,
    val totalSpent: Double
)

class ChajaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChajaRepository

    val allRecords: StateFlow<List<ChargingRecord>>
    val allExpenses: StateFlow<List<ExpenseRecord>>
    val chargingRecords: StateFlow<List<ChargingRecord>>
    val readyRecords: StateFlow<List<ChargingRecord>>
    val deliveredRecords: StateFlow<List<ChargingRecord>>

    val selectedTab = MutableStateFlow(ChajaTab.OVERVIEW)
    val searchQuery = MutableStateFlow("")
    val activeStaff = MutableStateFlow("Musa Admin")
    val receiptRecord = MutableStateFlow<ChargingRecord?>(null)
    val preselectedLocker = MutableStateFlow<String?>(null)
    val toastMessage = MutableStateFlow<String?>(null)

    val allLockerIds: List<String> = buildList {
        for (i in 1..10) add("Locker A-%02d".format(i))
        for (i in 1..10) add("Locker B-%02d".format(i))
    }

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ChajaRepository(db.chargingDao(), db.expenseDao())

        allRecords = repository.allRecords.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allExpenses = repository.allExpenses.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        chargingRecords = repository.chargingRecords.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        readyRecords = repository.readyRecords.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        deliveredRecords = repository.deliveredRecords.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    val searchResults: StateFlow<List<ChargingRecord>> = combine(
        allRecords,
        searchQuery
    ) { records, query ->
        val trimmed = query.trim().lowercase(Locale.ROOT)
        if (trimmed.isEmpty()) {
            emptyList()
        } else {
            records.filter {
                it.id.lowercase(Locale.ROOT).contains(trimmed) ||
                it.customerName.lowercase(Locale.ROOT).contains(trimmed) ||
                it.phoneNumber.contains(trimmed) ||
                it.locker.lowercase(Locale.ROOT).contains(trimmed) ||
                it.brand.lowercase(Locale.ROOT).contains(trimmed) ||
                it.model.lowercase(Locale.ROOT).contains(trimmed)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerStats: StateFlow<List<CustomerStat>> = allRecords.map { records ->
        records.groupBy { it.phoneNumber }
            .map { (phone, phoneRecords) ->
                CustomerStat(
                    name = phoneRecords.first().customerName,
                    phone = phone,
                    visitsCount = phoneRecords.size,
                    totalSpent = phoneRecords.sumOf { it.fee }
                )
            }.sortedByDescending { it.visitsCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: ChajaTab) {
        selectedTab.value = tab
    }

    fun showToast(msg: String) {
        toastMessage.value = msg
    }

    fun clearToast() {
        toastMessage.value = null
    }

    fun openReceipt(record: ChargingRecord) {
        receiptRecord.value = record
    }

    fun closeReceipt() {
        receiptRecord.value = null
    }

    fun selectLockerForAdmission(lockerId: String) {
        preselectedLocker.value = lockerId
        selectedTab.value = ChajaTab.ADMIT
    }

    fun clearPreselectedLocker() {
        preselectedLocker.value = null
    }

    fun getNextChargingId(records: List<ChargingRecord>): String {
        val maxId = records.mapNotNull {
            val numStr = it.id.removePrefix("CHG-")
            numStr.toIntOrNull()
        }.maxOrNull() ?: 0
        return "CHG-%05d".format(maxId + 1)
    }

    fun admitNewPhone(
        name: String,
        phone: String,
        staff: String,
        brand: String,
        model: String,
        color: String,
        cableType: String,
        imei: String,
        condition: String,
        locker: String,
        fee: Double
    ) {
        viewModelScope.launch {
            val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val nextId = getNextChargingId(allRecords.value)
            val record = ChargingRecord(
                id = nextId,
                customerName = name.trim(),
                phoneNumber = phone.trim(),
                brand = brand.trim(),
                model = model.trim(),
                color = color.trim(),
                cableType = cableType,
                staffName = staff,
                imei = imei.trim().ifEmpty { "N/A" },
                condition = condition.trim().ifEmpty { "Normal" },
                locker = locker,
                fee = fee,
                status = "Charging",
                timestamp = nowStr,
                pickupTime = null
            )
            repository.insertRecord(record)
            showToast("An karɓi wayar $name cikin nasara ($nextId)!")
            openReceipt(record)
            clearPreselectedLocker()
        }
    }

    fun markReady(recordId: String) {
        viewModelScope.launch {
            repository.updateStatus(recordId, "Ready")
            showToast("Waya $recordId ta cika, an mayar da ita 'Ready'!")
        }
    }

    fun deliverPhone(recordId: String) {
        viewModelScope.launch {
            val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            repository.updateStatus(recordId, "Delivered", nowStr)
            showToast("An miƙa waya $recordId ga mai ita!")
        }
    }

    fun deleteRecord(record: ChargingRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
            showToast("An goge bayanan $record.id")
        }
    }

    fun addExpense(category: String, amount: Double, notes: String) {
        viewModelScope.launch {
            val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val expense = ExpenseRecord(
                date = nowStr,
                category = category,
                amount = amount,
                notes = notes.trim().ifEmpty { "-" }
            )
            repository.insertExpense(expense)
            showToast("An shigar da kuɗin $category (₦$amount)!")
        }
    }

    fun deleteExpense(expense: ExpenseRecord) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            showToast("An goge wannan kudin da aka kashe")
        }
    }

    fun generateCsvExport(records: List<ChargingRecord>, expenses: List<ExpenseRecord>): String {
        val sb = StringBuilder()
        sb.append("=== CHAJA MASTER - CHARGING RECORDS ===\n")
        sb.append("ID,Customer Name,Phone Number,Staff,Brand,Model,Color,Cable,Locker,Fee,Status,Timestamp,Pickup Time\n")
        records.forEach { r ->
            sb.append("\"${r.id}\",\"${r.customerName}\",\"${r.phoneNumber}\",\"${r.staffName}\",\"${r.brand}\",\"${r.model}\",\"${r.color}\",\"${r.cableType}\",\"${r.locker}\",${r.fee},\"${r.status}\",\"${r.timestamp}\",\"${r.pickupTime ?: ""}\"\n")
        }
        sb.append("\n=== EXPENSES / KUDIN FETUR & GYARA ===\n")
        sb.append("Date,Category,Amount,Notes\n")
        expenses.forEach { e ->
            sb.append("\"${e.date}\",\"${e.category}\",${e.amount},\"${e.notes}\"\n")
        }
        return sb.toString()
    }

    fun generateJsonBackup(records: List<ChargingRecord>, expenses: List<ExpenseRecord>): String {
        val root = JSONObject()
        val phonesArray = JSONArray()
        records.forEach { r ->
            val obj = JSONObject().apply {
                put("id", r.id)
                put("customerName", r.customerName)
                put("phoneNumber", r.phoneNumber)
                put("staffName", r.staffName)
                put("brand", r.brand)
                put("model", r.model)
                put("color", r.color)
                put("cableType", r.cableType)
                put("imei", r.imei)
                put("condition", r.condition)
                put("locker", r.locker)
                put("fee", r.fee)
                put("status", r.status)
                put("timestamp", r.timestamp)
                put("pickupTime", r.pickupTime ?: JSONObject.NULL)
            }
            phonesArray.put(obj)
        }
        val expArray = JSONArray()
        expenses.forEach { e ->
            val obj = JSONObject().apply {
                put("date", e.date)
                put("category", e.category)
                put("amount", e.amount)
                put("notes", e.notes)
            }
            expArray.put(obj)
        }
        root.put("phones", phonesArray)
        root.put("expenses", expArray)
        root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        return root.toString(2)
    }

    fun restoreFromJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val phonesArray = root.optJSONArray("phones")
            val expArray = root.optJSONArray("expenses")

            val restoredPhones = mutableListOf<ChargingRecord>()
            if (phonesArray != null) {
                for (i in 0 until phonesArray.length()) {
                    val obj = phonesArray.getJSONObject(i)
                    restoredPhones.add(
                        ChargingRecord(
                            id = obj.getString("id"),
                            customerName = obj.getString("customerName"),
                            phoneNumber = obj.getString("phoneNumber"),
                            brand = obj.getString("brand"),
                            model = obj.getString("model"),
                            color = obj.optString("color", "Default"),
                            cableType = obj.optString("cableType", "Type-C"),
                            staffName = obj.optString("staffName", "Admin"),
                            imei = obj.optString("imei", "N/A"),
                            condition = obj.optString("condition", "Normal"),
                            locker = obj.getString("locker"),
                            fee = obj.optDouble("fee", 200.0),
                            status = obj.optString("status", "Charging"),
                            timestamp = obj.getString("timestamp"),
                            pickupTime = if (obj.isNull("pickupTime")) null else obj.getString("pickupTime")
                        )
                    )
                }
            }

            val restoredExpenses = mutableListOf<ExpenseRecord>()
            if (expArray != null) {
                for (i in 0 until expArray.length()) {
                    val obj = expArray.getJSONObject(i)
                    restoredExpenses.add(
                        ExpenseRecord(
                            date = obj.getString("date"),
                            category = obj.getString("category"),
                            amount = obj.getDouble("amount"),
                            notes = obj.optString("notes", "-")
                        )
                    )
                }
            }

            viewModelScope.launch {
                repository.clearAllData()
                if (restoredPhones.isNotEmpty()) repository.insertRecords(restoredPhones)
                if (restoredExpenses.isNotEmpty()) repository.insertExpenses(restoredExpenses)
                showToast("An dawo da dukkan bayanan da aka yi backup!")
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Kuskure: File din bai yi daidai ba!")
            false
        }
    }
}
