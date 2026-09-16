package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.data.CalculationRepository
import com.example.util.CryptoFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

enum class CalculationMode {
    DOLLARS_TO_COINS, // Enter Dollar Amount & Coin Price -> Get Coins
    COINS_TO_DOLLARS  // Enter Coin Quantity & Coin Price -> Get Dollar Cost
}

data class QuickCryptoPreset(
    val symbol: String,
    val name: String,
    val price: String,
    val iconTag: String
)

data class UiState(
    val mode: CalculationMode = CalculationMode.DOLLARS_TO_COINS,
    val coinName: String = "My Coin",
    val coinPriceInput: String = "0.1",        // User's example: 0.1
    val dollarAmountInput: String = "500",      // User's example: 500 dollars
    val coinQuantityInput: String = "5000",
    val targetPriceInput: String = "1.0",
    val formattedResult: CryptoFormatter.FormattedResult = CryptoFormatter.formatCoins(BigDecimal("5000")),
    val dollarResultFormatted: String = "$500.00",
    val potentialProfitUsd: String = "$4,500.00",
    val potentialRoiPercent: String = "+900%",
    val targetTotalValueUsd: String = "$5,000.00",
    val showScaleSheet: Boolean = false,
    val showAdMobGuide: Boolean = false,
    val snackbarMessage: String? = null
)

class CryptoCalcViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalculationRepository
    val history: StateFlow<List<CalculationEntity>>

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val quickPresets = listOf(
        QuickCryptoPreset("CUSTOM", "Custom Coin", "0.1", "⭐"),
        QuickCryptoPreset("MEME", "Micro Coin", "0.000025", "🚀"),
        QuickCryptoPreset("DOGE", "Doge Coin", "0.15", "🐕"),
        QuickCryptoPreset("SOL", "Solana", "150", "🟣"),
        QuickCryptoPreset("ETH", "Ethereum", "3500", "🔷"),
        QuickCryptoPreset("BTC", "Bitcoin", "68000", "🟡")
    )

    init {
        val dao = AppDatabase.getDatabase(application).calculationDao()
        repository = CalculationRepository(dao)
        history = repository.history.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        recalculate()
    }

    fun setMode(mode: CalculationMode) {
        _uiState.update { it.copy(mode = mode) }
        recalculate()
    }

    fun onCoinPriceChange(newPrice: String) {
        // Sanitize numbers and single decimal point
        val sanitized = sanitizeDecimal(newPrice)
        _uiState.update { it.copy(coinPriceInput = sanitized) }
        recalculate()
    }

    fun onDollarAmountChange(newDollars: String) {
        val sanitized = sanitizeDecimal(newDollars)
        _uiState.update { it.copy(dollarAmountInput = sanitized) }
        recalculate()
    }

    fun onCoinQuantityChange(newCoins: String) {
        val sanitized = sanitizeDecimal(newCoins)
        _uiState.update { it.copy(coinQuantityInput = sanitized) }
        recalculate()
    }

    fun onTargetPriceChange(newTarget: String) {
        val sanitized = sanitizeDecimal(newTarget)
        _uiState.update { it.copy(targetPriceInput = sanitized) }
        recalculateProfit()
    }

    fun onCoinNameChange(newName: String) {
        _uiState.update { it.copy(coinName = newName) }
    }

    fun addDollarPreset(amountToAdd: Double) {
        val current = _uiState.value.dollarAmountInput.toDoubleOrNull() ?: 0.0
        val updated = (current + amountToAdd).toString()
        val clean = if (updated.endsWith(".0")) updated.dropLast(2) else updated
        _uiState.update { it.copy(dollarAmountInput = clean) }
        recalculate()
    }

    fun applyPreset(preset: QuickCryptoPreset) {
        _uiState.update {
            it.copy(
                coinName = preset.name,
                coinPriceInput = preset.price
            )
        }
        recalculate()
    }

    fun setScaleSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showScaleSheet = visible) }
    }

    fun setAdMobGuideVisible(visible: Boolean) {
        _uiState.update { it.copy(showAdMobGuide = visible) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun showSnackbar(msg: String) {
        _uiState.update { it.copy(snackbarMessage = msg) }
    }

    fun saveCurrentCalculation() {
        val state = _uiState.value
        viewModelScope.launch {
            val entity = CalculationEntity(
                coinName = state.coinName,
                coinPrice = "$" + state.coinPriceInput,
                dollarAmount = "$" + state.dollarAmountInput,
                totalCoinsExact = state.formattedResult.exactString,
                totalCoinsCompact = state.formattedResult.compactString
            )
            repository.save(entity)
            showSnackbar("Calculation Saved to History!")
        }
    }

    fun deleteHistoryItem(item: CalculationEntity) {
        viewModelScope.launch {
            repository.delete(item)
            showSnackbar("History item deleted")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clear()
            showSnackbar("History cleared")
        }
    }

    private fun recalculate() {
        val state = _uiState.value
        val price = parseBigDecimal(state.coinPriceInput)
        val dollars = parseBigDecimal(state.dollarAmountInput)
        val coinsInput = parseBigDecimal(state.coinQuantityInput)

        if (state.mode == CalculationMode.DOLLARS_TO_COINS) {
            if (price > BigDecimal.ZERO && dollars > BigDecimal.ZERO) {
                val totalCoins = dollars.divide(price, 8, RoundingMode.HALF_UP)
                val formatted = CryptoFormatter.formatCoins(totalCoins)
                _uiState.update {
                    it.copy(
                        formattedResult = formatted,
                        coinQuantityInput = totalCoins.stripTrailingZeros().toPlainString()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        formattedResult = CryptoFormatter.formatCoins(BigDecimal.ZERO)
                    )
                }
            }
        } else {
            // Coins to dollars
            if (price > BigDecimal.ZERO && coinsInput > BigDecimal.ZERO) {
                val totalDollars = coinsInput.multiply(price)
                val formattedCoins = CryptoFormatter.formatCoins(coinsInput)
                _uiState.update {
                    it.copy(
                        formattedResult = formattedCoins,
                        dollarResultFormatted = CryptoFormatter.formatUsd(totalDollars),
                        dollarAmountInput = totalDollars.setScale(2, RoundingMode.HALF_UP).toPlainString()
                    )
                }
            }
        }
        recalculateProfit()
    }

    private fun recalculateProfit() {
        val state = _uiState.value
        val buyPrice = parseBigDecimal(state.coinPriceInput)
        val targetPrice = parseBigDecimal(state.targetPriceInput)
        val dollarInvested = parseBigDecimal(state.dollarAmountInput)

        if (buyPrice > BigDecimal.ZERO && targetPrice > BigDecimal.ZERO && dollarInvested > BigDecimal.ZERO) {
            val coins = dollarInvested.divide(buyPrice, 8, RoundingMode.HALF_UP)
            val futureValue = coins.multiply(targetPrice)
            val profit = futureValue.subtract(dollarInvested)
            val roi = profit.divide(dollarInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal("100"))

            val roiSign = if (roi >= BigDecimal.ZERO) "+" else ""
            _uiState.update {
                it.copy(
                    targetTotalValueUsd = CryptoFormatter.formatUsd(futureValue),
                    potentialProfitUsd = (if (profit >= BigDecimal.ZERO) "+" else "") + CryptoFormatter.formatUsd(profit),
                    potentialRoiPercent = "$roiSign${roi.setScale(1, RoundingMode.HALF_UP)}%"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    targetTotalValueUsd = "$0.00",
                    potentialProfitUsd = "$0.00",
                    potentialRoiPercent = "0%"
                )
            }
        }
    }

    private fun sanitizeDecimal(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' }
        val parts = filtered.split('.')
        return if (parts.size > 2) {
            parts[0] + "." + parts.drop(1).joinToString("")
        } else {
            filtered
        }
    }

    private fun parseBigDecimal(input: String): BigDecimal {
        return try {
            val cleaned = input.trim()
            if (cleaned.isEmpty() || cleaned == ".") BigDecimal.ZERO else BigDecimal(cleaned)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }
    }
}
