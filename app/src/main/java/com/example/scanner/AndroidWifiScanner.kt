package com.example.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.model.ScanError
import com.example.model.SignalLevel
import com.example.model.WifiNetworkInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class WifiScanException(val error: ScanError, message: String) : Exception(message)

class AndroidWifiScanner(private val context: Context) {

  private val wifiManager: WifiManager? =
    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

  private val locationManager: LocationManager? =
    context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

  fun hasRequiredPermissions(): Boolean {
    val fineLocation = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseLocation = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val nearbyWifi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.NEARBY_WIFI_DEVICES
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }

    return (fineLocation || coarseLocation) && nearbyWifi
  }

  fun isWifiEnabled(): Boolean {
    return wifiManager?.isWifiEnabled == true
  }

  fun isLocationEnabled(): Boolean {
    if (locationManager == null) return false
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      locationManager.isLocationEnabled
    } else {
      @Suppress("DEPRECATION")
      locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
          locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
  }

  /**
   * Performs real physical Android Wi-Fi scan using actual WifiManager APIs.
   * Returns a pair of (List<WifiNetworkInfo>, isFreshScan).
   */
  @SuppressLint("MissingPermission")
  suspend fun scan(): Result<Pair<List<WifiNetworkInfo>, Boolean>> {
    val wm = wifiManager
      ?: return Result.failure(WifiScanException(ScanError.WIFI_DISABLED, "Wi-Fi Hardware is not available"))

    if (!hasRequiredPermissions()) {
      return Result.failure(WifiScanException(ScanError.PERMISSION_MISSING, "Location/Wi-Fi permissions are required to scan nearby networks"))
    }

    if (!isWifiEnabled()) {
      return Result.failure(WifiScanException(ScanError.WIFI_DISABLED, "Wi-Fi is currently disabled on device"))
    }

    if (!isLocationEnabled()) {
      return Result.failure(WifiScanException(ScanError.LOCATION_SERVICES_OFF, "Android requires device Location to be turned ON to deliver Wi-Fi scan results"))
    }

    // Try starting a real scan via WifiManager.startScan()
    // and wait for SCAN_RESULTS_AVAILABLE_ACTION broadcast
    val scanFreshResult: Boolean? = withTimeoutOrNull(4000L) {
      suspendCancellableCoroutine { continuation ->
        val receiver = object : BroadcastReceiver() {
          override fun onReceive(c: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
              val updated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
              try {
                context.unregisterReceiver(this)
              } catch (_: Exception) {}
              if (continuation.isActive) {
                continuation.resume(updated)
              }
            }
          }
        }

        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        try {
          context.registerReceiver(receiver, filter)
        } catch (e: Exception) {
          if (continuation.isActive) continuation.resume(null)
          return@suspendCancellableCoroutine
        }

        continuation.invokeOnCancellation {
          try {
            context.unregisterReceiver(receiver)
          } catch (_: Exception) {}
        }

        @Suppress("DEPRECATION")
        val started = try {
          wm.startScan()
        } catch (e: Exception) {
          false
        }

        if (!started) {
          // Could be throttled by Android OS (max 4 times per 2 min in foreground)
          // Still, cached scanResults are available in WifiManager!
          try {
            context.unregisterReceiver(receiver)
          } catch (_: Exception) {}
          if (continuation.isActive) {
            continuation.resume(false)
          }
        }
      }
    }

    // Read real scan results from WifiManager
    val rawResults = try {
      wm.scanResults ?: emptyList()
    } catch (e: SecurityException) {
      return Result.failure(WifiScanException(ScanError.PERMISSION_MISSING, e.message ?: "Security Exception during scan"))
    } catch (e: Exception) {
      emptyList()
    }

    val isFresh = scanFreshResult == true
    val domainNetworks = rawResults.map { mapScanResult(it) }
      .sortedByDescending { it.rssi }

    return Result.success(Pair(domainNetworks, isFresh))
  }

  private fun mapScanResult(result: ScanResult): WifiNetworkInfo {
    val rawSsid = result.SSID ?: ""
    val maskedBssid = maskBssid(result.BSSID ?: "00:00:00:00:00:00")
    val displayName = if (rawSsid.isBlank()) "[Hidden Router $maskedBssid]" else rawSsid

    val frequency = result.frequency
    val band = when {
      frequency in 2400..2499 -> "2.4 GHz"
      frequency in 4900..5900 -> "5 GHz"
      frequency > 5900 -> "6 GHz"
      else -> "Wi-Fi"
    }

    val level = when {
      result.level >= -65 -> SignalLevel.STRONG
      result.level >= -80 -> SignalLevel.MODERATE
      else -> SignalLevel.WEAK
    }

    // Convert RSSI (-100 to -40 dBm) to 0-100 percentage
    val percent = ((result.level + 100) * 1.66f).toInt().coerceIn(5, 100)

    return WifiNetworkInfo(
      ssid = displayName,
      bssid = maskedBssid,
      rssi = result.level,
      frequency = frequency,
      band = band,
      level = level,
      signalPercent = percent
    )
  }

  private fun maskBssid(bssid: String): String {
    val parts = bssid.split(":")
    return if (parts.size >= 6) {
      "${parts[0]}:${parts[1]}:**:**:${parts[4]}:${parts[5]}"
    } else {
      bssid
    }
  }
}
