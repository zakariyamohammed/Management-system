package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChargingRecord
import com.example.ui.theme.*
import com.example.util.QRCodeGenerator

@Composable
fun ReceiptDialog(
    record: ChargingRecord,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val qrCodeBitmap = remember(record.id) {
        val qrData = "{\"id\":\"${record.id}\",\"name\":\"${record.customerName}\",\"locker\":\"${record.locker}\",\"fee\":${record.fee}}"
        QRCodeGenerator.generateQRCodeBitmap(qrData, width = 280, height = 280)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Slate900,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate800)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Receipt Slip & QR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Slate400
                        )
                    }
                }

                // White Receipt Body (Thermal Style)
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CHAJA MASTER HUB",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Center for Fast & Safe Charging",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Tel: 0800-CHAJA-MASTER",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = record.id,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Black
                            )
                            Text(
                                text = record.timestamp,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.DarkGray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Details Table
                        ReceiptItemRow("Kwastama:", record.customerName)
                        ReceiptItemRow("Lambar Waya:", record.phoneNumber)
                        ReceiptItemRow("Waya:", "${record.brand} ${record.model}")
                        ReceiptItemRow("Launi/Port:", "${record.color} (${record.cableType})")
                        ReceiptItemRow("Wajen Ajiya:", record.locker, isBold = true)
                        ReceiptItemRow("Ma'aikaci:", record.staffName)
                        ReceiptItemRow("Yanayi:", record.condition)
                        ReceiptItemRow("Matsayi:", record.status.uppercase(), isBold = true)

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KUDIN CHAJA:",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Black
                            )
                            Text(
                                text = "₦${record.fee.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF15803D)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Code Bitmap
                        if (qrCodeBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                                    .padding(6.dp)
                            ) {
                                Image(
                                    bitmap = qrCodeBitmap,
                                    contentDescription = "QR Code for ${record.id}",
                                    modifier = Modifier.size(130.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "NUNA WANNAN QR CODE YAYIN KARBA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Muna Godiya Da Siyan Chaja A Wajenmu!\nKada a bada slip din nan ga wani.",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Slate700))
                    ) {
                        Text("Rufe")
                    }

                    Button(
                        onClick = {
                            shareReceiptText(context, record)
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen600)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tura Slip")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptItemRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = Color.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

private fun shareReceiptText(context: Context, record: ChargingRecord) {
    val text = """
        ⚡ *CHAJA MASTER HUB - RECEIPT SLIP* ⚡
        ----------------------------------
        Ticket ID: ${record.id}
        Kwanan Wata: ${record.timestamp}
        Kwastama: ${record.customerName}
        Lambar Waya: ${record.phoneNumber}
        Waya: ${record.brand} ${record.model} (${record.color})
        Nau'in Cable: ${record.cableType}
        *Wajen Ajiya: ${record.locker}*
        Yanayin Waya: ${record.condition}
        Staff: ${record.staffName}
        ----------------------------------
        *KUDIN CHAJA: ₦${record.fee.toInt()}*
        Matsayin Chaja: ${record.status}
        ----------------------------------
        Nuna wannan sakon yayin daukar wayarka.
        Muna godiya da ziyara!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Receipt Slip - ${record.id}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Tura Receipt Slip"))
}
