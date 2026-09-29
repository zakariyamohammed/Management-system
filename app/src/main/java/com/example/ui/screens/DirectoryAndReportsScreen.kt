package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChargingRecord
import com.example.data.model.ExpenseRecord
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChajaViewModel
import com.example.ui.viewmodel.CustomerStat

@Composable
fun DirectoryAndReportsScreen(
    viewModel: ChajaViewModel,
    records: List<ChargingRecord>,
    expenses: List<ExpenseRecord>,
    customerStats: List<CustomerStat>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0 = Customers, 1 = Delivered, 2 = Reports & Backup
    var showRestoreDialog by remember { mutableStateOf(false) }

    val deliveredList = remember(records) { records.filter { it.status == "Delivered" } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sub-navigation Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(12.dp))
                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SubTabButton(
                title = "Customers",
                isSelected = selectedSubTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { selectedSubTab = 0 }
            )
            SubTabButton(
                title = "Delivered (${deliveredList.size})",
                isSelected = selectedSubTab == 1,
                modifier = Modifier.weight(1.1f),
                onClick = { selectedSubTab = 1 }
            )
            SubTabButton(
                title = "Reports & Backup",
                isSelected = selectedSubTab == 2,
                modifier = Modifier.weight(1.2f),
                onClick = { selectedSubTab = 2 }
            )
        }

        when (selectedSubTab) {
            0 -> {
                // Customers Register
                if (customerStats.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Babu bayanan abokan ciniki tukuna.", color = Slate400)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(customerStats, key = { it.phone }) { cust ->
                            CustomerCardItem(
                                cust = cust,
                                onFilterCustomer = {
                                    viewModel.searchQuery.value = cust.phone
                                }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Delivered Archive
                if (deliveredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Babu tarihin wayar da aka miƙa tukuna.", color = Slate400)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(deliveredList, key = { it.id }) { phone ->
                            DeliveredCardItem(
                                phone = phone,
                                onViewSlip = { viewModel.openReceipt(phone) }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Reports & Backup Screen
                ReportsAndBackupSection(
                    viewModel = viewModel,
                    records = records,
                    expenses = expenses,
                    context = context,
                    onOpenRestore = { showRestoreDialog = true }
                )
            }
        }
    }

    if (showRestoreDialog) {
        var restoreText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = {
                Text("Dawo da Backup (Restore JSON)", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Manna (paste) bayanan JSON din da ka yi backup domin mayar da duk bayanan tsarin.",
                        fontSize = 12.sp,
                        color = Slate300
                    )
                    OutlinedTextField(
                        value = restoreText,
                        onValueChange = { restoreText = it },
                        placeholder = { Text("Paste JSON here...", color = Slate500) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreText.isNotBlank()) {
                            val success = viewModel.restoreFromJson(restoreText)
                            if (success) showRestoreDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen600)
                ) {
                    Text("Restore Yanzu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Soke")
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SubTabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) BrandGreen600 else Color.Transparent,
            contentColor = if (isSelected) Color.White else Slate400
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun CustomerCardItem(
    cust: CustomerStat,
    onFilterCustomer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cust.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = cust.phone,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate400
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${cust.visitsCount} Sau",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Cyan400,
                        modifier = Modifier
                            .background(Cyan500.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "₦${cust.totalSpent.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandGreen400
                    )
                }

                IconButton(onClick = onFilterCustomer, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Search, contentDescription = "Duba Waya", tint = Slate300)
                }
            }
        }
    }
}

@Composable
private fun DeliveredCardItem(
    phone: ChargingRecord,
    onViewSlip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = phone.id,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Slate300
                    )
                    Text(
                        text = phone.locker,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
                Text(
                    text = "${phone.brand} ${phone.model} • ${phone.customerName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "Miƙa: ${phone.pickupTime ?: phone.timestamp} • Staff: ${phone.staffName}",
                    fontSize = 11.sp,
                    color = Slate400
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₦${phone.fee.toInt()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = BrandGreen400
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onViewSlip,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Slip", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ReportsAndBackupSection(
    viewModel: ChajaViewModel,
    records: List<ChargingRecord>,
    expenses: List<ExpenseRecord>,
    context: Context,
    onOpenRestore: () -> Unit
) {
    val totalRevenue = remember(records) { records.sumOf { it.fee } }
    val totalExpenses = remember(expenses) { expenses.sumOf { it.amount } }
    val netProfit = totalRevenue - totalExpenses

    // Brand counts
    val brandCounts = remember(records) {
        records.groupingBy { it.brand }.eachCount().toList().sortedByDescending { it.second }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Backup & Export Actions Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Ayyukan Sauke Bayanai & Backup",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val csv = viewModel.generateCsvExport(records, expenses)
                            shareContent(context, "Chaja_Master_Report.csv", csv)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen600),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Excel CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val json = viewModel.generateJsonBackup(records, expenses)
                            shareContent(context, "Chaja_Master_Backup.json", json)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue400),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Backup JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = onOpenRestore,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple400),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        // Summary Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReportStatCard("Kudin Shiga", "₦${totalRevenue.toInt()}", BrandGreen400, Modifier.weight(1f))
            ReportStatCard("Kudin Fita", "₦${totalExpenses.toInt()}", Rose400, Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReportStatCard("Yawan Wayoyi", "${records.size}", Amber400, Modifier.weight(1f))
            ReportStatCard("Ribar Gaskiya", "₦${netProfit.toInt()}", if (netProfit >= 0) BrandGreen400 else Rose400, Modifier.weight(1f))
        }

        // Brand Distribution
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Mafi Yawan Brands na Waya (Top Phone Brands)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                val maxCount = brandCounts.maxOfOrNull { it.second } ?: 1
                brandCounts.take(6).forEach { (brand, count) ->
                    val ratio = count.toFloat() / maxCount
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = brand, fontSize = 12.sp, color = Slate200, fontWeight = FontWeight.Medium)
                            Text(text = "$count wayoyi", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = BrandGreen500,
                            trackColor = Slate800
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

private fun shareContent(context: Context, filename: String, content: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, filename)
        putExtra(Intent.EXTRA_TEXT, content)
    }
    context.startActivity(Intent.createChooser(intent, "Aika Bayanai"))
}
