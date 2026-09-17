package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CurrentEstimation
import com.example.model.PowerStatus
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PowerOffRed
import com.example.ui.theme.WarningAmber

@Composable
fun ResultCard(
  estimation: CurrentEstimation,
  modifier: Modifier = Modifier
) {
  val statusColor = when (estimation.status) {
    PowerStatus.PROBABLY_ON -> NeonGreen
    PowerStatus.UNCERTAIN -> WarningAmber
    PowerStatus.PROBABLY_OFF -> PowerOffRed
  }

  val badgeBackground = statusColor.copy(alpha = 0.18f)

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
      .fillMaxWidth()
      .border(
        width = 1.5.dp,
        color = statusColor.copy(alpha = 0.6f),
        shape = RoundedCornerShape(20.dp)
      )
      .testTag("result_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (estimation.isDemo) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.errorContainer,
          modifier = Modifier.padding(bottom = 12.dp)
        ) {
          Text(
            text = "🧪 DEMO SIMULATION (নমুনা ফলাফল)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      // Status Badge
      Surface(
        shape = CircleShape,
        color = badgeBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor),
        modifier = Modifier.testTag("status_badge")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(statusColor)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = estimation.status.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = statusColor,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Bengali Headline
      Text(
        text = estimation.banglaHeadline,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 28.sp,
        modifier = Modifier.testTag("bangla_headline")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Banglish Subtext
      Text(
        text = estimation.banglishSubtext,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 20.sp,
        modifier = Modifier.testTag("banglish_subtext")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Transparent Confidence Probability Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "বিদ্যুৎ থাকার আনুমানিক সম্ভাবনা:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "~${estimation.confidenceScore}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = statusColor
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { (estimation.confidenceScore / 100f).coerceIn(0.05f, 0.95f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
          color = statusColor,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Heuristic explanation
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(statusColor.copy(alpha = 0.08f))
          .padding(10.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(
          imageVector = Icons.Filled.Info,
          contentDescription = "Analysis",
          tint = statusColor,
          modifier = Modifier
            .size(18.dp)
            .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = estimation.explanation,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          lineHeight = 18.sp
        )
      }
    }
  }
}
