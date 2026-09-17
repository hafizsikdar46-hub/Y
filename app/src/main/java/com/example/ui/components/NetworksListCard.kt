package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SignalLevel
import com.example.model.WifiNetworkInfo
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PowerOffRed
import com.example.ui.theme.WarningAmber

@Composable
fun NetworksListCard(
  networks: List<WifiNetworkInfo>,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("networks_list_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { expanded = !expanded }
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Router,
            contentDescription = "Routers",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "শনাক্তকৃত রাউটার তালিকা (${networks.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (expanded) "সংক্ষেপ করুন" else "বিস্তারিত দেখুন",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
          Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      if (networks.isEmpty()) {
        Text(
          text = "আশেপাশে কোনো Wi-Fi নেটওয়ার্ক পাওয়া যায়নি।",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 8.dp)
        )
      } else {
        // Show brief preview when collapsed
        if (!expanded) {
          Text(
            text = "শীর্ষ ৩টি: " + networks.take(3).joinToString(", ") { it.ssid },
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
          )
        }

        AnimatedVisibility(visible = expanded) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 12.dp)
          ) {
            networks.forEachIndexed { index, network ->
              if (index > 0) {
                HorizontalDivider(
                  modifier = Modifier.padding(vertical = 8.dp),
                  color = MaterialTheme.colorScheme.surface
                )
              }
              NetworkItemRow(network)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NetworkItemRow(network: WifiNetworkInfo) {
  val levelColor = when (network.level) {
    SignalLevel.STRONG -> NeonGreen
    SignalLevel.MODERATE -> WarningAmber
    SignalLevel.WEAK -> PowerOffRed
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = network.ssid,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MaterialTheme.colorScheme.surface
        ) {
          Text(
            text = network.band,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
          text = "MAC: ${network.bssid}",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    // Signal level & progress bar
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.width(76.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${network.rssi} dBm",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = levelColor
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { network.signalPercent / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(5.dp)
          .clip(CircleShape),
        color = levelColor,
        trackColor = MaterialTheme.colorScheme.surface
      )
    }
  }
}
