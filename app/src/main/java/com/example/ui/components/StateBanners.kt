package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScanError
import com.example.ui.theme.PowerOffRed
import com.example.ui.theme.WarningAmber

@Composable
fun StateBanner(
  error: ScanError,
  errorMessage: String?,
  onRequestPermission: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  when (error) {
    ScanError.WIFI_DISABLED -> {
      ErrorCard(
        icon = Icons.Filled.WifiOff,
        title = "Wi-Fi বন্ধ রয়েছে!",
        description = "কারেন্ট ডিটেক্ট করতে ডিভাইসের Wi-Fi চালু থাকতে হবে যাতে আশেপাশের রাউটার স্ক্যান করা যায়।",
        actionText = "Wi-Fi Settings খুলুন",
        onAction = { openWifiSettings(context) },
        modifier = modifier.testTag("wifi_disabled_card")
      )
    }

    ScanError.PERMISSION_MISSING -> {
      ErrorCard(
        icon = Icons.Filled.Security,
        title = "পারমিশন প্রয়োজন!",
        description = "অ্যান্ড্রয়েড সিস্টেমে আশেপাশের Wi-Fi স্ক্যান করতে Location ও Wi-Fi পারমিশন প্রয়োজন। এটি কোনো ব্যক্তিগত ডেটা সংগ্রহ করে না।",
        actionText = "পারমিশন দিন",
        onAction = onRequestPermission,
        secondaryActionText = "App Settings",
        onSecondaryAction = { openAppSettings(context) },
        modifier = modifier.testTag("permission_missing_card")
      )
    }

    ScanError.LOCATION_SERVICES_OFF -> {
      ErrorCard(
        icon = Icons.Filled.LocationOff,
        title = "ডিভাইস Location বন্ধ রয়েছে!",
        description = "অ্যান্ড্রয়েড পলিসির কারণে ডিভাইসের Location (GPS) চালু না থাকলে Wi-Fi স্ক্যান রেজাল্ট ব্লক করে দেওয়া হয়। দয়া করে Location অন করুন।",
        actionText = "Location Settings খুলুন",
        onAction = { openLocationSettings(context) },
        modifier = modifier.testTag("location_off_card")
      )
    }

    ScanError.SCAN_FAILED_OR_THROTTLED -> {
      ErrorCard(
        icon = Icons.Filled.Warning,
        title = "স্ক্যান থ্রোটলিং বা সীমাবদ্ধতা",
        description = errorMessage ?: "অ্যান্ড্রয়েড সিস্টেমে ২ মিনিটে সর্বোচ্চ ৪ বার স্ক্যান করা যায়। কিছুক্ষণ অপেক্ষা করে আবার চেষ্টা করুন।",
        actionText = "ঠিক আছে",
        onAction = onDismiss,
        modifier = modifier.testTag("scan_failed_card")
      )
    }
  }
}

@Composable
private fun ErrorCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  description: String,
  actionText: String,
  onAction: () -> Unit,
  secondaryActionText: String? = null,
  onSecondaryAction: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
    ),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onErrorContainer
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onErrorContainer,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        if (secondaryActionText != null && onSecondaryAction != null) {
          OutlinedButton(
            onClick = onSecondaryAction,
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Text(secondaryActionText)
          }
        }

        Button(
          onClick = onAction,
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
          )
        ) {
          Text(actionText, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

private fun openWifiSettings(context: Context) {
  try {
    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  } catch (_: Exception) {}
}

private fun openLocationSettings(context: Context) {
  try {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  } catch (_: Exception) {}
}

private fun openAppSettings(context: Context) {
  try {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
      data = Uri.fromParts("package", context.packageName, null)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  } catch (_: Exception) {}
}
