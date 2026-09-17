package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun DisclaimerFooter(modifier: Modifier = Modifier) {
  var showFaq by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("disclaimer_footer"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Expandable FAQ: How it works & why routers might fool it
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .clickable { showFaq = !showFaq }
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Filled.HelpOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "কীভাবে কাজ করে এবং সীমাবদ্ধতা কী?",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        AnimatedVisibility(visible = showFaq) {
          Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
              text = "• Wi-Fi কি কারেন্ট মাপতে পারে?\nনা! এটি কোনো হার্ডওয়্যার ইলেকট্রিক্যাল মিটার নয়। এটি শুধু আশেপাশের Wi-Fi রাউটার ব্রডকাস্ট স্ক্যান করে। সাধারণ অবস্থায় বিদ্যুৎ চলে গেলে প্রতিবেশীদের রাউটার বন্ধ হয়ে যায়।",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "• IPS / UPS ব্যাকআপের প্রভাব:\nকারো বাসায় IPS, অনলাইন UPS বা মিনি ডিসি ইউপিএস থাকলে লোডশেডিংয়ের মধ্যেও রাউটার চালু থাকতে পারে। তাই ফলাফল শতভাগ নিশ্চিত নয়।",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 18.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Mandatory Disclaimer text
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.Top
    ) {
      Icon(
        imageVector = Icons.Filled.WarningAmber,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier
          .size(16.dp)
          .padding(top = 2.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = stringResource(id = R.string.disclaimer),
        style = MaterialTheme.typography.bodySmall,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        lineHeight = 16.sp
      )
    }
  }
}
