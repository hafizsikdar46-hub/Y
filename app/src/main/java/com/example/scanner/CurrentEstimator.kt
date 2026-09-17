package com.example.scanner

import com.example.model.CurrentEstimation
import com.example.model.PowerStatus
import com.example.model.SignalLevel
import com.example.model.WifiNetworkInfo
import kotlin.math.min

object CurrentEstimator {

  private val ON_MESSAGES = listOf(
    Pair("🟢 কারেন্ট আছে ভাই! ⚡😂", "Wi-Fi-গুলো বেঁচে আছে, আশা করা যায় বিদ্যুৎও আছে!"),
    Pair("🟢 পুরা পাড়া আলোকিত! 💡🔥", "চারপাশের সব রাউটার তাগড়া সিগন্যাল দিচ্ছে! ফ্যান ফুল স্পিডে চালান! 🌪️"),
    Pair("🟢 বিদ্যুৎ বহাল তবিয়তে! ⚡🙌", "Wi-Fi জিন্দা আছে, ফোন-ল্যাপটপ সব দ্রুত চার্জে লাগায়া দেন! 🔌"),
    Pair("🟢 লোডশেডিংয়ের ভয় নাই! ⚡😎", "এলাকার রাউটারগুলো সগৌরবে সম্প্রচার চালাচ্ছে, পাওয়ার সম্ভবত অন! ⚡")
  )

  private val OFF_MESSAGES = listOf(
    Pair("🔴 কারেন্ট নাই মনে হচ্ছে! 😭", "Wi-Fi-ও চুপচাপ... ফ্যানের সাথে সম্পর্ক শেষ 💀"),
    Pair("🔴 ঘুটঘুটে অন্ধকার ভাই! 🕯️", "কোনো রাউটার সাড়া দিচ্ছে না, হাতপাখা খোঁজার টাইম এসে গেছে! 🪭"),
    Pair("🔴 লোডশেডিং কনফার্ম প্রায়! 💀", "জিরো Wi-Fi সিগন্যাল! পাশের বাড়ির ভাবিরও ফ্যান বন্ধ মনে হয়! 😭"),
    Pair("🔴 কারেন্ট হাওয়া হয়ে গেছে! 💨", "চারপাশে সব রাউটার অফলাইন! মোমবাতি জ্বালিয়ে বসে থাকুন 🕯️")
  )

  private val UNCERTAIN_MESSAGES = listOf(
    Pair("🟡 ব্যাপারটা সন্দেহজনক! 🤨", "নিজে গিয়ে সুইচ দেখে আসাই safest 😂"),
    Pair("🟡 ইউপিএস নাকি আসল কারেন্ট? 🔋🤔", "মাত্র দু-একটা সিগন্যাল... কারো হয়তো IPS বা ব্যাকআপ ব্যাটারি চালু!"),
    Pair("🟡 সিগন্যাল একটু ধোঁয়াশা! 🌫️", "রাউটার সংখ্যা কম বা সিগন্যাল দুর্বল, নিশ্চিত হওয়া যাচ্ছে না!"),
    Pair("🟡 দোলাচলে আছি ভাই! 🤷‍♂️", "কিছু রাউটার জেগে আছে, কিছু ঘুমন্ত। গিয়ে বাল্বের সুইচ চেক করুন!")
  )

