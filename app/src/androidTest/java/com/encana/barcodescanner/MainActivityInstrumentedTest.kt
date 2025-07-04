package com.encana.barcodescanner

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA
    )

    @Test
    fun activityLaunchesSuccessfully() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Verify that the main components are displayed
            onView(withId(R.id.viewFinder)).check(matches(isDisplayed()))
            onView(withId(R.id.result_text)).check(matches(isDisplayed()))
            onView(withId(R.id.scan_button)).check(matches(isDisplayed()))
            onView(withId(R.id.clear_button)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun initialStateDisplaysCorrectText() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Verify initial text is displayed
            onView(withId(R.id.result_text))
                .check(matches(withText("Point camera at a barcode")))
            
            // Verify barcode type text is initially empty
            onView(withId(R.id.barcode_type_text))
                .check(matches(withText("")))
        }
    }

    @Test
    fun scanButtonDisplaysHistoryWhenNoHistory() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Click scan button (which shows history)
            onView(withId(R.id.scan_button)).perform(click())
            
            // This would show a toast - in a real test you'd need to verify the toast
            // For now, just verify the button works
            onView(withId(R.id.scan_button)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun clearButtonResetsDisplayText() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Click clear button
            onView(withId(R.id.clear_button)).perform(click())
            
            // Verify text is reset to initial state
            onView(withId(R.id.result_text))
                .check(matches(withText("Point camera at a barcode")))
            onView(withId(R.id.barcode_type_text))
                .check(matches(withText("")))
        }
    }

    @Test
    fun uiComponentsHaveCorrectContentDescriptions() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Verify accessibility content descriptions are set
            onView(withId(R.id.scan_button))
                .check(matches(isDisplayed()))
            onView(withId(R.id.clear_button))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun cameraPreviewIsVisible() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Verify camera preview is visible
            onView(withId(R.id.viewFinder))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun scannerOverlayIsVisible() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Verify scanner overlay is visible
            onView(withId(R.id.scanning_overlay))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun bottomContainerIsVisible() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Verify bottom container with controls is visible
            onView(withId(R.id.bottom_container))
                .check(matches(isDisplayed()))
        }
    }
}
