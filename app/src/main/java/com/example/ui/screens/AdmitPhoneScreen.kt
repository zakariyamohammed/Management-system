package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChargingRecord
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChajaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmitPhoneScreen(
    viewModel: ChajaViewModel,
    records: List<ChargingRecord>,
    modifier: Modifier = Modifier
) {
    val nextId = remember(records) { viewModel.getNextChargingId(records) }
    val preselected = viewModel.preselectedLocker.collectAsState().value

    val occupiedLockers = remember(records) {
        records.filter { it.status != "Delivered" }.map { it.locker }
    }

    var customerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var staffName by remember { mutableStateOf("Musa Admin") }
    var selectedBrand by remember { mutableStateOf("Tecno") }
    var model by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("Black") }
    var cableType by remember { mutableStateOf("Type-C") }
    var imei by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Normal / Good") }
    var fee by remember { mutableStateOf("200") }

    // Locker selection
    var selectedLocker by remember {
        mutableStateOf(
            preselected ?: viewModel.allLockerIds.firstOrNull { it !in occupiedLockers } ?: ""
        )
    }

    LaunchedEffect(preselected) {
        if (!preselected.isNullOrEmpty()) {
            selectedLocker = preselected
        }
    }

    val brands = listOf(
        "Tecno", "Infinix", "Samsung", "iPhone", "Itel",
        "Redmi/Xiaomi", "Oppo/Vivo", "Nokia", "Laptop/Powerbank", "Sauran Nau'i (Other)"
    )

    val cableTypes = listOf(
        "Type-C", "Micro-USB", "iPhone Lightning", "DC / Laptop Plug"
    )

    val staffList = listOf(
        "Musa Admin", "Ibrahim (Shift A)", "Sani (Shift B)"
    )

    var brandDropdownExpanded by remember { mutableStateOf(false) }
    var lockerDropdownExpanded by remember { mutableStateOf(false) }
    var staffDropdownExpanded by remember { mutableStateOf(false) }
    var cableDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Card with Ticket ID
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Form Din Karɓar Waya",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Cika bayanan waya domin fitar da Ticket & QR",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }

                Box(
                    modifier = Modifier
                        .background(BrandGreen900.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .border(1.dp, BrandGreen500.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = nextId,
                        color = BrandGreen400,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Customer Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Bayanan Kwastama (Customer Info)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen400
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Sunan Kwastama (Customer Name) *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Slate400) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cust_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen500,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Lambar Waya (Phone Number) *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate400) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cust_phone_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen500,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Staff Attendant Dropdown
                ExposedDropdownMenuBox(
                    expanded = staffDropdownExpanded,
                    onExpandedChange = { staffDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = staffName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ma'aikaci (Staff Attendant)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = staffDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreen500,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = staffDropdownExpanded,
                        onDismissRequest = { staffDropdownExpanded = false },
                        modifier = Modifier.background(Slate800)
                    ) {
                        staffList.forEach { staff ->
                            DropdownMenuItem(
                                text = { Text(staff, color = Color.White) },
                                onClick = {
                                    staffName = staff
                                    staffDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Phone Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "2. Bayanan Waya (Device Details)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen400
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Brand Dropdown
                    ExposedDropdownMenuBox(
                        expanded = brandDropdownExpanded,
                        onExpandedChange = { brandDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedBrand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Brand *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandGreen500,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = brandDropdownExpanded,
                            onDismissRequest = { brandDropdownExpanded = false },
                            modifier = Modifier.background(Slate800)
                        ) {
                            brands.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b, color = Color.White) },
                                    onClick = {
                                        selectedBrand = b
                                        brandDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Model TextField
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Nau'i (Model) *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreen500,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Color TextField
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Launi (Color)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreen500,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Cable Dropdown
                    ExposedDropdownMenuBox(
                        expanded = cableDropdownExpanded,
                        onExpandedChange = { cableDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = cableType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cable / Port") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cableDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandGreen500,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = cableDropdownExpanded,
                            onDismissRequest = { cableDropdownExpanded = false },
                            modifier = Modifier.background(Slate800)
                        ) {
                            cableTypes.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c, color = Color.White) },
                                    onClick = {
                                        cableType = c
                                        cableDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Locker Dropdown
                ExposedDropdownMenuBox(
                    expanded = lockerDropdownExpanded,
                    onExpandedChange = { lockerDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedLocker,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Locker / Wajen Ajiya *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Cyan400) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lockerDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandGreen500,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = lockerDropdownExpanded,
                        onDismissRequest = { lockerDropdownExpanded = false },
                        modifier = Modifier.background(Slate800)
                    ) {
                        viewModel.allLockerIds.forEach { lockerId ->
                            val isTaken = lockerId in occupiedLockers
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$lockerId ${if (isTaken) "❌ (TAKEN)" else "✅ (FREE)"}",
                                        color = if (isTaken) Rose400 else Color.White,
                                        fontWeight = if (isTaken) FontWeight.Normal else FontWeight.Bold
                                    )
                                },
                                enabled = !isTaken,
                                onClick = {
                                    selectedLocker = lockerId
                                    lockerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Condition Notes
                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Yanayin Waya (Condition/Notes)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen500,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // IMEI / Serial (Optional)
                OutlinedTextField(
                    value = imei,
                    onValueChange = { imei = it },
                    label = { Text("IMEI / Serial No. (Zabi ne)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen500,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        // Fee & Submit Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "3. Kudin Chaja (Fee - ₦)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen400
                )

                OutlinedTextField(
                    value = fee,
                    onValueChange = { fee = it },
                    label = { Text("Kudin Chaja (₦)") },
                    leadingIcon = { Text("₦", fontWeight = FontWeight.Bold, color = BrandGreen400, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen500,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("100", "200", "300", "500").forEach { preset ->
                        OutlinedButton(
                            onClick = { fee = preset },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (fee == preset) BrandGreen500 else Slate700
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (fee == preset) BrandGreen900.copy(alpha = 0.5f) else Color.Transparent,
                                contentColor = if (fee == preset) BrandGreen400 else Slate300
                            ),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("₦$preset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Submit Button
                Button(
                    onClick = {
                        val feeDouble = fee.toDoubleOrNull() ?: 200.0
                        if (customerName.isBlank()) {
                            viewModel.showToast("Da fatan a saka Sunan Kwastama!")
                            return@Button
                        }
                        if (phoneNumber.isBlank()) {
                            viewModel.showToast("Da fatan a saka Lambar Waya!")
                            return@Button
                        }
                        if (selectedLocker.isBlank()) {
                            viewModel.showToast("Da fatan a zaɓi Locker!")
                            return@Button
                        }
                        viewModel.admitNewPhone(
                            name = customerName,
                            phone = phoneNumber,
                            staff = staffName,
                            brand = selectedBrand,
                            model = model.ifBlank { "Smart Phone" },
                            color = color,
                            cableType = cableType,
                            imei = imei,
                            condition = condition,
                            locker = selectedLocker,
                            fee = feeDouble
                        )
                        // Reset inputs
                        customerName = ""
                        phoneNumber = ""
                        model = ""
                        imei = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admit_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen600)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Raiƙa Waya & Buga Receipt",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
