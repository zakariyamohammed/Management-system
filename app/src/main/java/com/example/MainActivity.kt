package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.SearchModal
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChajaTab
import com.example.ui.viewmodel.ChajaViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ChajaMasterApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChajaMasterApp(
    viewModel: ChajaViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRecords.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val customerStats by viewModel.customerStats.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val receiptRecord by viewModel.receiptRecord.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var showSearchModal by remember { mutableStateOf(false) }

    // Live Clock
    var currentTimeStr by remember {
        mutableStateOf(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()))
    }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }

    // Toast listener
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val chargingCount = remember(allRecords) { allRecords.count { it.status == "Charging" } }
    val readyCount = remember(allRecords) { allRecords.count { it.status == "Ready" } }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BrandGreen600, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Amber300,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "CHAJA MASTER",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Pro Hub • $currentTimeStr",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = BrandGreen400
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSearchModal = true },
                        modifier = Modifier.testTag("open_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Slate900,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Slate900,
                contentColor = Slate300
            ) {
                NavigationBarItem(
                    selected = currentTab == ChajaTab.OVERVIEW,
                    onClick = { viewModel.selectTab(ChajaTab.OVERVIEW) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Taswira") },
                    label = { Text("Lockers", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrandGreen400,
                        indicatorColor = BrandGreen600,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_overview")
                )

                NavigationBarItem(
                    selected = currentTab == ChajaTab.ADMIT,
                    onClick = { viewModel.selectTab(ChajaTab.ADMIT) },
                    icon = { Icon(Icons.Default.AddCircleOutline, contentDescription = "Karɓi Waya") },
                    label = { Text("Karɓi", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrandGreen400,
                        indicatorColor = BrandGreen600,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_admit")
                )

                NavigationBarItem(
                    selected = currentTab == ChajaTab.ACTIVE,
                    onClick = { viewModel.selectTab(ChajaTab.ACTIVE) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (chargingCount + readyCount > 0) {
                                    Badge(
                                        containerColor = if (readyCount > 0) BrandGreen500 else Amber500,
                                        contentColor = Color.Black
                                    ) {
                                        Text("${chargingCount + readyCount}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "Chaja")
                        }
                    },
                    label = { Text("Chaja", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrandGreen400,
                        indicatorColor = BrandGreen600,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_active")
                )

                NavigationBarItem(
                    selected = currentTab == ChajaTab.EXPENSES,
                    onClick = { viewModel.selectTab(ChajaTab.EXPENSES) },
                    icon = { Icon(Icons.Default.LocalGasStation, contentDescription = "Fetur") },
                    label = { Text("Fetur", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrandGreen400,
                        indicatorColor = BrandGreen600,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_expenses")
                )

                NavigationBarItem(
                    selected = currentTab == ChajaTab.DIRECTORY,
                    onClick = { viewModel.selectTab(ChajaTab.DIRECTORY) },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Rahoto") },
                    label = { Text("Rahoto", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = BrandGreen400,
                        indicatorColor = BrandGreen600,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400
                    ),
                    modifier = Modifier.testTag("nav_reports")
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate950),
            label = "tab_fade"
        ) { tab ->
            when (tab) {
                ChajaTab.OVERVIEW -> OverviewScreen(
                    viewModel = viewModel,
                    records = allRecords
                )
                ChajaTab.ADMIT -> AdmitPhoneScreen(
                    viewModel = viewModel,
                    records = allRecords
                )
                ChajaTab.ACTIVE -> ActiveAndReadyScreen(
                    viewModel = viewModel,
                    records = allRecords
                )
                ChajaTab.EXPENSES -> ExpensesScreen(
                    viewModel = viewModel,
                    records = allRecords,
                    expenses = allExpenses
                )
                ChajaTab.DIRECTORY -> DirectoryAndReportsScreen(
                    viewModel = viewModel,
                    records = allRecords,
                    expenses = allExpenses,
                    customerStats = customerStats
                )
            }
        }
    }

    // Receipt Modal Dialog
    receiptRecord?.let { record ->
        ReceiptDialog(
            record = record,
            onDismiss = { viewModel.closeReceipt() }
        )
    }

    // Search Modal Dialog
    if (showSearchModal) {
        SearchModal(
            viewModel = viewModel,
            searchResults = searchResults,
            onDismiss = { showSearchModal = false }
        )
    }
}
