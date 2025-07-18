# 🚀 Supabase Setup Guide for Barcode Scanner App

## Overview

This guide walks you through setting up Supabase as the backend database for your Android Barcode Scanner app to store scan data in the cloud.

## Prerequisites

- ✅ Android project already set up
- ✅ Supabase dependencies already added to `build.gradle`
- ✅ Internet permission already added to `AndroidManifest.xml`

## Step 1: Create Your Supabase Project

### 1.1 Sign Up/Login to Supabase

1. Go to [https://supabase.com/dashboard](https://supabase.com/dashboard)
2. Sign up with GitHub, Google, or email
3. Create a new organization if prompted

### 1.2 Create a New Project

1. Click **"New Project"**
2. Choose your organization
3. Fill out project details:
   - **Name**: `Barcode Scanner Database`
   - **Database Password**: Use a strong password (save this!)
   - **Region**: Choose closest to your location
   - **Pricing Plan**: Start with Free tier
4. Click **"Create new project"**
5. Wait for project setup (2-3 minutes)

## Step 2: Create the Database Table

### 2.1 Access SQL Editor

1. In your Supabase dashboard, go to **SQL Editor** (left sidebar)
2. Click **"New query"**

### 2.2 Create the Barcode Scans Table

Copy and paste this SQL query:

```sql
-- Create the barcode_scans table
CREATE TABLE barcode_scans (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    barcode_text TEXT NOT NULL,
    barcode_type TEXT NOT NULL,
    scanned_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    device_id TEXT NOT NULL,
    app_version TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create an index for better query performance
CREATE INDEX idx_barcode_scans_device_id ON barcode_scans(device_id);
CREATE INDEX idx_barcode_scans_scanned_at ON barcode_scans(scanned_at DESC);

-- Enable Row Level Security (RLS)
ALTER TABLE barcode_scans ENABLE ROW LEVEL SECURITY;

-- Create a policy to allow all operations (for demo purposes)
-- In production, you'd want more restrictive policies
CREATE POLICY "Allow all operations on barcode_scans" ON barcode_scans
    FOR ALL TO anon, authenticated
    USING (true)
    WITH CHECK (true);
```

3. Click **"Run"** to execute the query
4. You should see "Success. No rows returned" message

### 2.3 Verify Table Creation

1. Go to **Table Editor** (left sidebar)
2. You should see the `barcode_scans` table
3. The table should have columns: `id`, `barcode_text`, `barcode_type`, `scanned_at`, `device_id`, `app_version`, `created_at`

## Step 3: Get Your Project Credentials

### 3.1 Get Project URL and API Key

1. Go to **Settings** > **API** (left sidebar)
2. Copy the following values:
   - **Project URL**: `https://your-project-id.supabase.co`
   - **Anon/Public Key**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` (long string)

## Step 4: Configure Your Android App

    ### 4.1 Update SupabaseConfig.kt

Replace the placeholder values in your app:

1. Open `app/src/main/java/com/encana/barcodescanner/data/SupabaseConfig.kt`
2. Replace `YOUR_SUPABASE_URL` with your Project URL
3. Replace `YOUR_SUPABASE_ANON_KEY` with your Anon/Public Key

```kotlin
object SupabaseConfig {
    // Replace with your actual Supabase project URL
    private const val SUPABASE_URL = "https://your-project-id.supabase.co"
    
    // Replace with your actual Supabase anon key
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Realtime)
    }
}
```

## Step 5: Test the Connection

### 5.1 Build and Run the App

1. Build your app using the OneDrive-safe script: `.\build_onedrive_safe.bat debug`
2. Install the APK on your device or emulator
3. Launch the app

### 5.2 Test Scanning

1. Press **"Scan"** button
2. Point camera at a barcode/QR code
3. When detected, check the logs for:
   - `✅ Supabase connection successful`
   - `✅ Barcode synced to Supabase: [scan-id]`

### 5.3 Verify Data in Supabase

1. Go back to **Table Editor** in Supabase
2. Click on the `barcode_scans` table
3. You should see your scan data appearing in real-time!

## Step 6: Monitor and Manage Data

### 6.1 View Scan History

- In **Table Editor**, you can see all scanned barcodes
- Data includes: barcode text, type, timestamp, device ID

### 6.2 Database Insights

- Go to **Reports** to see usage statistics
- Monitor storage usage and API calls

## Troubleshooting

### Common Issues:

**1. "Connection failed" message:**

- Check your internet connection
- Verify Project URL and API key are correct
- Ensure RLS policies are set up correctly

**2. "Permission denied" errors:**

- Make sure RLS policies allow the operations
- Check that the table exists with correct schema

**3. App crashes on startup:**

- Check Android logs for specific error messages
- Ensure all Supabase dependencies are properly added

### Debug Steps:

1. Check Android Studio logs for detailed error messages
2. Test the table in Supabase SQL Editor with simple queries
3. Verify network permissions in AndroidManifest.xml

## Next Steps

### Production Considerations:

1. **Security**: Implement proper RLS policies for production
2. **Environment Variables**: Store credentials securely
3. **Error Handling**: Add robust offline/online sync
4. **Performance**: Implement pagination for large datasets
5. **Backup**: Set up automated database backups

### Advanced Features:

- Real-time sync across devices
- User authentication
- Data export/import
- Analytics and reporting

## Support Resources

- [Supabase Documentation](https://supabase.com/docs)
- [Supabase Discord Community](https://discord.supabase.com/)
- [Android Kotlin Guide](https://supabase.com/docs/guides/getting-started/quickstarts/kotlin)

---

**🎉 Congratulations!** Your barcode scanner now has cloud backup and sync capabilities with Supabase!

Every scan is automatically saved both locally and in the cloud, ensuring your data is always safe and accessible.
