#!/bin/bash

# Test Supabase Setup Script
# This script helps verify your Supabase configuration is working correctly

echo "🔍 Supabase Setup Verification"
echo "=============================="
echo ""

echo "✅ Step 1: Check Dependencies"
echo "Looking for Supabase dependencies in build.gradle..."

# Check if Supabase dependencies are present
if grep -q "supabase" app/build.gradle; then
    echo "   ✅ Supabase dependencies found"
else
    echo "   ❌ Supabase dependencies missing"
    echo "      Add these to your app/build.gradle:"
    echo "      implementation platform('io.github.jan-tennert.supabase:bom:2.5.4')"
    echo "      implementation 'io.github.jan-tennert.supabase:postgrest-kt'"
fi

echo ""
echo "✅ Step 2: Check Configuration"
echo "Checking SupabaseConfig.kt..."

# Check if configuration file exists and is configured
if [ -f "app/src/main/java/com/encana/barcodescanner/data/SupabaseConfig.kt" ]; then
    if grep -q "YOUR_SUPABASE_URL" app/src/main/java/com/encana/barcodescanner/data/SupabaseConfig.kt; then
        echo "   ⚠️  Configuration file needs your actual Supabase URL and key"
        echo "      Update SupabaseConfig.kt with your project credentials"
    else
        echo "   ✅ Configuration file appears to be updated"
    fi
else
    echo "   ❌ SupabaseConfig.kt not found"
fi

echo ""
echo "✅ Step 3: Check Internet Permission"
echo "Checking AndroidManifest.xml..."

if grep -q "android.permission.INTERNET" app/src/main/AndroidManifest.xml; then
    echo "   ✅ Internet permission found"
else
    echo "   ❌ Internet permission missing"
    echo "      Add this to AndroidManifest.xml:"
    echo "      <uses-permission android:name=\"android.permission.INTERNET\" />"
fi

echo ""
echo "🚀 Next Steps:"
echo "1. Follow SUPABASE_SETUP_GUIDE.md to create your project"
echo "2. Update SupabaseConfig.kt with your credentials"
echo "3. Build and test the app"
echo ""
echo "📱 Test Commands:"
echo "   Build: ./build_onedrive_safe.bat debug"
echo "   Install: adb install app/build/outputs/apk/debug/app-debug.apk"
