package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChajaViewModel
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActiveAndReadyScreen(
    viewModel: ChajaViewModel,
    records: List<ChargingRecord>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0 = Charging, 1 = Ready

    val chargingList = remember(records) { records.filter { it.status == "Charging" } }
    val readyList = remember(records) { records.filter { it.status == "Ready" } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Segmented Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(12.dp))
                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { selectedSubTab = 0 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_charging_now"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSubTab == 0) Amber500 else Color.Transparent,
                    contentColor = if (selectedSubTab == 0) Color.Black else Slate400
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Charging Now (${chargingList.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Button(
                onClick = { selectedSubTab = 1 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_ready_pickup"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedSubTab == 1) BrandGreen600 else Color.Transparent,
                    contentColor = if (selectedSubTab == 1) Color.White else Slate400
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Suka Shirya (${readyList.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (selectedSubTab == 0) {
            // Charging Now List
            if (chargingList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "Babu waya da ke kan chaja yanzu",
                    subtitle = "Danna 'Karɓi Waya' domin karɓar sabuwar waya a wajen ajiya."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chargingList, key = { it.id }) { phone ->
                        ChargingCardItem(
                            phone = phone,
                            onViewSlip = { viewModel.openReceipt(phone) },
                            onMarkReady = { viewModel.markReady(phone.id) }
                        )
                    }
                }
            }
        } else {
            // Ready List
            if (readyList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.CheckCircleOutline,
                    title = "Babu wayar da ke jiran mai ita a yanzu",
                    subtitle = "Duk wayoyin da aka karɓa an miƙa su ko kuma suna kan chaja."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(readyList, key = { it.id }) { phone ->
                        ReadyCardItem(
                            phone = phone,
                            onWhatsApp = {
                                sendWhatsAppNotice(context, phone)
                            },
                            onDeliver = { viewModel.deliverPhone(phone.id) },
                            onViewSlip = { viewModel.openReceipt(phone) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChargingCardItem(
    phone: ChargingRecord,
    onViewSlip: () -> Unit,
    onMarkReady: () -> Unit
) {
    val elapsed = calculateTimeElapsed(phone.timestamp)
    val isOverdue = elapsed.second >= 6 // > 6 hours overdue

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOverdue) Rose500.copy(alpha = 0.8f) else Slate800
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = phone.id,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Amber400,
                        modifier = Modifier
                            .background(Color(0xFF451A03), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Text(
                        text = phone.locker,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .background(Slate800, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isOverdue) {
                        Text(
                            text = "Overdue ⚠️",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Rose400,
                            modifier = Modifier
                                .background(Rose500.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "⏱ ${elapsed.first}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Amber400
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Phone Title
            Text(
                text = "${phone.brand} ${phone.model}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "${phone.color} • Port: ${phone.cableType} • ${phone.condition}",
                fontSize = 12.sp,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Customer Info Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = phone.customerName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = phone.phoneNumber,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
                Text(
                    text = "₦${phone.fee.toInt()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = BrandGreen400
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewSlip,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                ) {
                    Text("Duba Slip", fontSize = 12.sp)
                }

                Button(
                    onClick = onMarkReady,
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen600)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark Ready (Ta Cika)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReadyCardItem(
    phone: ChargingRecord,
    onWhatsApp: () -> Unit,
    onDeliver: () -> Unit,
    onViewSlip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen500.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = phone.id,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandGreen400,
                        modifier = Modifier
                            .background(BrandGreen900.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Text(
                        text = phone.locker,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .background(Slate800, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "READY ✅",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = BrandGreen400,
                    modifier = Modifier
                        .background(BrandGreen900, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${phone.brand} ${phone.model}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Kwastama: ${phone.customerName} (${phone.phoneNumber})",
                fontSize = 12.sp,
                color = Slate300
            )

            Spacer(modifier = Modifier.height(10.dp))

            // WhatsApp Quick Alert Button
            Button(
                onClick = onWhatsApp,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Aiko Saƙon WhatsApp (100% Caji)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewSlip,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                ) {
                    Text("Duba Slip", fontSize = 12.sp)
                }

                Button(
                    onClick = onDeliver,
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan500)
                ) {
                    Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Miƙa Waya (₦${phone.fee.toInt()})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Slate600,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Slate300
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Slate400,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

private fun calculateTimeElapsed(timestampStr: String): Pair<String, Long> {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = format.parse(timestampStr) ?: return Pair("0m", 0L)
        val diffMs = Date().time - date.time
        val hours = diffMs / (1000 * 60 * 60)
        val minutes = (diffMs % (1000 * 60 * 60)) / (1000 * 60)
        Pair("${hours}h ${minutes}m", hours)
    } catch (e: Exception) {
        Pair("0m", 0L)
    }
}

private fun sendWhatsAppNotice(context: Context, phone: ChargingRecord) {
    try {
        var cleanPhone = phone.phoneNumber.replace("[^0-9]".toRegex(), "")
        if (cleanPhone.startsWith("0")) {
            cleanPhone = "234" + cleanPhone.substring(1)
        }
        val message = "Ina kwana ${phone.customerName}! Wayarka (${phone.brand} ${phone.model}) da ke a ${phone.locker} ta cika caji 100% tana jiran karɓa. Kuɗin chaja shine ₦${phone.fee.toInt()}. Muna godiya! - CHAJA MASTER HUB"
        val encodedMsg = URLEncoder.encode(message, "UTF-8")
        val uri = Uri.parse("https://wa.me/$cleanPhone?text=$encodedMsg")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
