package com.encana.barcodescanner

import com.encana.barcodescanner.data.BarcodeHistoryRepositoryIntegrationTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite for all instrumented tests in the Barcode Scanner application
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityInstrumentedTest::class,
    BarcodeHistoryRepositoryIntegrationTest::class
)
class InstrumentedTestSuite
