package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DemoScenario
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PowerOffRed
import com.example.ui.theme.WarningAmber

@Composable
fun DemoLabSection(
  isDemoActive: Boolean,
  activeScenario: DemoScenario?,
  onSelectScenario: (DemoScenario) -> Unit,
  onExitDemo: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (isDemoActive) WarningAmber else MaterialTheme.colorScheme.outlineVariant
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag("demo_lab_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { isExpanded = !isExpanded }
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Science,
            contentDescription = "Test Lab",
            tint = if (isDemoActive) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "🧪 টেস্ট ল্যাব (DEMO MODE)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDemoActive) WarningAmber else MaterialTheme.colorScheme.onSurface
              )
              if (isDemoActive) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = WarningAmber.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningAmber,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
            }
            Text(
              text = "পিসি বা ইমুলেটরে Wi-Fi হার্ডওয়্যার না থাকলে টেস্টিংয়ের জন্য",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Icon(
          imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          Text(
            text = "নিচের যেকোনো টেস্ট সিনারিও চালিয়ে অ্যাপের ভিন্ন ভিন্ন ফলাফল ও বাংলা ডায়ালগ পরখ করুন:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DemoScenario.entries.forEach { scenario ->
              val isSelected = isDemoActive && activeScenario == scenario
              val chipColor = when (scenario) {
                DemoScenario.FULL_POWER -> NeonGreen
                DemoScenario.LOAD_SHEDDING -> PowerOffRed
                DemoScenario.BACKUP_ROUTER -> WarningAmber
                DemoScenario.SCAN_THROTTLED -> ElectricCyan
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) chipColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                  width = if (isSelected) 1.5.dp else 0.8.dp,
                  color = if (isSelected) chipColor else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { onSelectScenario(scenario) }
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = scenario.label,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.SemiBold,
                      color = if (isSelected) chipColor else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = scenario.description,
                      style = MaterialTheme.typography.labelSmall,
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  if (isSelected) {
                    Text(
                      text = "চালু",
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = chipColor
                    )
                  }
                }
              }
            }
          }

          if (isDemoActive) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
              onClick = onExitDemo,
              modifier = Modifier.fillMaxWidth(),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "রিয়েল ফিজিক্যাল স্ক্যানারে ফিরে যান 📡",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
