package com.encana.barcodescanner.data

import com.encana.barcodescanner.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Secure Supabase client configuration for the Barcode Scanner app
 * 
 * SECURITY SETUP:
 * 1. Create/edit 'local.properties' file in project root (already gitignored)
 * 2. Add your Supabase credentials:
 *    supabase.url=https://your-project-id.supabase.co
 *    supabase.anon.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 * 3. Never commit local.properties to git (already in .gitignore)
 * 
 * The credentials are securely injected via BuildConfig at compile time.
 */
object SupabaseConfig {
    
    // Securely loaded from local.properties via BuildConfig
    // These values are NOT stored in source code or committed to git
    private val SUPABASE_URL = BuildConfig.SUPABASE_URL
    private val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
    
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Realtime)
    }
}
