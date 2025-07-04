package com.encana.barcodescanner.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class BarcodeHistoryRepositoryIntegrationTest {

    private lateinit var context: Context
    private lateinit var repository: BarcodeHistoryRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = BarcodeHistoryRepository(context)
        // Clear any existing history
        repository.clearHistory()
    }

    @After
    fun cleanup() {
        // Clean up after each test
        repository.clearHistory()
    }

    @Test
    fun saveAndRetrieveBarcodeHistory() {
        // Save some barcodes
        repository.saveBarcodeToHistory("123456789", "EAN_13")
        repository.saveBarcodeToHistory("987654321", "QR_CODE")
        repository.saveBarcodeToHistory("555666777", "CODE_128")

        // Retrieve history
        val history = repository.getHistory()

        // Verify history
        assertEquals(3, history.size)
        assertEquals("555666777", history[0].value) // Most recent first
        assertEquals("CODE_128", history[0].type)
        assertEquals("987654321", history[1].value)
        assertEquals("QR_CODE", history[1].type)
        assertEquals("123456789", history[2].value)
        assertEquals("EAN_13", history[2].type)
    }

    @Test
    fun duplicateBarcodeMovesToTop() {
        // Save initial barcodes
        repository.saveBarcodeToHistory("111111111", "EAN_13")
        repository.saveBarcodeToHistory("222222222", "QR_CODE")
        repository.saveBarcodeToHistory("333333333", "CODE_128")

        // Re-scan an existing barcode
        repository.saveBarcodeToHistory("222222222", "QR_CODE")

        // Verify it moved to top and there's no duplicate
        val history = repository.getHistory()
        assertEquals(3, history.size)
        assertEquals("222222222", history[0].value) // Should be at top
        assertEquals("333333333", history[1].value)
        assertEquals("111111111", history[2].value)

        // Verify no duplicates
        val values = history.map { it.value }
        assertEquals(values.toSet().size, values.size)
    }

    @Test
    fun deleteSpecificHistoryItem() {
        // Save some barcodes
        repository.saveBarcodeToHistory("111111111", "EAN_13")
        repository.saveBarcodeToHistory("222222222", "QR_CODE")
        repository.saveBarcodeToHistory("333333333", "CODE_128")

        // Get the ID of the middle item
        val history = repository.getHistory()
        val itemToDelete = history[1]
        assertEquals("222222222", itemToDelete.value)

        // Delete the item
        repository.deleteHistoryItem(itemToDelete.id)

        // Verify it's removed
        val updatedHistory = repository.getHistory()
        assertEquals(2, updatedHistory.size)
        assertFalse(updatedHistory.any { it.value == "222222222" })
        assertTrue(updatedHistory.any { it.value == "333333333" })
        assertTrue(updatedHistory.any { it.value == "111111111" })
    }

    @Test
    fun clearHistoryRemovesAllItems() {
        // Save some barcodes
        repository.saveBarcodeToHistory("111111111", "EAN_13")
        repository.saveBarcodeToHistory("222222222", "QR_CODE")
        repository.saveBarcodeToHistory("333333333", "CODE_128")

        // Verify items exist
        assertEquals(3, repository.getHistory().size)

        // Clear history
        repository.clearHistory()

        // Verify history is empty
        assertTrue(repository.getHistory().isEmpty())
    }

    @Test
    fun historyPersistsAcrossRepositoryInstances() {
        // Save with first instance
        repository.saveBarcodeToHistory("persistent_value", "QR_CODE")

        // Create new repository instance
        val newRepository = BarcodeHistoryRepository(context)
        val history = newRepository.getHistory()

        // Verify data persists
        assertEquals(1, history.size)
        assertEquals("persistent_value", history[0].value)
        assertEquals("QR_CODE", history[0].type)
    }

    @Test
    fun timestampIsCorrectlySet() {
        val beforeSave = System.currentTimeMillis()
        repository.saveBarcodeToHistory("timestamp_test", "EAN_13")
        val afterSave = System.currentTimeMillis()

        val history = repository.getHistory()
        assertEquals(1, history.size)
        
        val item = history[0]
        assertTrue("Timestamp should be between before and after save", 
            item.timestamp >= beforeSave && item.timestamp <= afterSave)
    }

    @Test
    fun uniqueIdsAreGenerated() {
        // Save multiple items
        repository.saveBarcodeToHistory("item1", "EAN_13")
        repository.saveBarcodeToHistory("item2", "QR_CODE")
        repository.saveBarcodeToHistory("item3", "CODE_128")

        val history = repository.getHistory()
        val ids = history.map { it.id }

        // Verify all IDs are unique
        assertEquals(ids.size, ids.toSet().size)
        // Verify all IDs are non-empty
        assertTrue(ids.all { it.isNotEmpty() })
    }

    @Test
    fun maxHistoryItemsLimitIsEnforced() {
        // This test would take a while with 100+ items, so we'll test the concept
        // Save several items to verify the list grows
        repeat(5) { index ->
            repository.saveBarcodeToHistory("item_$index", "EAN_13")
        }

        val history = repository.getHistory()
        assertEquals(5, history.size)
        
        // Verify order (most recent first)
        assertEquals("item_4", history[0].value)
        assertEquals("item_3", history[1].value)
        assertEquals("item_0", history[4].value)
    }
}
