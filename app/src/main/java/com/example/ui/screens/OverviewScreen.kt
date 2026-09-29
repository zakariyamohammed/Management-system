package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChargingRecord
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChajaTab
import com.example.ui.viewmodel.ChajaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OverviewScreen(
    viewModel: ChajaViewModel,
    records: List<ChargingRecord>,
    modifier: Modifier = Modifier
) {
    val activeCharging = remember(records) { records.filter { it.status == "Charging" } }
    val readyList = remember(records) { records.filter { it.status == "Ready" } }
    val occupiedCount = activeCharging.size + readyList.size
    val totalLockers = 20
    val occupancyRatio = (occupiedCount.toFloat() / totalLockers).coerceIn(0f, 1f)

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayRevenue = remember(records, todayStr) {
        records.filter { it.timestamp.startsWith(todayStr) }.sumOf { it.fee }
    }

    var selectedPhoneForQuickAction by remember { mutableStateOf<ChargingRecord?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Stat Banners
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Today Revenue Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(BrandGreen900.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = BrandGreen400,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "KUDIN YAU",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                        Text(
                            text = "₦${todayRevenue.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = BrandGreen400
                        )
                    }
                }
            }

            // Active Devices Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF451A03), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Amber400,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "KAN CHAJA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400
                        )
                        Text(
                            text = "${activeCharging.size} Waya",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Amber400
                        )
                    }
                }
            }
        }

        // Locker Capacity & Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Matsayin Wuraren Ajiya (Lockers)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "$occupiedCount/$totalLockers Cike",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (occupiedCount > 15) Rose400 else BrandGreen400
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { occupancyRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (occupancyRatio > 0.8f) Rose500 else BrandGreen500,
                    trackColor = Slate800
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A Buɗe: ${totalLockers - occupiedCount} | Shirye: ${readyList.size}",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Button(
                        onClick = { viewModel.selectTab(ChajaTab.ADMIT) },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("quick_admit_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Karɓi Waya", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Lockers Visual Grid Header & Legend
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(16.dp))
                .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Taswirar Lockers (20 Boxes)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = BrandGreen500, label = "A Buɗe (Free)")
                LegendItem(color = Amber400, label = "Chaja (Charging)")
                LegendItem(color = Cyan400, label = "Ta Shirya (Ready)")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 20 Lockers Grid (4 columns)
            val lockersList = viewModel.allLockerIds
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (rowIndex in 0 until (lockersList.size / 4)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (colIndex in 0 until 4) {
                            val index = rowIndex * 4 + colIndex
                            if (index < lockersList.size) {
                                val lockerId = lockersList[index]
                                val record = records.firstOrNull { it.locker == lockerId && it.status != "Delivered" }
                                LockerBoxItem(
                                    lockerId = lockerId,
                                    record = record,
                                    modifier = Modifier.weight(1f),
                                    onEmptyClick = {
                                        viewModel.selectLockerForAdmission(lockerId)
                                    },
                                    onOccupiedClick = { phone ->
                                        selectedPhoneForQuickAction = phone
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Quick Action Bottom Sheet / Dialog for Locker
    selectedPhoneForQuickAction?.let { phone ->
        AlertDialog(
            onDismissRequest = { selectedPhoneForQuickAction = null },
            title = {
                Text(
                    text = "${phone.locker} - ${phone.brand} ${phone.model}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Kwastama: ${phone.customerName} (${phone.phoneNumber})", color = Slate300, fontSize = 13.sp)
                    Text("Matsayi: ${phone.status}", color = if (phone.status == "Ready") BrandGreen400 else Amber400, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Lokaci: ${phone.timestamp}", color = Slate400, fontSize = 12.sp)
                    Text("Kudin Chaja: ₦${phone.fee.toInt()}", color = BrandGreen400, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        viewModel.openReceipt(phone)
                        selectedPhoneForQuickAction = null
                    }) {
                        Text("Duba Slip")
                    }
                    if (phone.status == "Charging") {
                        Button(
                            onClick = {
                                viewModel.markReady(phone.id)
                                selectedPhoneForQuickAction = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen600)
                        ) {
                            Text("Mark Ready")
                        }
                    } else if (phone.status == "Ready") {
                        Button(
                            onClick = {
                                viewModel.deliverPhone(phone.id)
                                selectedPhoneForQuickAction = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                        ) {
                            Text("Miƙa Waya")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPhoneForQuickAction = null }) {
                    Text("Rufe")
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(text = label, fontSize = 11.sp, color = Slate400)
    }
}

@Composable
private fun LockerBoxItem(
    lockerId: String,
    record: ChargingRecord?,
    modifier: Modifier = Modifier,
    onEmptyClick: () -> Unit,
    onOccupiedClick: (ChargingRecord) -> Unit
) {
    val shortName = lockerId.replace("Locker ", "")
    val isFree = record == null
    val isReady = record?.status == "Ready"

    val bgColor = when {
        isFree -> Slate950
        isReady -> BrandGreen900.copy(alpha = 0.5f)
        else -> Color(0xFF451A03).copy(alpha = 0.6f)
    }

    val borderColor = when {
        isFree -> Slate800
        isReady -> BrandGreen500
        else -> Amber500
    }

    Box(
        modifier = modifier
            .height(84.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable {
                if (record != null) {
                    onOccupiedClick(record)
                } else {
                    onEmptyClick()
                }
            }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = shortName,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isFree -> Slate400
                        isReady -> BrandGreen400
                        else -> Amber400
                    }
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            when {
                                isFree -> BrandGreen500
                                isReady -> Cyan400
                                else -> Amber400
                            },
                            CircleShape
                        )
                )
            }

            if (isFree) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = Slate600,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "A Buɗe",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandGreen400
                )
            } else {
                Text(
                    text = "${record?.brand} ${record?.model}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isReady) "READY ✅" else "CHARGING...",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isReady) BrandGreen400 else Amber400
                )
            }
        }
    }
}
