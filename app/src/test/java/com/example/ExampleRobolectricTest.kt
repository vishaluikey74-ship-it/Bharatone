package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.OFFICIAL_GOVT_SOURCES
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BharatOne", appName)
  }

  @Test
  fun `verify government official sources list compliance`() {
    assertTrue(OFFICIAL_GOVT_SOURCES.isNotEmpty())
    // Ensure all government portals use genuine public domains (.gov.in or .nic.in)
    assertTrue(OFFICIAL_GOVT_SOURCES.all { it.domain.endsWith(".gov.in") || it.domain.endsWith(".nic.in") })
  }
}
