package com.encana.barcodescanner.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Data class representing a scanned barcode entry
 */
data class BarcodeHistoryItem(
    val value: String,
    val type: String,
    val timestamp: Long = System.currentTimeMillis(),
    val id: String = UUID.randomUUID().toString()
)

/**
 * Repository class for managing barcode scan history
 */
class BarcodeHistoryRepository(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "barcode_history"
        private const val KEY_HISTORY = "scan_history"
        private const val MAX_HISTORY_ITEMS = 100
    }
    
    /**
     * Save a new barcode scan to history
     */
    fun saveBarcodeToHistory(value: String, type: String) {
        val currentHistory = getHistory().toMutableList()
        val newItem = BarcodeHistoryItem(value, type)
        
        // Remove duplicate if exists
        currentHistory.removeAll { it.value == value }
        
        // Add new item at the beginning
        currentHistory.add(0, newItem)
        
        // Keep only the most recent items
        if (currentHistory.size > MAX_HISTORY_ITEMS) {
            currentHistory.subList(MAX_HISTORY_ITEMS, currentHistory.size).clear()
        }
        
        saveHistory(currentHistory)
    }
    
    /**
     * Get all barcode scan history
     */
    fun getHistory(): List<BarcodeHistoryItem> {
        val historyJson = sharedPreferences.getString(KEY_HISTORY, "[]")
        val type = object : TypeToken<List<BarcodeHistoryItem>>() {}.type
        return gson.fromJson(historyJson, type) ?: emptyList()
    }
    
    /**
     * Clear all scan history
     */
    fun clearHistory() {
        sharedPreferences.edit().remove(KEY_HISTORY).apply()
    }
    
    /**
     * Delete a specific history item
     */
    fun deleteHistoryItem(id: String) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.id == id }
        saveHistory(currentHistory)
    }
    
    private fun saveHistory(history: List<BarcodeHistoryItem>) {
        val historyJson = gson.toJson(history)
        sharedPreferences.edit().putString(KEY_HISTORY, historyJson).apply()
    }
}
