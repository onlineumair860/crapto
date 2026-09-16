package com.example.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

data class UnitScaleInfo(
    val symbol: String,
    val nameEn: String,
    val nameUrdu: String,
    val exponent: Int,
    val zerosText: String,
    val example: String
)

object CryptoFormatter {

    // Scales from Thousands (10^3) up to Vigintillion (10^63) and beyond!
    val SCALES = listOf(
        UnitScaleInfo("K", "Thousand", "ہزار (Hazaar)", 3, "3 Zeros (000)", "1,000"),
        UnitScaleInfo("M", "Million", "دس لاکھ (10 Lakh)", 6, "6 Zeros", "1,000,000"),
        UnitScaleInfo("B", "Billion", "ایک ارب (1 Arab)", 9, "9 Zeros", "1,000,000,000"),
        UnitScaleInfo("T", "Trillion", "ایک کھرب (1 Kharab)", 12, "12 Zeros", "1,000,000,000,000"),
        UnitScaleInfo("Qa", "Quadrillion", "ایک نیل (1 Neel)", 15, "15 Zeros", "1,000,000,000,000,000"),
        UnitScaleInfo("Qi", "Quintillion", "ایک پدما (1 Padma)", 18, "18 Zeros", "10^18"),
        UnitScaleInfo("Sx", "Sextillion", "ایک سنکھ (1 Shankh)", 21, "21 Zeros", "10^21"),
        UnitScaleInfo("Sp", "Septillion", "سیپٹیلین", 24, "24 Zeros", "10^24"),
        UnitScaleInfo("Oc", "Octillion", "اوکٹیلین", 27, "27 Zeros", "10^27"),
        UnitScaleInfo("No", "Nonillion", "نونیلیئن", 30, "30 Zeros", "10^30"),
        UnitScaleInfo("Dc", "Decillion", "ڈیسیلیئن", 33, "33 Zeros", "10^33"),
        UnitScaleInfo("Ud", "Undecillion", "ان ڈیسیلیئن", 36, "36 Zeros", "10^36"),
        UnitScaleInfo("Dd", "Duodecillion", "ڈیو ڈیسیلیئن", 39, "39 Zeros", "10^39"),
        UnitScaleInfo("Td", "Tredecillion", "ٹری ڈیسیلیئن", 42, "42 Zeros", "10^42"),
        UnitScaleInfo("Qad", "Quattuordecillion", "کواٹور ڈیسیلیئن", 45, "45 Zeros", "10^45"),
        UnitScaleInfo("Qid", "Quindecillion", "کوئن ڈیسیلیئن", 48, "48 Zeros", "10^48"),
        UnitScaleInfo("Sxd", "Sexdecillion", "سیکس ڈیسیلیئن", 51, "51 Zeros", "10^51"),
        UnitScaleInfo("Spd", "Septendecillion", "سیپٹن ڈیسیلیئن", 54, "54 Zeros", "10^54"),
        UnitScaleInfo("Ocd", "Octodecillion", "اوکٹو ڈیسیلیئن", 57, "57 Zeros", "10^57"),
        UnitScaleInfo("Nod", "Novemdecillion", "نووم ڈیسیلیئن", 60, "60 Zeros", "10^60"),
        UnitScaleInfo("Vg", "Vigintillion", "ویگینٹیلیئن", 63, "63 Zeros", "10^63")
    )

    data class FormattedResult(
        val compactString: String,      // e.g. "5K", "2.5M", "500"
        val exactString: String,        // e.g. "5,000", "2,500,000.00"
        val unitScale: UnitScaleInfo?,  // null if < 1,000
        val isZeroOrNegative: Boolean = false
    )

    /**
     * Formats coin amount according to user rule:
     * - Under 1,000: exact number with no 'k' suffix.
     * - 1,000 and above: dynamic scale suffix (K, M, B, T, Qa, Qi, etc.)
     */
    fun formatCoins(amount: BigDecimal): FormattedResult {
        if (amount <= BigDecimal.ZERO) {
            return FormattedResult(
                compactString = "0",
                exactString = "0",
                unitScale = null,
                isZeroOrNegative = true
            )
        }

        val thousand = BigDecimal("1000")
        if (amount < thousand) {
            // Less than 1000 - strictly NO 'k'
            val exact = if (amount.scale() > 6) {
                amount.setScale(6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
            } else {
                amount.stripTrailingZeros().toPlainString()
            }
            return FormattedResult(
                compactString = exact,
                exactString = formatThousands(amount),
                unitScale = null
            )
        }

        // 1000 or greater: find appropriate scale from highest to lowest
        for (i in SCALES.indices.reversed()) {
            val scale = SCALES[i]
            val scaleDivisor = BigDecimal.TEN.pow(scale.exponent)
            if (amount >= scaleDivisor) {
                val scaled = amount.divide(scaleDivisor, 3, RoundingMode.HALF_UP)
                val cleanScaled = scaled.stripTrailingZeros().toPlainString()
                return FormattedResult(
                    compactString = "$cleanScaled ${scale.symbol}",
                    exactString = formatThousands(amount),
                    unitScale = scale
                )
            }
        }

        // Extremely huge (> 10^66) - scientific fallback
        return FormattedResult(
            compactString = "${amount.toEngineeringString()} Coins",
            exactString = amount.toEngineeringString(),
            unitScale = null
        )
    }

    /**
     * Format currency amount (e.g. $500.00)
     */
    fun formatUsd(amount: BigDecimal): String {
        val df = DecimalFormat("#,##0.00")
        return "$" + df.format(amount)
    }

    /**
     * Format coin price (can be small like $0.000025 or large like $65,000.00)
     */
    fun formatPrice(price: BigDecimal): String {
        if (price >= BigDecimal.ONE) {
            val df = DecimalFormat("#,##0.00")
            return "$" + df.format(price)
        } else {
            return "$" + price.stripTrailingZeros().toPlainString()
        }
    }

    /**
     * Exact comma separated integer/decimal display
     */
    fun formatThousands(amount: BigDecimal): String {
        val integerPart = amount.toBigInteger()
        val remainder = amount.subtract(BigDecimal(integerPart))
        val intFormat = DecimalFormat("#,###")
        val intStr = intFormat.format(integerPart)

        return if (remainder > BigDecimal.ZERO) {
            val decStr = remainder.setScale(4, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString()
                .removePrefix("0")
            "$intStr$decStr"
        } else {
            intStr
        }
    }
}
