package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CurrentEstimation
import com.example.model.DemoDataGenerator
import com.example.model.DemoScenario
import com.example.model.ScanError
import com.example.model.WifiNetworkInfo
import com.example.scanner.AndroidWifiScanner
import com.example.scanner.CurrentEstimator
import com.example.scanner.WifiScanException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CurrentDetectorUiState(
  val isScanning: Boolean = false,
  val result: CurrentEstimation? = null,
  val networks: List<WifiNetworkInfo> = emptyList(),
  val error: ScanError? = null,
  val errorMessage: String? = null,
  val hasPermissions: Boolean = false,
  val isWifiEnabled: Boolean = true,
  val isLocationEnabled: Boolean = true,
  val isDemoMode: Boolean = false,
  val activeDemoScenario: DemoScenario? = null,
  val totalScansPerformed: Int = 0
)

class CurrentDetectorViewModel(application: Application) : AndroidViewModel(application) {

  private val scanner = AndroidWifiScanner(application)

  private val _uiState = MutableStateFlow(CurrentDetectorUiState())
  val uiState: StateFlow<CurrentDetectorUiState> = _uiState.asStateFlow()

  init {
    refreshSystemStatus()
  }

  fun refreshSystemStatus() {
    _uiState.update {
      it.copy(
        hasPermissions = scanner.hasRequiredPermissions(),
        isWifiEnabled = scanner.isWifiEnabled(),
        isLocationEnabled = scanner.isLocationEnabled()
      )
    }
  }

  fun checkCurrent() {
    refreshSystemStatus()
    val currentState = _uiState.value

    if (!scanner.hasRequiredPermissions()) {
      _uiState.update {
        it.copy(
          error = ScanError.PERMISSION_MISSING,
          errorMessage = "Wi-Fi স্ক্যান করতে Location ও Wi-Fi পারমিশন প্রয়োজন।"
        )
      }
      return
    }

    if (!scanner.isWifiEnabled()) {
      _uiState.update {
        it.copy(
          error = ScanError.WIFI_DISABLED,
          errorMessage = "ডিভাইসের Wi-Fi বন্ধ রয়েছে! Wi-Fi অন করে আবার চেষ্টা করুন।"
        )
      }
      return
    }

    if (!scanner.isLocationEnabled()) {
      _uiState.update {
        it.copy(
          error = ScanError.LOCATION_SERVICES_OFF,
          errorMessage = "অ্যান্ড্রয়েডে Wi-Fi স্ক্যানের জন্য ডিভাইস Location (GPS) চালু থাকা বাধ্যতামূলক।"
        )
      }
      return
    }

    viewModelScope.launch {
      _uiState.update {
        it.copy(
          isScanning = true,
          error = null,
          errorMessage = null,
          isDemoMode = false,
          activeDemoScenario = null
        )
      }

      val scanResult = scanner.scan()

      scanResult.fold(
        onSuccess = { (networks, isFresh) ->
          val estimation = CurrentEstimator.estimate(
            networks = networks,
            isFreshScan = isFresh,
            isDemo = false
          )
          _uiState.update {
            it.copy(
              isScanning = false,
              result = estimation,
              networks = networks,
              error = null,
              errorMessage = null,
              totalScansPerformed = it.totalScansPerformed + 1
            )
          }
        },
        onFailure = { throwable ->
          val scanError = (throwable as? WifiScanException)?.error ?: ScanError.SCAN_FAILED_OR_THROTTLED
          val estimation = CurrentEstimator.createScanFailedEstimation()
          _uiState.update {
            it.copy(
              isScanning = false,
              result = estimation,
              networks = emptyList(),
              error = scanError,
              errorMessage = throwable.message ?: "Wi-Fi স্ক্যান ব্যর্থ হয়েছে।"
            )
          }
        }
      )
    }
  }

  fun runDemoScenario(scenario: DemoScenario) {
    viewModelScope.launch {
      _uiState.update {
        it.copy(
          isScanning = true,
          error = null,
          errorMessage = null,
          isDemoMode = true,
          activeDemoScenario = scenario
        )
      }

      // Add a brief delay to simulate scanning animation realistically
      delay(1200L)

      if (scenario == DemoScenario.SCAN_THROTTLED) {
        val estimation = CurrentEstimator.createScanFailedEstimation().copy(isDemo = true)
        _uiState.update {
          it.copy(
            isScanning = false,
            result = estimation,
            networks = emptyList(),
            error = ScanError.SCAN_FAILED_OR_THROTTLED,
            errorMessage = "ডেমো: স্ক্যান ব্যর্থতা বা থ্রোটলিং দৃশ্যপট"
          )
        }
      } else {
        val demoNetworks = DemoDataGenerator.generate(scenario)
        val estimation = CurrentEstimator.estimate(
          networks = demoNetworks,
          isFreshScan = true,
          isDemo = true
        )
        _uiState.update {
          it.copy(
            isScanning = false,
            result = estimation,
            networks = demoNetworks,
            error = null,
            errorMessage = null
          )
        }
      }
    }
  }

  fun exitDemoMode() {
    _uiState.update {
      it.copy(
        isDemoMode = false,
        activeDemoScenario = null,
        result = null,
        networks = emptyList(),
        error = null,
        errorMessage = null
      )
    }
    checkCurrent()
  }

  fun dismissError() {
    _uiState.update { it.copy(error = null, errorMessage = null) }
  }
}
