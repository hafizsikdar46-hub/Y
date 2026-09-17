package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.PowerStatus
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PowerOffRed
import com.example.ui.theme.WarningAmber

@Composable
fun ElectricBoltIcon(
  powerStatus: PowerStatus?,
  isScanning: Boolean,
  size: Dp = 140.dp,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "electric_pulse")

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = if (isScanning) 0.92f else 0.98f,
    targetValue = if (isScanning) 1.14f else 1.02f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isScanning) 450 else 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val radarRing by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isScanning) 1200 else 2800, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radar_ring"
  )

  val activeColor by animateColorAsState(
    targetValue = when {
      isScanning -> ElectricYellow
      powerStatus == PowerStatus.PROBABLY_ON -> NeonGreen
      powerStatus == PowerStatus.UNCERTAIN -> WarningAmber
      powerStatus == PowerStatus.PROBABLY_OFF -> PowerOffRed
      else -> ElectricYellow
    },
    label = "bolt_color"
  )

  val glowAuraColor = activeColor.copy(alpha = if (isScanning) 0.35f else 0.2f)

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(size)
      .testTag("electric_bolt_icon")
  ) {
    // Pulse and ripple canvas
    Canvas(modifier = Modifier.matchParentSize()) {
      val center = Offset(this.size.width / 2f, this.size.height / 2f)
      val maxRadius = this.size.minDimension / 2f

      // Expanding radar wave
      if (isScanning || powerStatus == PowerStatus.PROBABLY_ON) {
        val currentRadius = maxRadius * radarRing
        val ringAlpha = ((1f - radarRing) * 0.6f).coerceIn(0f, 1f)
        drawCircle(
          color = activeColor.copy(alpha = ringAlpha),
          radius = currentRadius,
          center = center,
          style = Stroke(width = 3.dp.toPx())
        )
      }

      // Outer soft aura
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(glowAuraColor, Color.Transparent),
          center = center,
          radius = maxRadius * pulseScale
        ),
        radius = maxRadius * pulseScale,
        center = center
      )
    }

    // Inner glowing circular background
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(size * 0.72f)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              activeColor.copy(alpha = 0.28f),
              MaterialTheme.colorScheme.surfaceVariant
            )
          )
        )
    ) {
      Icon(
        imageVector = Icons.Filled.Bolt,
        contentDescription = "Electric Current Status",
        tint = activeColor,
        modifier = Modifier
          .size(size * 0.52f)
          .scale(pulseScale)
      )
    }
  }
}
