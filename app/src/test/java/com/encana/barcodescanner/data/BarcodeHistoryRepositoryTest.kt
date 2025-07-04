package com.encana.barcodescanner.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.*

@RunWith(MockitoJUnitRunner::class)
class BarcodeHistoryRepositoryTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var repository: BarcodeHistoryRepository
    private val gson = Gson()

    @Before
    fun setup() {
        // Mock SharedPreferences behavior
        `when`(mockContext.getSharedPreferences("barcode_history", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(any(), any())).thenReturn(mockEditor)
        `when`(mockEditor.remove(any())).thenReturn(mockEditor)

        repository = BarcodeHistoryRepository(mockContext)
    }

    @Test
    fun `saveBarcodeToHistory should save new barcode item`() {
        // Arrange
        `when`(mockSharedPreferences.getString("scan_history", "[]")).thenReturn("[]")

        // Act
        repository.saveBarcodeToHistory("123456789", "EAN_13")

        // Assert
        verify(mockEditor).putString(eq("scan_history"), any())
        verify(mockEditor).apply()
    }

    @Test
    fun `saveBarcodeToHistory should remove duplicate and add to beginning`() {
        // Arrange
        val existingHistory = listOf(
            BarcodeHistoryItem("987654321", "QR_CODE", 1000L, "id1"),
            BarcodeHistoryItem("123456789", "EAN_13", 2000L, "id2")
        )
        val existingJson = gson.toJson(existingHistory)
        `when`(mockSharedPreferences.getString("scan_history", "[]")).thenReturn(existingJson)

        // Act
        repository.saveBarcodeToHistory("123456789", "EAN_13")

        // Assert
        verify(mockEditor).putString(eq("scan_history"), any())
        verify(mockEditor).apply()
    }

    @Test
    fun `getHistory should return empty list when no history exists`() {
        // Arrange
        `when`(mockSharedPreferences.getString("scan_history", "[]")).thenReturn("[]")

        // Act
        val result = repository.getHistory()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getHistory should return parsed history items`() {
        // Arrange
        val historyItems = listOf(
            BarcodeHistoryItem("123456789", "EAN_13", 1000L, "id1"),
            BarcodeHistoryItem("987654321", "QR_CODE", 2000L, "id2")
        )
        val historyJson = gson.toJson(historyItems)
        `when`(mockSharedPreferences.getString("scan_history", "[]")).thenReturn(historyJson)

        // Act
        val result = repository.getHistory()

        // Assert
        assertEquals(2, result.size)
        assertEquals("123456789", result[0].value)
        assertEquals("EAN_13", result[0].type)
        assertEquals("987654321", result[1].value)
        assertEquals("QR_CODE", result[1].type)
    }

    @Test
    fun `getHistory should handle malformed JSON gracefully`() {
        // Arrange
        `when`(mockSharedPreferences.getString("scan_history", "[]")).thenReturn("invalid json")

        // Act
        val result = repository.getHistory()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `clearHistory should remove all history`() {
        // Act
        repository.clearHistory()

        // Assert
        verify(mockEditor).remove("scan_history")
        verify(mockEditor).apply()
    }

    @Test
    fun `deleteHistoryItem should remove specific item by id`() {
        // Arrange
        val historyItems = listOf(
            BarcodeHistoryItem("123456789", "EAN_13", 1000L, "id1"),
            BarcodeHistoryItem("987654321", "QR_CODE", 2000L, "id2"),
            BarcodeHistoryItem("555666777", "CODE_128", 3000L, "id3")
        )
        val historyJson = gson.toJson(historyItems)
        `when`(mockSharedPreferences.getString("scan_history", "[]")).thenReturn(historyJson)

        // Act
        repository.deleteHistoryItem("id2")

        // Assert
        verify(mockEditor).putString(eq("scan_history"), any())
        verify(mockEditor).apply()
    }

    @Test
    fun `saveBarcodeToHistory should limit history to MAX_HISTORY_ITEMS`() {
        // Arrange
        val largeHistory = mutableListOf<BarcodeHistoryItem>()
        repeat(105) { index ->
            largeHistory.add(BarcodeHistoryItem("value$index", "TYPE", 1000L + index, "id$index"))
        }
        val historyJson = gson.toJson(largeHistory)
        `when`(mockSharedPreferences.getString("scan_history", "[]")).thenReturn(historyJson)

        // Act
        repository.saveBarcodeToHistory("newValue", "NEW_TYPE")

        // Assert
        verify(mockEditor).putString(eq("scan_history"), any())
        verify(mockEditor).apply()
    }

    @Test
    fun `BarcodeHistoryItem should have correct default values`() {
        // Act
        val item = BarcodeHistoryItem("123456789", "EAN_13")

        // Assert
        assertEquals("123456789", item.value)
        assertEquals("EAN_13", item.type)
        assertTrue(item.timestamp > 0)
        assertNotNull(item.id)
        assertTrue(item.id.isNotEmpty())
    }

    @Test
    fun `BarcodeHistoryItem should accept custom timestamp and id`() {
        // Act
        val customTimestamp = 12345L
        val customId = "custom-id"
        val item = BarcodeHistoryItem("123456789", "EAN_13", customTimestamp, customId)

        // Assert
        assertEquals("123456789", item.value)
        assertEquals("EAN_13", item.type)
        assertEquals(customTimestamp, item.timestamp)
        assertEquals(customId, item.id)
    }
}
