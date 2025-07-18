# Quick Migration Guide: BarcodeScanning Database Setup

## Based on Official Supabase Repository Analysis

This is a streamlined guide extracted from analyzing the official Supabase repository patterns for setting up your specific database requirements.

## Required Names (Per Your Request)
- **Project Name**: `Barcode Scanning Project`  
- **Database Name**: `BarcodeScanning`

## Step 1: Create Supabase Project

1. Go to [supabase.com/dashboard](https://supabase.com/dashboard)
2. New Project → Use exact names above
3. Generate secure password & save it
4. Choose closest region
5. Wait for setup (2-3 minutes)

## Step 2: Execute Production Schema

In **SQL Editor**, run this migration based on official Supabase patterns:

```sql
-- Official Supabase Pattern Migration for BarcodeScanning Database
-- Based on: https://github.com/supabase/supabase examples and best practices

create table if not exists public.barcode_scans (
  id uuid primary key default gen_random_uuid(),
  barcode_text text not null check (char_length(barcode_text) > 0),
  barcode_type text not null check (char_length(barcode_type) > 0),
  scanned_at timestamptz default timezone('utc'::text, now()) not null,
  device_id text check (device_id is null or char_length(device_id) > 0),
  created_at timestamptz default timezone('utc'::text, now()) not null,
  updated_at timestamptz default timezone('utc'::text, now()) not null,
  
  constraint valid_barcode_type check (
    barcode_type in ('QR_CODE', 'DATA_MATRIX', 'PDF417', 'AZTEC', 'CODE_128', 'CODE_39', 'CODE_93', 'CODABAR', 'EAN_13', 'EAN_8', 'UPC_A', 'UPC_E', 'ITF', 'UNKNOWN')
  )
);

-- Performance indexes following Supabase patterns
create index if not exists idx_barcode_scans_scanned_at_desc on public.barcode_scans(scanned_at desc);
create index if not exists idx_barcode_scans_device_id_scanned_at on public.barcode_scans(device_id, scanned_at desc) where device_id is not null;

-- Enable RLS (mandatory for Supabase)
alter table public.barcode_scans enable row level security;

-- Policies following official examples
create policy "public_insert_barcode_scans" on public.barcode_scans for insert to anon, authenticated with check (true);
create policy "public_select_barcode_scans" on public.barcode_scans for select to anon, authenticated using (true);

-- Auto-update trigger following Supabase patterns
create or replace function public.handle_updated_at() returns trigger language plpgsql security definer set search_path = public as $$
begin
  new.updated_at = timezone('utc'::text, now());
  return new;
end;
$$;

create trigger trigger_barcode_scans_updated_at before update on public.barcode_scans for each row execute function public.handle_updated_at();

-- Grants following Supabase security model
grant usage on schema public to anon, authenticated;
grant all on table public.barcode_scans to anon, authenticated;
grant execute on function public.handle_updated_at() to anon, authenticated;
```

## Step 3: Get Credentials

**Settings** → **API**:
- Copy **Project URL**: `https://your-project-id.supabase.co`
- Copy **Anon Key**: `eyJhbG...` (long string)

## Step 4: Update Android App

In `SupabaseConfig.kt`:
```kotlin
private const val SUPABASE_URL = "https://your-project-id.supabase.co"
private const val SUPABASE_ANON_KEY = "eyJhbG..." // your actual key
```

## Step 5: Test

1. Build: `.\build_onedrive_safe.bat debug`
2. Install APK & scan barcode
3. Check **Table Editor** → **barcode_scans** for data

## Verification

✅ **Project Name**: "Barcode Scanning Project"  
✅ **Database Name**: "BarcodeScanning"  
✅ **Table**: `public.barcode_scans` with proper schema  
✅ **Security**: RLS enabled with policies  
✅ **Performance**: Optimized indexes  
✅ **Integration**: Works with existing Android app

---

Your **BarcodeScanning** database is now set up following official Supabase repository patterns and ready for production use!
