package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.ui.components.DemoLabSection
import com.example.ui.components.DisclaimerFooter
import com.example.ui.components.ElectricBoltIcon
import com.example.ui.components.NetworksListCard
import com.example.ui.components.ResultCard
import com.example.ui.components.SignalInfoCard
import com.example.ui.components.StateBanner
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.ElectricYellowDark
import com.example.ui.theme.NeonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentDetectorScreen(
  viewModel: CurrentDetectorViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val scrollState = rememberScrollState()

  // Permission Launcher
  val permissionsLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    viewModel.refreshSystemStatus()
    val allGranted = permissions.values.all { it }
    if (allGranted) {
      viewModel.checkCurrent()
    }
  }

  fun requestScanPermissions() {
    val permissions = mutableListOf(
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
    }
    permissionsLauncher.launch(permissions.toTypedArray())
  }

  // Refresh status when app comes to foreground
  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        viewModel.refreshSystemStatus()
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Bolt,
              contentDescription = null,
              tint = ElectricYellow,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = stringResource(id = R.string.app_title),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.5.sp
            )
          }
        },
        actions = {
          IconButton(
            onClick = {
              if (uiState.hasPermissions) {
                viewModel.checkCurrent()
              } else {
                requestScanPermissions()
              }
            },
            enabled = !uiState.isScanning,
            modifier = Modifier.testTag("refresh_button")
          ) {
            Icon(
              imageVector = Icons.Filled.Refresh,
              contentDescription = "Refresh Scan",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
          .verticalScroll(scrollState)
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        // Hero Lightning Section
        Spacer(modifier = Modifier.height(10.dp))
        ElectricBoltIcon(
          powerStatus = uiState.result?.status,
          isScanning = uiState.isScanning,
          size = 140.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Big Main Action Button: CHECK CURRENT ⚡ / SCAN AGAIN ⚡
        val buttonText = if (uiState.result != null) {
          stringResource(id = R.string.scan_again_button)
        } else {
          stringResource(id = R.string.check_current_button)
        }

        Button(
          onClick = {
            if (!uiState.hasPermissions) {
              requestScanPermissions()
            } else {
              viewModel.checkCurrent()
            }
          },
          enabled = !uiState.isScanning,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ElectricYellow,
            contentColor = Color(0xFF1F1600),
            disabledContainerColor = ElectricYellow.copy(alpha = 0.5f),
            disabledContentColor = Color(0xFF1F1600).copy(alpha = 0.5f)
          ),
          elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("check_current_button")
        ) {
          if (uiState.isScanning) {
            CircularProgressIndicator(
              strokeWidth = 3.dp,
              color = Color(0xFF1F1600),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "স্ক্যানিং চলছে...",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.5.sp
            )
          } else {
            Icon(
              imageVector = Icons.Filled.Bolt,
              contentDescription = null,
              tint = Color(0xFF1F1600),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = buttonText,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.5.sp
            )
          }
        }

        // Scanning Animation & Fun Status
        AnimatedVisibility(visible = uiState.isScanning) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            LinearProgressIndicator(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = ElectricYellow,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = stringResource(id = R.string.scanning_status),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }

        // Error / Permission / Location / Wi-Fi state banner
        if (uiState.error != null) {
          Spacer(modifier = Modifier.height(18.dp))
          StateBanner(
            error = uiState.error!!,
            errorMessage = uiState.errorMessage,
            onRequestPermission = { requestScanPermissions() },
            onDismiss = { viewModel.dismissError() }
          )
        }

        // Real Result Card
        if (uiState.result != null && !uiState.isScanning) {
          Spacer(modifier = Modifier.height(20.dp))
          ResultCard(estimation = uiState.result!!)

          Spacer(modifier = Modifier.height(16.dp))
          SignalInfoCard(estimation = uiState.result!!)

          Spacer(modifier = Modifier.height(16.dp))
          NetworksListCard(networks = uiState.networks)
        }

        // Initial Guidance when not scanned yet
        if (uiState.result == null && !uiState.isScanning && uiState.error == null) {
          Spacer(modifier = Modifier.height(24.dp))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Filled.WifiFind,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "কারেন্ট আছে কি না পরীক্ষা করুন!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "উপরে 'CHECK CURRENT ⚡' বাটনে চাপ দিন। আপনার চারপাশের সক্রিয় Wi-Fi সিগন্যাল স্ক্যান করে বিদ্যুৎ থাকার সম্ভাব্য পরিস্থিতি জানানো হবে।",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Demo Test Lab (Separated & Clearly Marked for Emulators)
        DemoLabSection(
          isDemoActive = uiState.isDemoMode,
          activeScenario = uiState.activeDemoScenario,
          onSelectScenario = { scenario -> viewModel.runDemoScenario(scenario) },
          onExitDemo = { viewModel.exitDemoMode() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mandatory Disclaimer & FAQ
        DisclaimerFooter()

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
