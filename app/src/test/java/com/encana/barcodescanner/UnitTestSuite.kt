package com.encana.barcodescanner

import com.encana.barcodescanner.analyzer.BarcodeAnalyzerLogicTest
import com.encana.barcodescanner.analyzer.BarcodeFormatValidationTest
import com.encana.barcodescanner.data.BarcodeHistoryRepositoryTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite for all unit tests in the Barcode Scanner application
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    BarcodeHistoryRepositoryTest::class,
    MainActivityLogicTest::class,
    BarcodeAnalyzerLogicTest::class,
    BarcodeFormatValidationTest::class
)
class UnitTestSuite
