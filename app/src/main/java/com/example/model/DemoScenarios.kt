package com.example.model

enum class DemoScenario(val label: String, val description: String) {
  FULL_POWER(
    "বিদ্যুৎ আছে (Full Power)",
    "৬টি সক্রিয় Wi-Fi রাউটার (৩টি শক্তিশালী সিগন্যাল সহ)"
  ),
  LOAD_SHEDDING(
    "লোডশেডিং (Power Off)",
    "০টি Wi-Fi পাওয়া গেছে (আশেপাশের সব রাউটার অফলাইন)"
  ),
  BACKUP_ROUTER(
    "সন্দেহজনক (IPS/UPS Backup)",
    "মাত্র ১টি দুর্বল Wi-Fi সক্রিয় (সম্ভবত ব্যাটারি বা হটস্পট)"
  ),
  SCAN_THROTTLED(
    "স্ক্যান ব্যর্থ (Throttled/Error)",
    "অ্যান্ড্রয়েড OS থেকে স্ক্যান ব্যর্থ হওয়ার অবস্থা"
  )
}

object DemoDataGenerator {
  fun generate(scenario: DemoScenario): List<WifiNetworkInfo> {
    return when (scenario) {
      DemoScenario.FULL_POWER -> listOf(
        WifiNetworkInfo("DeshNet_5G_Home", "E4:8D:**:**:32:1A", -48, 5180, "5 GHz", SignalLevel.STRONG, 92),
        WifiNetworkInfo("Borno_Fiber_2.4G", "F0:9F:**:**:11:4B", -58, 2412, "2.4 GHz", SignalLevel.STRONG, 80),
        WifiNetworkInfo("Apon_Alloy_WiFi", "34:60:**:**:88:C2", -62, 2437, "2.4 GHz", SignalLevel.STRONG, 74),
        WifiNetworkInfo("Link3_Dhaka_AP", "C8:3A:**:**:44:90", -72, 5240, "5 GHz", SignalLevel.MODERATE, 58),
        WifiNetworkInfo("TP-Link_Shahbagh", "98:DA:**:**:FF:01", -78, 2462, "2.4 GHz", SignalLevel.MODERATE, 48),
        WifiNetworkInfo("[Hidden Router 00:1A:**:**:E2:40]", "00:1A:**:**:E2:40", -84, 2412, "2.4 GHz", SignalLevel.WEAK, 34)
      )
      DemoScenario.LOAD_SHEDDING -> emptyList()
      DemoScenario.BACKUP_ROUTER -> listOf(
        WifiNetworkInfo("Neighbor_IPS_Backup", "BC:CF:**:**:77:99", -79, 2437, "2.4 GHz", SignalLevel.WEAK, 42)
      )
      DemoScenario.SCAN_THROTTLED -> emptyList()
    }
  }
}
