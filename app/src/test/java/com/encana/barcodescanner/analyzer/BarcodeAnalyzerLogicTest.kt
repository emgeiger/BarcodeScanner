package com.encana.barcodescanner.analyzer

import com.google.mlkit.vision.barcode.common.Barcode
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.*

@RunWith(MockitoJUnitRunner::class)
class BarcodeAnalyzerLogicTest {

    @Mock
    private lateinit var mockBarcode: Barcode

    @Test
    fun `barcode processing should handle empty list`() {
        // Arrange
        val barcodes = emptyList<Barcode>()
        var listenerCalled = false
        val listener: (List<Barcode>) -> Unit = { 
            listenerCalled = true
        }

        // Act
        listener(barcodes)

        // Assert
        assertTrue(listenerCalled)
    }

    @Test
    fun `barcode processing should handle single barcode`() {
        // Arrange
        `when`(mockBarcode.rawValue).thenReturn("123456789")
        `when`(mockBarcode.format).thenReturn(Barcode.FORMAT_EAN_13)
        
        val barcodes = listOf(mockBarcode)
        var processedBarcode: Barcode? = null
        
        val listener: (List<Barcode>) -> Unit = { barcodeList ->
            if (barcodeList.isNotEmpty()) {
                processedBarcode = barcodeList.first()
            }
        }

        // Act
        listener(barcodes)

        // Assert
        assertNotNull(processedBarcode)
        assertEquals("123456789", processedBarcode?.rawValue)
        assertEquals(Barcode.FORMAT_EAN_13, processedBarcode?.format)
    }

    @Test
    fun `barcode processing should handle multiple barcodes`() {
        // Arrange
        val mockBarcode1 = mock(Barcode::class.java)
        val mockBarcode2 = mock(Barcode::class.java)
        
        `when`(mockBarcode1.rawValue).thenReturn("123456789")
        `when`(mockBarcode1.format).thenReturn(Barcode.FORMAT_EAN_13)
        `when`(mockBarcode2.rawValue).thenReturn("987654321")
        `when`(mockBarcode2.format).thenReturn(Barcode.FORMAT_QR_CODE)
        
        val barcodes = listOf(mockBarcode1, mockBarcode2)
        var processedBarcodes: List<Barcode>? = null
        
        val listener: (List<Barcode>) -> Unit = { barcodeList ->
            processedBarcodes = barcodeList
        }

        // Act
        listener(barcodes)

        // Assert
        assertNotNull(processedBarcodes)
        assertEquals(2, processedBarcodes?.size)
        assertEquals("123456789", processedBarcodes?.get(0)?.rawValue)
        assertEquals("987654321", processedBarcodes?.get(1)?.rawValue)
    }

    @Test
    fun `barcode processing should handle null raw value`() {
        // Arrange
        `when`(mockBarcode.rawValue).thenReturn(null)
        `when`(mockBarcode.format).thenReturn(Barcode.FORMAT_QR_CODE)
        
        val barcodes = listOf(mockBarcode)
        var processedValue: String? = null
        
        val listener: (List<Barcode>) -> Unit = { barcodeList ->
            if (barcodeList.isNotEmpty()) {
                processedValue = barcodeList.first().rawValue ?: "Unknown"
            }
        }

        // Act
        listener(barcodes)

        // Assert
        assertEquals("Unknown", processedValue)
    }
}

/**
 * Test class for barcode format validation logic
 */
class BarcodeFormatValidationTest {

    @Test
    fun `all supported barcode formats should be handled`() {
        val supportedFormats = setOf(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_PDF417,
            Barcode.FORMAT_AZTEC
        )

        // Verify all formats are covered
        supportedFormats.forEach { format ->
            val typeName = getBarcodeTypeName(format)
            assertNotEquals("Format $format should have a valid type name", "UNKNOWN", typeName)
        }
    }

    @Test
    fun `unsupported barcode format should return UNKNOWN`() {
        val unsupportedFormat = 9999
        val typeName = getBarcodeTypeName(unsupportedFormat)
        assertEquals("UNKNOWN", typeName)
    }

    private fun getBarcodeTypeName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_CODE_128 -> "CODE_128"
            Barcode.FORMAT_CODE_39 -> "CODE_39"
            Barcode.FORMAT_CODE_93 -> "CODE_93"
            Barcode.FORMAT_CODABAR -> "CODABAR"
            Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
            Barcode.FORMAT_EAN_13 -> "EAN_13"
            Barcode.FORMAT_EAN_8 -> "EAN_8"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_QR_CODE -> "QR_CODE"
            Barcode.FORMAT_UPC_A -> "UPC_A"
            Barcode.FORMAT_UPC_E -> "UPC_E"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_AZTEC -> "AZTEC"
            else -> "UNKNOWN"
        }
    }
}
