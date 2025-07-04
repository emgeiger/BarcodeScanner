package com.encana.barcodescanner.utils

import com.encana.barcodescanner.data.BarcodeHistoryItem
import java.util.*

/**
 * Test utilities for creating test data and common test operations
 */
object TestUtils {

    /**
     * Creates a sample BarcodeHistoryItem for testing
     */
    fun createSampleBarcodeItem(
        value: String = "123456789",
        type: String = "EAN_13",
        timestamp: Long = System.currentTimeMillis(),
        id: String = UUID.randomUUID().toString()
    ): BarcodeHistoryItem {
        return BarcodeHistoryItem(value, type, timestamp, id)
    }

    /**
     * Creates a list of sample barcode items for testing
     */
    fun createSampleBarcodeList(count: Int = 3): List<BarcodeHistoryItem> {
        return (1..count).map { index ->
            createSampleBarcodeItem(
                value = "barcode_$index",
                type = if (index % 2 == 0) "EAN_13" else "QR_CODE",
                timestamp = System.currentTimeMillis() + index
            )
        }
    }

    /**
     * Common barcode types for testing
     */
    object BarcodeTypes {
        const val EAN_13 = "EAN_13"
        const val QR_CODE = "QR_CODE"
        const val CODE_128 = "CODE_128"
        const val UPC_A = "UPC_A"
        const val PDF417 = "PDF417"
    }

    /**
     * Common barcode values for testing
     */
    object BarcodeValues {
        const val SAMPLE_EAN_13 = "1234567890123"
        const val SAMPLE_UPC_A = "123456789012"
        const val SAMPLE_QR_CODE = "https://example.com"
        const val SAMPLE_CODE_128 = "TEST123"
    }
}
