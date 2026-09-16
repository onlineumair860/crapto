package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdMobBannerView
import com.example.ui.components.AdMobSetupDialog
import com.example.ui.components.HistorySheet
import com.example.ui.components.ScaleGuideDialog
import com.example.ui.theme.CryptoBackground
import com.example.ui.theme.CryptoCardHighlight
import com.example.ui.theme.CryptoCyan
import com.example.ui.theme.CryptoGold
import com.example.ui.theme.CryptoGreen
import com.example.ui.theme.CryptoRed
import com.example.ui.theme.CryptoSurface
import com.example.ui.theme.CryptoSurfaceBorder
import com.example.ui.theme.CryptoSurfaceBorderActive
import com.example.ui.theme.CryptoSurfaceElevated
import com.example.ui.theme.CryptoTextMuted
import com.example.ui.theme.CryptoTextPrimary
import com.example.ui.theme.CryptoTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoCalcScreen(
    viewModel: CryptoCalcViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showHistorySheet by remember { mutableStateOf(false) }
    var isProfitSimExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CryptoBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CryptoBackground
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "CRYPTO",
                            color = CryptoTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CALC",
                            color = CryptoGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CryptoGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VIP",
                                color = CryptoGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                },
                navigationIcon = {
                    // Scale Table / Info button
                    IconButton(
                        onClick = { viewModel.setScaleSheetVisible(true) },
                        modifier = Modifier.testTag("scale_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = "Scale Table (1K - Vigintillion)",
                            tint = CryptoGold
                        )
                    }
                },
                actions = {
                    // AdMob info button
                    IconButton(
                        onClick = { viewModel.setAdMobGuideVisible(true) },
                        modifier = Modifier.testTag("admob_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdsClick,
                            contentDescription = "AdMob Ads Info",
                            tint = CryptoCyan
                        )
                    }

                    // History button with count badge
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.testTag("history_button")
                    ) {
                        if (history.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = CryptoGold,
                                        contentColor = CryptoBackground
                                    ) {
                                        Text(history.size.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Calculation History",
                                    tint = CryptoTextPrimary
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = CryptoTextSecondary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Google AdMob Banner Ad anchored at bottom
            AdMobBannerView(
                onInfoClick = { viewModel.setAdMobGuideVisible(true) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {

            // Mode Selector Tabs (Dollar to Coin vs Coin to Dollar)
            TabRow(
                selectedTabIndex = if (uiState.mode == CalculationMode.DOLLARS_TO_COINS) 0 else 1,
                containerColor = CryptoSurfaceElevated,
                contentColor = CryptoGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (uiState.mode == CalculationMode.DOLLARS_TO_COINS) 0 else 1]),
                        color = CryptoGold,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CryptoSurfaceBorder, RoundedCornerShape(12.dp))
                    .testTag("mode_tab_row")
            ) {
                Tab(
                    selected = uiState.mode == CalculationMode.DOLLARS_TO_COINS,
                    onClick = { viewModel.setMode(CalculationMode.DOLLARS_TO_COINS) },
                    text = {
                        Text(
                            text = "Dollar ➔ Coins",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (uiState.mode == CalculationMode.DOLLARS_TO_COINS) CryptoGold else CryptoTextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_dollar_to_coin")
                )
                Tab(
                    selected = uiState.mode == CalculationMode.COINS_TO_DOLLARS,
                    onClick = { viewModel.setMode(CalculationMode.COINS_TO_DOLLARS) },
                    text = {
                        Text(
                            text = "Coins ➔ Dollar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (uiState.mode == CalculationMode.COINS_TO_DOLLARS) CryptoGold else CryptoTextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_coin_to_dollar")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Presets Row (Custom, MEME, DOGE, SOL, ETH, BTC)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.quickPresets) { preset ->
                    val isSelected = uiState.coinPriceInput == preset.price
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CryptoGold.copy(alpha = 0.2f) else CryptoSurfaceElevated)
                            .border(
                                1.dp,
                                if (isSelected) CryptoGold else CryptoSurfaceBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.applyPreset(preset) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(preset.iconTag, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = preset.symbol,
                                color = if (isSelected) CryptoGold else CryptoTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "($${preset.price})",
                                color = CryptoTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Input Fields Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inputs_card"),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(CryptoSurfaceBorderActive, CryptoSurfaceBorder)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Coin Price Field
                    Text(
                        text = "1 COIN KI PRICE (USD / DOLLAR RATE)",
                        color = CryptoGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.coinPriceInput,
                        onValueChange = { viewModel.onCoinPriceChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("coin_price_input"),
                        leadingIcon = {
                            Text(
                                text = "$",
                                color = CryptoGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        trailingIcon = {
                            if (uiState.coinPriceInput.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onCoinPriceChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CryptoTextMuted)
                                }
                            }
                        },
                        placeholder = { Text("e.g. 0.1", color = CryptoTextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CryptoSurfaceElevated,
                            unfocusedContainerColor = CryptoSurfaceElevated,
                            focusedBorderColor = CryptoGold,
                            unfocusedBorderColor = CryptoSurfaceBorder,
                            focusedTextColor = CryptoTextPrimary,
                            unfocusedTextColor = CryptoTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (uiState.mode == CalculationMode.DOLLARS_TO_COINS) {
                        // Dollar Amount Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KITNE DOLLAR K LENE HAIN (INVESTMENT)",
                                color = CryptoCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.dollarAmountInput,
                            onValueChange = { viewModel.onDollarAmountChange(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dollar_amount_input"),
                            leadingIcon = {
                                Text(
                                    text = "$",
                                    color = CryptoCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            },
                            trailingIcon = {
                                if (uiState.dollarAmountInput.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onDollarAmountChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CryptoTextMuted)
                                    }
                                }
                            },
                            placeholder = { Text("e.g. 500", color = CryptoTextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CryptoSurfaceElevated,
                                unfocusedContainerColor = CryptoSurfaceElevated,
                                focusedBorderColor = CryptoCyan,
                                unfocusedBorderColor = CryptoSurfaceBorder,
                                focusedTextColor = CryptoTextPrimary,
                                unfocusedTextColor = CryptoTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Dollar Increment Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(50.0, 100.0, 500.0, 1000.0, 5000.0).forEach { amount ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CryptoSurfaceElevated)
                                        .border(1.dp, CryptoSurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.addDollarPreset(amount) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+$${amount.toInt()}",
                                        color = CryptoTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    } else {
                        // Coins to Dollars Mode
                        Text(
                            text = "COIN QUANTITY (KITNE COINS HAIN)",
                            color = CryptoCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.coinQuantityInput,
                            onValueChange = { viewModel.onCoinQuantityChange(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("coin_quantity_input"),
                            trailingIcon = {
                                if (uiState.coinQuantityInput.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onCoinQuantityChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CryptoTextMuted)
                                    }
                                }
                            },
                            placeholder = { Text("e.g. 5000", color = CryptoTextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CryptoSurfaceElevated,
                                unfocusedContainerColor = CryptoSurfaceElevated,
                                focusedBorderColor = CryptoCyan,
                                unfocusedBorderColor = CryptoSurfaceBorder,
                                focusedTextColor = CryptoTextPrimary,
                                unfocusedTextColor = CryptoTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // VIP Master Result Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_card"),
                colors = CardDefaults.cardColors(containerColor = CryptoSurfaceElevated),
                shape = RoundedCornerShape(18.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(
                        listOf(
                            CryptoGold,
                            CryptoGreen.copy(alpha = 0.8f),
                            CryptoSurfaceBorder
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CryptoGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.mode == CalculationMode.DOLLARS_TO_COINS) "TOTALLY COINS JO BANEIN GY" else "TOTAL COST (USD)",
                                color = CryptoTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        // Unit Scale Indicator Badge
                        uiState.formattedResult.unitScale?.let { scale ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CryptoGold.copy(alpha = 0.15f))
                                    .clickable { viewModel.setScaleSheetVisible(true) }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Scale: ${scale.symbol} (${scale.nameUrdu})",
                                    color = CryptoGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } ?: run {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CryptoSurfaceBorder)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Scale: <1K (No K)",
                                    color = CryptoTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Prominent Highlighted Value
                    if (uiState.mode == CalculationMode.DOLLARS_TO_COINS) {
                        Text(
                            text = "${uiState.formattedResult.compactString} Coins",
                            color = CryptoGold,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("compact_coins_display")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Exact: ${uiState.formattedResult.exactString} Coins",
                            color = CryptoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("exact_coins_display")
                        )
                    } else {
                        Text(
                            text = uiState.dollarResultFormatted,
                            color = CryptoGreen,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dollar_result_display")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "For ${uiState.formattedResult.compactString} Coins",
                            color = CryptoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detail summary pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CryptoBackground)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = "Price: $${uiState.coinPriceInput.ifEmpty { "0" }}",
                                color = CryptoTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "•",
                                color = CryptoTextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Invest: $${uiState.dollarAmountInput.ifEmpty { "0" }}",
                                color = CryptoTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Copy & Save Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToCopy = "Crypto Calc Result:\nCoin Price: $${uiState.coinPriceInput}\nInvestment: $${uiState.dollarAmountInput}\nTotal Coins: ${uiState.formattedResult.compactString} (${uiState.formattedResult.exactString})"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Crypto Calculation", textToCopy))
                                viewModel.showSnackbar("Result copied to clipboard! 📋")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("copy_result_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CryptoCardHighlight),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CryptoGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Result", color = CryptoGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.saveCurrentCalculation() },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("save_result_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CryptoGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = CryptoBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Entry", color = CryptoBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profit & Future Target Simulator Card (Expandable)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .testTag("profit_simulator_card"),
                colors = CardDefaults.cardColors(containerColor = CryptoSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CryptoSurfaceBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isProfitSimExpanded = !isProfitSimExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = CryptoGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Future Target & Profit Simulator",
                                    color = CryptoTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Agar coin is price par jaye to kitna faida hoga?",
                                    color = CryptoTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isProfitSimExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CryptoTextSecondary
                        )
                    }

                    if (isProfitSimExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "TARGET COIN PRICE (FUTURE VALUE)",
                            color = CryptoTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = uiState.targetPriceInput,
                            onValueChange = { viewModel.onTargetPriceChange(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_price_input"),
                            leadingIcon = {
                                Text("$", color = CryptoGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp))
                            },
                            placeholder = { Text("e.g. 1.00", color = CryptoTextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CryptoSurfaceElevated,
                                unfocusedContainerColor = CryptoSurfaceElevated,
                                focusedBorderColor = CryptoGreen,
                                unfocusedBorderColor = CryptoSurfaceBorder,
                                focusedTextColor = CryptoTextPrimary,
                                unfocusedTextColor = CryptoTextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Target Result Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CryptoSurfaceElevated)
                                    .border(1.dp, CryptoSurfaceBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("Portfolio Value", color = CryptoTextMuted, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = uiState.targetTotalValueUsd,
                                        color = CryptoTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CryptoSurfaceElevated)
                                    .border(1.dp, CryptoGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("Net Profit & ROI", color = CryptoGreen, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${uiState.potentialProfitUsd} (${uiState.potentialRoiPercent})",
                                        color = CryptoGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Scale Guide Card Link
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setScaleSheetVisible(true) },
                colors = CardDefaults.cardColors(containerColor = CryptoSurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CryptoSurfaceBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = null,
                            tint = CryptoGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kitne Tak Add Kar Skty Hain? (Unit Chart)",
                            color = CryptoTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "1K se 10^63+",
                        color = CryptoGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialogs & Sheets
    if (uiState.showScaleSheet) {
        ScaleGuideDialog(onDismiss = { viewModel.setScaleSheetVisible(false) })
    }

    if (uiState.showAdMobGuide) {
        AdMobSetupDialog(onDismiss = { viewModel.setAdMobGuideVisible(false) })
    }

    if (showHistorySheet) {
        HistorySheet(
            history = history,
            onDismiss = { showHistorySheet = false },
            onSelectItem = { item ->
                viewModel.onCoinNameChange(item.coinName)
                viewModel.onCoinPriceChange(item.coinPrice.removePrefix("$"))
                viewModel.onDollarAmountChange(item.dollarAmount.removePrefix("$"))
                showHistorySheet = false
                viewModel.showSnackbar("Loaded calculation for ${item.coinName}")
            },
            onDeleteItem = { item ->
                viewModel.deleteHistoryItem(item)
            },
            onClearAll = {
                viewModel.clearAllHistory()
            }
        )
    }
}
