package com.example.model

enum class SignalLevel(val label: String) {
  STRONG("Strong"),
  MODERATE("Moderate"),
  WEAK("Weak")
}

data class WifiNetworkInfo(
  val ssid: String,
  val bssid: String,
  val rssi: Int,
  val frequency: Int,
  val band: String,
  val level: SignalLevel,
  val signalPercent: Int
)

enum class PowerStatus(val displayName: String) {
  PROBABLY_ON("PROBABLY ON"),
  UNCERTAIN("UNCERTAIN"),
  PROBABLY_OFF("PROBABLY OFF")
}

data class CurrentEstimation(
  val status: PowerStatus,
  val confidenceScore: Int, // 0 - 95% (Never 100% per instructions)
  val banglaHeadline: String,
  val banglishSubtext: String,
  val totalCount: Int,
  val strongCount: Int,
  val moderateCount: Int,
  val weakCount: Int,
  val avgRssi: Int?,
  val explanation: String,
  val isFreshScan: Boolean,
  val timestamp: Long = System.currentTimeMillis(),
  val isDemo: Boolean = false
)

enum class ScanError {
  WIFI_DISABLED,
  PERMISSION_MISSING,
  LOCATION_SERVICES_OFF,
  SCAN_FAILED_OR_THROTTLED
}
