# Asset Tag CRUD Operations Guide

## Overview

This document describes the implementation of CRUD (Create, Read, Update, Delete) operations for writing barcode scan data to the `asset_tag` column in the BarcodeScanning database. The implementation follows official Supabase Kotlin patterns and integrates seamlessly with the existing barcode scanning workflow.

## Architecture

### Components Added

1. **AssetTag Data Model** (`BarcodeModels.kt`)
   - Kotlin data class with Supabase serialization
   - Follows official Supabase naming conventions
   - Includes metadata for tracking and audit

2. **AssetTagRepository** (`AssetTagRepository.kt`)
   - Repository pattern implementation
   - Complete CRUD operations following official Supabase patterns
   - Error handling and logging
   - Device-specific data isolation

3. **MainActivity Integration**
   - Automatic asset tag creation when barcode is scanned
   - UPSERT operations for existing barcodes
   - Enhanced error handling and user feedback

4. **Database Schema** (`asset_tags_schema.sql`)
   - Production-ready PostgreSQL schema
   - Indexes for performance optimization
   - Row Level Security (RLS) for data isolation

## CRUD Operations

### CREATE Operation

**Purpose**: Create a new asset tag when a barcode is scanned

```kotlin
suspend fun createAssetTag(
    scannedBarcode: String,
    assetName: String? = null,
    assetDescription: String? = null,
    location: String? = null
): Result<AssetTag>
```

**Usage**: Automatically called when scanning new barcodes

**Database Action**: `INSERT INTO asset_tags ...`

### READ Operations

#### Get All Asset Tags
```kotlin
suspend fun getAllAssetTags(): Result<List<AssetTag>>
```

#### Get Asset Tag by Barcode
```kotlin
suspend fun getAssetTagByBarcode(barcodeValue: String): Result<AssetTag?>
```

#### Get Recent Asset Tags
```kotlin
suspend fun getRecentAssetTags(limit: Int = 50): Result<List<AssetTag>>
```

**Database Actions**: `SELECT FROM asset_tags WHERE ...`

### UPDATE Operation

**Purpose**: Update asset tag information (name, description, location, status)

```kotlin
suspend fun updateAssetTag(
    id: String,
    assetName: String? = null,
    assetDescription: String? = null,
    location: String? = null,
    status: String? = null
): Result<AssetTag>
```

**Database Action**: `UPDATE asset_tags SET ... WHERE id = ? AND device_id = ?`

### DELETE Operations

#### Delete by ID
```kotlin
suspend fun deleteAssetTag(id: String): Result<Boolean>
```

#### Delete by Barcode Value
```kotlin
suspend fun deleteAssetTagByBarcode(barcodeValue: String): Result<Boolean>
```

**Database Actions**: `DELETE FROM asset_tags WHERE ...`

### UPSERT Operation

**Purpose**: Create new or update existing asset tag (smart operation for rescanning)

```kotlin
suspend fun upsertAssetTag(
    scannedBarcode: String,
    assetName: String? = null,
    assetDescription: String? = null,
    location: String? = null
): Result<AssetTag>
```

**Logic**: 
1. Check if asset tag exists for the scanned barcode
2. If exists: Update the existing record
3. If not exists: Create new record

## Integration with Barcode Scanning

### Automatic Workflow

When a barcode is successfully scanned:

1. **Local Storage**: Save to local history (existing functionality)
2. **Barcode Database**: Save scan record to `barcode_scans` table (existing)
3. **Asset Tag Database**: **NEW** - Create/update asset tag in `asset_tags` table
4. **User Feedback**: Show confirmation with asset tag status

### Enhanced MainActivity Flow

```kotlin
private fun processBarcodes(barcodes: List<Barcode>) {
    // ... existing barcode processing ...
    
    // Save to local history
    barcodeHistoryRepository.saveBarcodeToHistory(barcodeValue, barcodeType)
    
    // Save to Supabase (both tables)
    saveBarcodeToSupabase(barcodeValue, barcodeType)
}

private fun saveBarcodeToSupabase(barcodeText: String, barcodeType: String) {
    // 1. Save barcode scan (existing)
    supabaseRepository.saveBarcodeToSupabase(barcodeText, barcodeType)
    
    // 2. Create/update asset tag (NEW)
    assetTagRepository.upsertAssetTag(
        scannedBarcode = barcodeText,
        assetDescription = "Scanned $barcodeType barcode"
    )
}
```

## Database Schema

### Table Structure

