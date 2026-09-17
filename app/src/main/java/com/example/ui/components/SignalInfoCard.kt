package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrentEstimation
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PowerOffRed
import com.example.ui.theme.WarningAmber

@Composable
fun SignalInfoCard(
  estimation: CurrentEstimation,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("signal_info_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Wifi,
            contentDescription = "Wi-Fi signals",
            tint = ElectricCyan,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Wi-Fi সিগন্যাল বিশ্লেষণ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = CircleShape,
          color = if (estimation.isFreshScan) NeonGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ) {
          Text(
            text = if (estimation.isFreshScan) "✓ Fresh Scan" else "⚡ Cached",
            style = MaterialTheme.typography.labelSmall,
            color = if (estimation.isFreshScan) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Stats row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Total Count
        StatBox(
          label = "মোট নেটওয়ার্ক",
          value = "${estimation.totalCount} টি",
          highlightColor = ElectricCyan,
          modifier = Modifier.weight(1f)
        )

        // Avg Signal
        StatBox(
          label = "গড় সিগন্যাল",
          value = if (estimation.avgRssi != null) "${estimation.avgRssi} dBm" else "N/A",
          highlightColor = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Strength distribution pills
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SignalStrengthPill(
          label = "শক্তিশালী",
          count = estimation.strongCount,
          color = NeonGreen,
          modifier = Modifier.weight(1f)
        )
        SignalStrengthPill(
          label = "মাঝারি",
          count = estimation.moderateCount,
          color = WarningAmber,
          modifier = Modifier.weight(1f)
        )
        SignalStrengthPill(
          label = "দুর্বল",
          count = estimation.weakCount,
          color = PowerOffRed,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun StatBox(
  label: String,
  value: String,
  highlightColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
      .padding(horizontal = 12.dp, vertical = 10.dp)
  ) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = highlightColor
      )
    }
  }
}

@Composable
private fun SignalStrengthPill(
  label: String,
  count: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = color.copy(alpha = 0.12f),
    border = androidx.compose.foundation.BorderStroke(0.8.dp, color.copy(alpha = 0.5f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "$count",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = color
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
