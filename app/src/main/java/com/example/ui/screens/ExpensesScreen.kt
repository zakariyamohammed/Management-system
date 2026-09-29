package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.ExpenseRecord
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChajaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    viewModel: ChajaViewModel,
    records: List<ChargingRecord>,
    expenses: List<ExpenseRecord>,
    modifier: Modifier = Modifier
) {
    val totalRevenue = remember(records) { records.sumOf { it.fee } }
    val totalExpenses = remember(expenses) { expenses.sumOf { it.amount } }
    val netProfit = totalRevenue - totalExpenses

    var category by remember { mutableStateOf("Fetur (Petrol)") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Fetur (Petrol)",
        "Servicing / Gyara",
        "Kudin Wuta (NEPA)",
        "Sauran Abubuwa"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Net Profit & Financials Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Lissafin Riba & Fitarwa (Profit & Loss)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Gross Revenue
                    FinancialMiniCard(
                        title = "KUDIN CHAJA",
                        amount = "₦${totalRevenue.toInt()}",
                        color = BrandGreen400,
                        modifier = Modifier.weight(1f)
                    )

                    // Total Expenses
                    FinancialMiniCard(
                        title = "KUDIN FETUR",
                        amount = "₦${totalExpenses.toInt()}",
                        color = Rose400,
                        modifier = Modifier.weight(1f)
                    )

                    // Net Profit
                    FinancialMiniCard(
                        title = "RIBAR GASKIYA",
                        amount = "₦${netProfit.toInt()}",
                        color = if (netProfit >= 0) BrandGreen400 else Rose400,
                        modifier = Modifier.weight(1.1f),
                        isHighlight = true
                    )
                }
            }
        }

        // Add Expense Form Card
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = Rose400)
                    Text(
                        text = "Adana Kuɗin Fetur / Gyara",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rukunin Abinda Aka Biya") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Rose400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.background(Slate800)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = Color.White) },
                                onClick = {
                                    category = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Adadin Kuɗi (₦) *") },
                    leadingIcon = { Text("₦", fontWeight = FontWeight.Bold, color = Rose400, fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Rose400,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Karin Bayani (Misali: Lita 5 ta fetur)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Rose400,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Submit Button
                Button(
                    onClick = {
                        val amountDouble = amount.toDoubleOrNull()
                        if (amountDouble == null || amountDouble <= 0) {
                            viewModel.showToast("Da fatan a saka adadin kuɗi mai kyau!")
                            return@Button
                        }
                        viewModel.addExpense(category, amountDouble, notes)
                        amount = ""
                        notes = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_expense_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Rose500)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Yi Shigarwar Kuɗi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // Expenses History
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tarihin Kuɗaɗen da Aka Kashe (${expenses.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (expenses.isEmpty()) {
                    Text(
                        text = "Babu bayanin kuɗin fetur ko gyara da aka shigar tukuna.",
                        fontSize = 12.sp,
                        color = Slate400,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        expenses.forEach { exp ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate950, RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = exp.category,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${exp.date} • ${exp.notes}",
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "₦${exp.amount.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Rose400
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteExpense(exp) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = Slate500,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialMiniCard(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Box(
        modifier = modifier
            .background(
                if (isHighlight) BrandGreen900.copy(alpha = 0.4f) else Slate950,
                RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isHighlight) BrandGreen500.copy(alpha = 0.6f) else Slate800,
                RoundedCornerShape(10.dp)
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}
