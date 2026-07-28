package com.jssdvv.ara.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 29)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStartup() = benchmarkRule.measureRepeated(
        packageName = "com.jssdvv.ara",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.DEFAULT,
        setupBlock = {
            device.executeShellCommand("pm grant com.jssdvv.ara android.permission.CAMERA")
        }
    ) {
        pressHome()
        device.waitForIdle()
        startActivityAndWait()
    }

    @Test
    fun warmStartup() = benchmarkRule.measureRepeated(
        packageName = "com.jssdvv.ara",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.DEFAULT,
        setupBlock = {
            device.executeShellCommand("pm grant com.jssdvv.ara android.permission.CAMERA")
        }
    ) {
        pressHome()
        device.waitForIdle()
        startActivityAndWait()
    }

    @Test
    fun hotStartup() = benchmarkRule.measureRepeated(
        packageName = "com.jssdvv.ara",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.HOT,
        compilationMode = CompilationMode.DEFAULT,
        setupBlock = {
            device.executeShellCommand("pm grant com.jssdvv.ara android.permission.CAMERA")
            Thread.sleep(500)
        }
    ) {
        pressHome()
        device.waitForIdle()
        startActivityAndWait()
    }
}