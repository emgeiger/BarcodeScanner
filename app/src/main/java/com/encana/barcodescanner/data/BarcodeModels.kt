package com.encana.barcodescanner.data

import kotlinx.serialization.Serializable
import java.util.*

/**
 * Data model for asset tags stored in BarcodeScanning database
 * Follows official Supabase Kotlin patterns
 */
@Serializable
data class AssetTag(
    val id: String = UUID.randomUUID().toString(),
    val asset_tag: String, // The scanned barcode value
    val asset_name: String? = null,
    val asset_description: String? = null,
    val location: String? = null,
    val status: String = "active", // active, inactive, maintenance
    val scanned_at: String, // ISO timestamp
    val device_id: String? = null,
    val app_version: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

/**
 * Data model for barcode scans stored in Supabase
 */
@Serializable
data class BarcodeSync(
    val id: String = UUID.randomUUID().toString(),
    val barcode_text: String,
    val barcode_type: String,
    val scanned_at: String, // ISO timestamp
    val device_id: String? = null,
    val app_version: String? = null
)

/**
 * Local data model for offline storage (existing)
 */
data class BarcodeHistoryItem(
    val value: String,
    val type: String,
    val timestamp: Long,
    val id: String
)
