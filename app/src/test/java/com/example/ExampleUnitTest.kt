package com.example

import com.example.util.CryptoFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class ExampleUnitTest {

  @Test
  fun userExample_fiveHundredDollarsAtZeroPointOne_yieldsFiveThousandCoins() {
    // User's example: 500 dollars / 0.1 per coin = 5,000 coins -> should format as 5 K
    val dollar = BigDecimal("500")
    val price = BigDecimal("0.1")
    val totalCoins = dollar.divide(price)

    val result = CryptoFormatter.formatCoins(totalCoins)
    assertEquals("5 K", result.compactString)
    assertEquals("5,000", result.exactString)
    assertNotNull(result.unitScale)
    assertEquals("K", result.unitScale?.symbol)
  }

  @Test
  fun numbersUnderOneThousand_doNotHaveKSuffix() {
    // Under 1000: strictly NO 'k'
    val result500 = CryptoFormatter.formatCoins(BigDecimal("500"))
    assertEquals("500", result500.compactString)
    assertNull(result500.unitScale)

    val result999 = CryptoFormatter.formatCoins(BigDecimal("999.5"))
    assertEquals("999.5", result999.compactString)
    assertNull(result999.unitScale)

    val result01 = CryptoFormatter.formatCoins(BigDecimal("0.45"))
    assertEquals("0.45", result01.compactString)
    assertNull(result01.unitScale)
  }

  @Test
  fun largeScaleFormatting_millionsBillionsTrillionsQuadrillions() {
    // 10^6: Million (M)
    val million = CryptoFormatter.formatCoins(BigDecimal("2500000"))
    assertEquals("2.5 M", million.compactString)
    assertEquals("M", million.unitScale?.symbol)

    // 10^9: Billion (B)
    val billion = CryptoFormatter.formatCoins(BigDecimal("10000000000"))
    assertEquals("10 B", billion.compactString)
    assertEquals("B", billion.unitScale?.symbol)

    // 10^12: Trillion (T)
    val trillion = CryptoFormatter.formatCoins(BigDecimal("5000000000000"))
    assertEquals("5 T", trillion.compactString)
    assertEquals("T", trillion.unitScale?.symbol)

    // 10^15: Quadrillion (Qa)
    val quadrillion = CryptoFormatter.formatCoins(BigDecimal("1000000000000000"))
    assertEquals("1 Qa", quadrillion.compactString)
    assertEquals("Qa", quadrillion.unitScale?.symbol)
  }
}

