package com.encana.barcodescanner.data

import android.content.Context
import android.provider.Settings
import android.util.Log
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for syncing barcode scans with Supabase database
 */
class SupabaseRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "SupabaseRepository"
        private const val TABLE_NAME = "barcode_scans"
    }
    
    private val deviceId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    
    /**
     * Save a barcode scan to Supabase
     */
    suspend fun saveBarcodeToSupabase(
        barcodeText: String,
        barcodeType: String
    ): Result<BarcodeSync> = withContext(Dispatchers.IO) {
        try {
            // Create the barcode sync object
            val barcodeSync = BarcodeSync(
                barcode_text = barcodeText,
                barcode_type = barcodeType,
                scanned_at = getCurrentTimestamp(),
                device_id = deviceId,
                app_version = getAppVersion()
            )
            
            // Insert into Supabase
            val response = SupabaseConfig.client
                .from(TABLE_NAME)
                .insert(barcodeSync)
                .decodeSingle<BarcodeSync>()
            
            Log.d(TAG, "Barcode saved to Supabase: $barcodeText")
            Result.success(response)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save barcode to Supabase", e)
            Result.failure(e)
        }
    }
    
    /**
     * Retrieve all barcode scans from Supabase for this device
     */
    suspend fun getAllBarcodesFromSupabase(): Result<List<BarcodeSync>> = withContext(Dispatchers.IO) {
        try {
            val response = SupabaseConfig.client
                .from(TABLE_NAME)
                .select() {
                    filter {
                        eq("device_id", deviceId)
                    }
                    order("scanned_at", Order.DESCENDING)
                }
                .decodeList<BarcodeSync>()
            
            Log.d(TAG, "Retrieved ${response.size} barcodes from Supabase")
            Result.success(response)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve barcodes from Supabase", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get recent barcodes from Supabase (last 50)
     */
    suspend fun getRecentBarcodesFromSupabase(limit: Int = 50): Result<List<BarcodeSync>> = withContext(Dispatchers.IO) {
        try {
            val response = SupabaseConfig.client
                .from(TABLE_NAME)
                .select() {
                    filter {
                        eq("device_id", deviceId)
                    }
                    order("scanned_at", Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<BarcodeSync>()
            
            Log.d(TAG, "Retrieved ${response.size} recent barcodes from Supabase")
            Result.success(response)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve recent barcodes from Supabase", e)
            Result.failure(e)
        }
    }
    
    /**
     * Test Supabase connection
     */
    suspend fun testConnection(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Try to query the table to test connection
            SupabaseConfig.client
                .from(TABLE_NAME)
                .select() {
                    limit(1)
                }
                .decodeList<BarcodeSync>()
            
            Log.d(TAG, "Supabase connection test successful")
            Result.success(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "Supabase connection test failed", e)
            Result.failure(e)
        }
    }
    
    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
    
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }
}
