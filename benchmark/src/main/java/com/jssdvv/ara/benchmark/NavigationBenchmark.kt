package com.jssdvv.ara.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 29)
class NavigationBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun navigateToCalibration() = benchmarkRule.measureRepeated(
        packageName = "com.jssdvv.ara",
        metrics = listOf(FrameTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.DEFAULT,
        setupBlock = {
            device.executeShellCommand("pm grant com.jssdvv.ara android.permission.CAMERA")
        }
    ) {
        pressHome()
        startActivityAndWait()

        device.wait(Until.hasObject(By.res("MACHINES")), 2_000)
        device.findObject(By.res("MACHINES")).click()

        device.wait(Until.hasObject(By.res("navigate_to_specs")), 2_000)
        device.findObject(By.res("navigate_to_specs")).click()

        device.wait(Until.hasObject(By.res("navigate_to_activities")), 2_000)
        device.findObject(By.res("navigate_to_activities")).click()

        device.wait(Until.hasObject(By.res("navigate_to_calibration")), 2_000)
        device.findObject(By.res("navigate_to_calibration")).click()

        device.waitForIdle(10_000)
    }

    @Test
    fun navigateToSteps() = benchmarkRule.measureRepeated(
        packageName = "com.jssdvv.ara",
        metrics = listOf(FrameTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.DEFAULT,
        setupBlock = {
            device.executeShellCommand("pm grant com.jssdvv.ara android.permission.CAMERA")
        }
    ) {
        pressHome()
        startActivityAndWait()

        device.wait(Until.hasObject(By.res("MACHINES")), 2_000)
        device.findObject(By.res("MACHINES")).click()

        device.wait(Until.hasObject(By.res("navigate_to_specs")), 2_000)
        device.findObject(By.res("navigate_to_specs")).click()

        device.wait(Until.hasObject(By.res("navigate_to_activities")), 2_000)
        device.findObject(By.res("navigate_to_activities")).click()

        device.wait(Until.hasObject(By.res("navigate_to_steps")), 2_000)
        device.findObject(By.res("navigate_to_steps")).click()

        device.waitForIdle(5_000)
    }

    @Test
    fun navigateToARSession() = benchmarkRule.measureRepeated(
        packageName = "com.jssdvv.ara",
        metrics = listOf(FrameTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.DEFAULT,
        setupBlock = {
            device.executeShellCommand("pm grant com.jssdvv.ara android.permission.CAMERA")
        }
    ) {
        pressHome()
        startActivityAndWait()

        device.wait(Until.hasObject(By.res("MACHINES")), 2_000)
        device.findObject(By.res("MACHINES")).click()

        device.wait(Until.hasObject(By.res("navigate_to_specs")), 2_000)
        device.findObject(By.res("navigate_to_specs")).click()

        device.wait(Until.hasObject(By.res("navigate_to_activities")), 2_000)
        device.findObject(By.res("navigate_to_activities")).click()

        device.wait(Until.hasObject(By.res("navigate_to_ar_session")), 2_000)
        device.findObject(By.res("navigate_to_ar_session")).click()

        device.waitForIdle(10_000)
    }
}