  fun estimate(
    networks: List<WifiNetworkInfo>,
    isFreshScan: Boolean,
    isDemo: Boolean = false,
    variationSeed: Long = System.currentTimeMillis()
  ): CurrentEstimation {
    val totalCount = networks.size
    val strongCount = networks.count { it.level == SignalLevel.STRONG }
    val moderateCount = networks.count { it.level == SignalLevel.MODERATE }
    val weakCount = networks.count { it.level == SignalLevel.WEAK }
    val avgRssi = if (networks.isNotEmpty()) networks.map { it.rssi }.average().toInt() else null

    val (status, score, explanation) = when {
      totalCount == 0 -> {
        Triple(
          PowerStatus.PROBABLY_OFF,
          8, // 8% power probability -> 92% off
          "আশেপাশে কোনো Wi-Fi নেটওয়ার্ক পাওয়া যায়নি (০ টি)। সাধারণত সব রাউটার একসাথে অফলাইন হওয়া বিদ্যুৎ বিভ্রাটের প্রধান লক্ষণ।"
        )
      }
      totalCount == 1 -> {
        val score = if (strongCount >= 1) 45 else 25
        Triple(
          PowerStatus.UNCERTAIN,
          score,
          "মাত্র ১টি Wi-Fi পাওয়া গেছে (${networks.first().ssid.ifBlank { "লুকানো রাউটার" }}). হতে পারে প্রতিবেশীর রাউটারে IPS/UPS বা পাওয়ারব্যাংক ব্যাকআপ আছে।"
        )
      }
      totalCount == 2 -> {
        val score = if (strongCount >= 1) 55 else 38
        Triple(
          PowerStatus.UNCERTAIN,
          score,
          "মাত্র ২টি Wi-Fi পাওয়া গেছে। বিদ্যুৎ চালু থাকতেও পারে, আবার ব্যাকআপ/পকেট রাউটার হওয়ার সম্ভাবনাও উড়িয়ে দেওয়া যায় না।"
        )
      }
      else -> {
        // totalCount >= 3
        val rawBonus = (totalCount - 3) * 3 + (strongCount * 5) + (moderateCount * 2)
        val score = min(92, 65 + rawBonus) // Never 100% per instructions!
        Triple(
          PowerStatus.PROBABLY_ON,
          score,
          "$totalCount টি স্বতন্ত্র Wi-Fi সক্রিয় পাওয়া গেছে (শক্তিশালী: $strongCount টি, মাঝারি: $moderateCount টি)। একসাথে এতগুলো রাউটার ব্যাকআপে চলার সম্ভাবনা কম, তাই বিদ্যুৎ চালু থাকার প্রবল সম্ভাবনা।"
        )
      }
    }

    val seedIndex = (variationSeed.hashCode() and 0x7FFFFFFF)
    val (headline, subtext) = when (status) {
      PowerStatus.PROBABLY_ON -> ON_MESSAGES[seedIndex % ON_MESSAGES.size]
      PowerStatus.UNCERTAIN -> UNCERTAIN_MESSAGES[seedIndex % UNCERTAIN_MESSAGES.size]
      PowerStatus.PROBABLY_OFF -> OFF_MESSAGES[seedIndex % OFF_MESSAGES.size]
    }

    return CurrentEstimation(
      status = status,
      confidenceScore = score,
      banglaHeadline = headline,
      banglishSubtext = subtext,
      totalCount = totalCount,
      strongCount = strongCount,
      moderateCount = moderateCount,
      weakCount = weakCount,
      avgRssi = avgRssi,
      explanation = explanation,
      isFreshScan = isFreshScan,
      isDemo = isDemo
    )
  }

  fun createScanFailedEstimation(): CurrentEstimation {
    return CurrentEstimation(
      status = PowerStatus.UNCERTAIN,
      confidenceScore = 50,
      banglaHeadline = "🟡 অ্যান্ড্রয়েড স্ক্যান করতে পারল না! ⚠️",
      banglishSubtext = "OS থেকে নতুন স্ক্যান রেজাল্ট পাওয়া যায়নি। একটু পরে আবার ট্রাই করুন!",
      totalCount = 0,
      strongCount = 0,
      moderateCount = 0,
      weakCount = 0,
      avgRssi = null,
      explanation = "অ্যান্ড্রয়েড সিস্টেমে Wi-Fi স্ক্যান সীমাবদ্ধতা (Throttling) বা অনুমতি সমস্যার কারণে নতুন ডেটা পাওয়া যায়নি। কোনো আন্দাজে রেজাল্ট দেওয়া হয়নি।",
      isFreshScan = false,
      isDemo = false
    )
  }
}
