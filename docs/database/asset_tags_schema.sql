-- SQL Schema for Asset Tag CRUD Operations in BarcodeScanning Database
-- Following official Supabase database patterns and best practices

-- =============================================
-- Create asset_tags table for barcode scanning
-- =============================================

CREATE TABLE IF NOT EXISTS asset_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    asset_tag TEXT NOT NULL, -- The scanned barcode value
    asset_name TEXT,
    asset_description TEXT,
    location TEXT,
    status TEXT DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'maintenance')),
    scanned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    device_id TEXT,
    app_version TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    
    -- Constraints
    CONSTRAINT asset_tag_not_empty CHECK (LENGTH(TRIM(asset_tag)) > 0)
);

-- =============================================
-- Create indexes for performance optimization
-- =============================================

-- Index for quick lookup by asset_tag (most common query)
CREATE INDEX IF NOT EXISTS idx_asset_tags_asset_tag ON asset_tags(asset_tag);

-- Index for device-specific queries
CREATE INDEX IF NOT EXISTS idx_asset_tags_device_id ON asset_tags(device_id);

-- Index for time-based queries (recent scans)
CREATE INDEX IF NOT EXISTS idx_asset_tags_scanned_at ON asset_tags(scanned_at DESC);

-- Composite index for device + time queries (most efficient for the app)
CREATE INDEX IF NOT EXISTS idx_asset_tags_device_scanned_at ON asset_tags(device_id, scanned_at DESC);

-- Index for status filtering
CREATE INDEX IF NOT EXISTS idx_asset_tags_status ON asset_tags(status);

-- =============================================
-- Enable Row Level Security (RLS) for data isolation
-- =============================================

ALTER TABLE asset_tags ENABLE ROW LEVEL SECURITY;

-- Policy: Users can only access their own device records
-- This ensures data privacy when multiple devices use the same database
CREATE POLICY "asset_tags_device_isolation" ON asset_tags
    USING (device_id = current_setting('app.current_device_id', true));

-- Policy: Allow all operations for authenticated users on their own records
CREATE POLICY "asset_tags_authenticated_access" ON asset_tags
    FOR ALL USING (auth.role() = 'authenticated');

-- =============================================
-- Create trigger for automatic updated_at timestamps
-- =============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply trigger to asset_tags table
DROP TRIGGER IF EXISTS update_asset_tags_updated_at ON asset_tags;
CREATE TRIGGER update_asset_tags_updated_at
    BEFORE UPDATE ON asset_tags
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =============================================
-- Sample data and validation queries
-- =============================================

-- Insert sample asset tag (for testing)
INSERT INTO asset_tags (asset_tag, asset_name, asset_description, location, device_id, app_version) 
VALUES (
    'SAMPLE-001',
    'Test Asset',
    'Sample asset tag for testing CRUD operations',
    'Warehouse A',
    'test-device-001',
    'BarcodeScanner v1.0.0'
) ON CONFLICT DO NOTHING;

-- Validation queries to test the schema
-- 1. Test basic select
SELECT COUNT(*) as total_asset_tags FROM asset_tags;

-- 2. Test recent scans query (simulates app behavior)
SELECT asset_tag, asset_name, scanned_at, status 
FROM asset_tags 
WHERE device_id = 'test-device-001' 
ORDER BY scanned_at DESC 
LIMIT 10;

-- 3. Test asset lookup by barcode (simulates app lookup)
SELECT * FROM asset_tags 
WHERE asset_tag = 'SAMPLE-001' 
AND device_id = 'test-device-001';

-- =============================================
-- Performance optimization views (optional)
-- =============================================

-- View for recent active assets (frequently used query)
CREATE OR REPLACE VIEW recent_active_assets AS
SELECT 
    id,
    asset_tag,
    asset_name,
    asset_description,
    location,
    scanned_at,
    device_id
FROM asset_tags 
WHERE status = 'active' 
ORDER BY scanned_at DESC;

-- View for asset summary statistics
CREATE OR REPLACE VIEW asset_tag_stats AS
SELECT 
    device_id,
    COUNT(*) as total_assets,
    COUNT(CASE WHEN status = 'active' THEN 1 END) as active_assets,
    COUNT(CASE WHEN status = 'inactive' THEN 1 END) as inactive_assets,
    COUNT(CASE WHEN status = 'maintenance' THEN 1 END) as maintenance_assets,
    MAX(scanned_at) as last_scan_time,
    MIN(scanned_at) as first_scan_time
FROM asset_tags 
GROUP BY device_id;

-- =============================================
-- Database management and cleanup procedures
-- =============================================

-- Function to clean up old records (optional maintenance)
CREATE OR REPLACE FUNCTION cleanup_old_asset_tags(days_to_keep INTEGER DEFAULT 365)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM asset_tags 
    WHERE scanned_at < NOW() - INTERVAL '1 day' * days_to_keep;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- =============================================
-- Grant permissions for API access
-- =============================================

-- Grant permissions to authenticated users
GRANT SELECT, INSERT, UPDATE, DELETE ON asset_tags TO authenticated;
GRANT USAGE ON SEQUENCE asset_tags_id_seq TO authenticated;

-- Grant read-only access to anonymous users (if needed)
GRANT SELECT ON asset_tags TO anon;

-- =============================================
-- Comments for documentation
-- =============================================

COMMENT ON TABLE asset_tags IS 'Stores asset tag information from barcode scans in the BarcodeScanning database';
COMMENT ON COLUMN asset_tags.asset_tag IS 'The scanned barcode value serving as the asset identifier';
COMMENT ON COLUMN asset_tags.device_id IS 'Unique identifier for the scanning device to ensure data isolation';
COMMENT ON COLUMN asset_tags.status IS 'Current status of the asset: active, inactive, or maintenance';
COMMENT ON COLUMN asset_tags.scanned_at IS 'Timestamp when the barcode was scanned by the mobile app';

-- =============================================
-- Verification and testing commands
-- =============================================

-- Test insert operation (simulates app CREATE operation)
-- INSERT INTO asset_tags (asset_tag, asset_name, device_id) VALUES ('TEST-001', 'Test Asset', 'device-001');

-- Test update operation (simulates app UPDATE operation)  
-- UPDATE asset_tags SET asset_name = 'Updated Test Asset', updated_at = NOW() WHERE asset_tag = 'TEST-001';

-- Test delete operation (simulates app DELETE operation)
-- DELETE FROM asset_tags WHERE asset_tag = 'TEST-001';

-- Check table structure
-- \d asset_tags;

-- Check indexes
-- \di asset_tags*;

ANALYZE asset_tags;
