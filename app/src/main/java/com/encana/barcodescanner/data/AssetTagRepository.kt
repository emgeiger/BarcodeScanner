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
 * Repository for CRUD operations on asset_tag column in BarcodeScanning database
 * Follows official Supabase Kotlin patterns from https://github.com/supabase/supabase
 */
class AssetTagRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "AssetTagRepository"
        private const val TABLE_NAME = "asset_tags" // Table in BarcodeScanning database
    }
    
    private val deviceId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    
    /**
     * CREATE: Save a new asset tag when barcode is scanned
     * Following official Supabase insert pattern
     */
    suspend fun createAssetTag(
        scannedBarcode: String,
        assetName: String? = null,
        assetDescription: String? = null,
        location: String? = null
    ): Result<AssetTag> = withContext(Dispatchers.IO) {
        try {
            val assetTag = AssetTag(
                asset_tag = scannedBarcode,
                asset_name = assetName,
                asset_description = assetDescription,
                location = location,
                status = "active",
                scanned_at = getCurrentTimestamp(),
                device_id = deviceId,
                app_version = getAppVersion(),
                created_at = getCurrentTimestamp(),
                updated_at = getCurrentTimestamp()
            )
            
            // Insert into BarcodeScanning database following official pattern
            val response = SupabaseConfig.client
                .from(TABLE_NAME)
                .insert(assetTag)
                .decodeSingle<AssetTag>()
            
            Log.d(TAG, "Asset tag created successfully: $scannedBarcode")
            Result.success(response)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create asset tag: $scannedBarcode", e)
            Result.failure(e)
        }
    }
    
    /**
     * READ: Get all asset tags for this device
     * Following official Supabase select pattern
     */
    suspend fun getAllAssetTags(): Result<List<AssetTag>> = withContext(Dispatchers.IO) {
        try {
            val response = SupabaseConfig.client
                .from(TABLE_NAME)
                .select() {
                    filter {
                        eq("device_id", deviceId)
                    }
                    order("scanned_at", Order.DESCENDING)
                }
                .decodeList<AssetTag>()
            
            Log.d(TAG, "Retrieved ${response.size} asset tags")
            Result.success(response)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve asset tags", e)
            Result.failure(e)
        }
    }
    
    /**
     * READ: Get asset tag by barcode value
     * Following official Supabase filter pattern
     */
    suspend fun getAssetTagByBarcode(barcodeValue: String): Result<AssetTag?> = withContext(Dispatchers.IO) {
        try {
            val response = SupabaseConfig.client
                .from(TABLE_NAME)
                .select() {
                    filter {
                        eq("asset_tag", barcodeValue)
                        eq("device_id", deviceId)
                    }
                    order("scanned_at", Order.DESCENDING)
                    limit(1)
                }
                .decodeList<AssetTag>()
            
            val assetTag = response.firstOrNull()
            Log.d(TAG, "Asset tag lookup for $barcodeValue: ${if (assetTag != null) "found" else "not found"}")
            Result.success(assetTag)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get asset tag by barcode: $barcodeValue", e)
            Result.failure(e)
        }
    }
    
    /**
     * READ: Get recent asset tags with limit
     * Following official Supabase pagination pattern
     */
    suspend fun getRecentAssetTags(limit: Int = 50): Result<List<AssetTag>> = withContext(Dispatchers.IO) {
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
                .decodeList<AssetTag>()
            
            Log.d(TAG, "Retrieved ${response.size} recent asset tags")
            Result.success(response)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve recent asset tags", e)
            Result.failure(e)
        }
    }
    
    /**
     * UPDATE: Update asset tag information
     * Following official Supabase update pattern
     */
    suspend fun updateAssetTag(
        id: String,
        assetName: String? = null,
        assetDescription: String? = null,
        location: String? = null,
        status: String? = null
    ): Result<AssetTag> = withContext(Dispatchers.IO) {
        try {
            val response = SupabaseConfig.client
                .from(TABLE_NAME)
                .update({
                    assetName?.let { set("asset_name", it) }
                    assetDescription?.let { set("asset_description", it) }
                    location?.let { set("location", it) }
                    status?.let { set("status", it) }
                    set("updated_at", getCurrentTimestamp())
                }) {
                    filter {
                        eq("id", id)
                        eq("device_id", deviceId) // Security: only update own records
                    }
                }
                .decodeSingle<AssetTag>()
            
            Log.d(TAG, "Asset tag updated successfully: $id")
            Result.success(response)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update asset tag: $id", e)
            Result.failure(e)
        }
    }
    
    /**
     * DELETE: Remove asset tag
     * Following official Supabase delete pattern
     */
    suspend fun deleteAssetTag(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            SupabaseConfig.client
                .from(TABLE_NAME)
                .delete {
                    filter {
                        eq("id", id)
                        eq("device_id", deviceId) // Security: only delete own records
                    }
                }
            
            Log.d(TAG, "Asset tag deleted successfully: $id")
            Result.success(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete asset tag: $id", e)
            Result.failure(e)
        }
    }
    
    /**
     * DELETE: Remove asset tag by barcode value
     */
    suspend fun deleteAssetTagByBarcode(barcodeValue: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            SupabaseConfig.client
                .from(TABLE_NAME)
                .delete {
                    filter {
                        eq("asset_tag", barcodeValue)
                        eq("device_id", deviceId) // Security: only delete own records
                    }
                }
            
            Log.d(TAG, "Asset tag deleted by barcode: $barcodeValue")
            Result.success(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete asset tag by barcode: $barcodeValue", e)
            Result.failure(e)
        }
    }
    
    /**
     * UPSERT: Create or update asset tag (when scanning existing barcode)
     * Following official Supabase upsert pattern
     */
    suspend fun upsertAssetTag(
        scannedBarcode: String,
        assetName: String? = null,
        assetDescription: String? = null,
        location: String? = null
    ): Result<AssetTag> = withContext(Dispatchers.IO) {
        try {
            // Check if asset tag already exists
            val existingResult = getAssetTagByBarcode(scannedBarcode)
            
            if (existingResult.isSuccess && existingResult.getOrNull() != null) {
                // Update existing asset tag
                val existing = existingResult.getOrNull()!!
                return@withContext updateAssetTag(
                    id = existing.id,
                    assetName = assetName ?: existing.asset_name,
                    assetDescription = assetDescription ?: existing.asset_description,
                    location = location ?: existing.location
                )
            } else {
                // Create new asset tag
                return@withContext createAssetTag(
                    scannedBarcode = scannedBarcode,
                    assetName = assetName,
                    assetDescription = assetDescription,
                    location = location
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upsert asset tag: $scannedBarcode", e)
            Result.failure(e)
        }
    }
    
    /**
     * Test database connection and table access
     */
    suspend fun testConnection(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            SupabaseConfig.client
                .from(TABLE_NAME)
                .select() {
                    limit(1)
                }
                .decodeList<AssetTag>()
            
            Log.d(TAG, "Asset tag table connection test successful")
            Result.success(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "Asset tag table connection test failed", e)
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