```sql
CREATE TABLE asset_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    asset_tag TEXT NOT NULL,           -- Scanned barcode value
    asset_name TEXT,                   -- Optional asset name
    asset_description TEXT,            -- Optional description  
    location TEXT,                     -- Optional location
    status TEXT DEFAULT 'active',      -- active/inactive/maintenance
    scanned_at TIMESTAMPTZ NOT NULL,   -- Scan timestamp
    device_id TEXT,                    -- Device isolation
    app_version TEXT,                  -- App version tracking
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

### Indexes for Performance

- `idx_asset_tags_asset_tag`: Fast lookup by barcode value
- `idx_asset_tags_device_id`: Device-specific queries
- `idx_asset_tags_scanned_at`: Time-based queries
- `idx_asset_tags_device_scanned_at`: Composite index for app queries

### Security Features

- **Row Level Security (RLS)**: Users can only access their device's data
- **Data Isolation**: Each device_id sees only its own records
- **Input Validation**: Constraints prevent empty asset tags

## Usage Examples

### Basic Scanning (Automatic)

User scans a barcode → App automatically creates/updates asset tag

### Manual Asset Tag Operations

```kotlin
// Create asset tag with additional info
assetTagRepository.createAssetTag(
    scannedBarcode = "ABC123",
    assetName = "Office Printer",
    assetDescription = "HP LaserJet Pro",
    location = "Floor 2, Room 201"
)

// Update asset information
assetTagRepository.updateAssetTag(
    id = "uuid-here",
    assetName = "Updated Printer Name",
    status = "maintenance"
)

// Get all assets for this device
val assets = assetTagRepository.getAllAssetTags()

// Delete asset tag
assetTagRepository.deleteAssetTag("uuid-here")
```

## Error Handling

### Network Issues
- Operations fail gracefully when offline
- Local barcode history continues to work
- Sync occurs when connection restored

### Database Errors
- Detailed logging for troubleshooting
- User-friendly error messages
- Fallback to local-only operation

### Duplicate Handling
- UPSERT operations prevent duplicates
- Existing assets updated with new scan time
- Move existing records to top of history

## Performance Considerations

### Database Optimization
- Efficient indexes for common queries
- Prepared statements for security
- Batch operations where possible

### Memory Management
- Lazy loading of asset lists
- Pagination for large datasets
- Proper cleanup of resources

### Network Efficiency
- Async operations (non-blocking UI)
- Retry logic for failed requests
- Compression for large payloads

## Security Implementation

### Data Privacy
- Device-specific data isolation
- No cross-device data access
- Secure credential management via BuildConfig

### Access Control
- Row Level Security in database
- Authentication required for API access
- Input validation and sanitization

### Audit Trail
- Timestamp tracking (created_at, updated_at)
- Device identification for accountability
- App version tracking for debugging

## Testing

### Unit Tests
```kotlin
// Test asset tag creation
@Test
fun testCreateAssetTag() {
    // Test implementation
}

// Test UPSERT behavior
@Test  
fun testUpsertAssetTag() {
    // Test implementation
}
```

### Integration Tests
- Database connection testing
- CRUD operation verification
- Error handling validation

### Manual Testing
1. Scan new barcode → Verify asset tag created
2. Scan same barcode → Verify asset tag updated
3. Check offline behavior → Verify graceful degradation

## Monitoring and Maintenance

### Logging
- Detailed operation logs with timing
- Error tracking with stack traces
- Success/failure rate monitoring

### Database Maintenance
- Automatic cleanup of old records (optional)
- Performance monitoring with indexes
- Regular backup verification

### App Monitoring
- Connection status tracking
- Sync success/failure rates
- User experience metrics

## Future Enhancements

### Potential Features
1. **Location Services**: Auto-populate location from GPS
2. **Asset Photos**: Upload images with asset tags
3. **Batch Operations**: Scan multiple items at once
4. **Asset Categories**: Organize by type/department
5. **Export Features**: Generate reports and CSV exports
6. **Barcode Generation**: Create printable asset tags

### API Extensions
1. **Asset Search**: Full-text search across all fields
2. **Asset Relationships**: Link related assets
3. **Asset History**: Track changes over time
4. **User Management**: Multi-user support with permissions

## Troubleshooting

### Common Issues

1. **Asset Tag Not Created**
   - Check internet connection
   - Verify Supabase credentials
   - Check database permissions

2. **Duplicate Asset Tags**
   - UPSERT should prevent this
   - Check device_id consistency
   - Verify unique constraints

3. **Performance Issues**
   - Check database indexes
   - Monitor query performance
   - Consider pagination for large datasets

### Debug Steps

1. Check logs for error messages
2. Test database connection
3. Verify Supabase configuration
4. Test with simple asset tag creation

## Configuration

### Environment Variables
```properties
# In local.properties
supabase.url=https://your-project.supabase.co
supabase.anon.key=your-anon-key
```

### Build Configuration
```kotlin
// In app/build.gradle
buildConfigField "String", "SUPABASE_URL", "\"${supabaseUrl}\""
buildConfigField "String", "SUPABASE_ANON_KEY", "\"${supabaseAnonKey}\""
```

## Conclusion

The asset tag CRUD implementation provides a robust, scalable solution for managing barcode scan data in the BarcodeScanning database. Following official Supabase patterns ensures reliability, security, and maintainability while providing a seamless user experience.
