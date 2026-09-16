package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CryptoCalcScreen
import com.example.ui.CryptoCalcViewModel
import com.example.ui.theme.CryptoCalcTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Google Mobile Ads SDK for AdMob monetization
    try {
      MobileAds.initialize(this) {}
    } catch (e: Exception) {
      e.printStackTrace()
    }

    setContent {
      CryptoCalcTheme {
        val viewModel: CryptoCalcViewModel = viewModel()
        CryptoCalcScreen(viewModel = viewModel)
      }
    }
  }
}

