package com.example

import com.example.model.DemoDataGenerator
import com.example.model.DemoScenario
import com.example.model.PowerStatus
import com.example.scanner.CurrentEstimator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `zero networks results in PROBABLY_OFF`() {
    val result = CurrentEstimator.estimate(emptyList(), isFreshScan = true)
    assertEquals(PowerStatus.PROBABLY_OFF, result.status)
    assertEquals(0, result.totalCount)
    assertTrue(result.confidenceScore < 20)
  }

  @Test
  fun `multiple networks results in PROBABLY_ON and never reaches 100`() {
    val networks = DemoDataGenerator.generate(DemoScenario.FULL_POWER)
    val result = CurrentEstimator.estimate(networks, isFreshScan = true)
    assertEquals(PowerStatus.PROBABLY_ON, result.status)
    assertTrue("Score should be high", result.confidenceScore >= 60)
    assertTrue("Score should never be 100%", result.confidenceScore < 100)
  }

  @Test
  fun `single network results in UNCERTAIN due to battery backup possibility`() {
    val networks = DemoDataGenerator.generate(DemoScenario.BACKUP_ROUTER)
    val result = CurrentEstimator.estimate(networks, isFreshScan = true)
    assertEquals(PowerStatus.UNCERTAIN, result.status)
  }
}
