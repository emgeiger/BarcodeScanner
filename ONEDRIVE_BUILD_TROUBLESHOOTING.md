# OneDrive Android Build Troubleshooting Guide

## Overview

This guide addresses common issues when building Android projects stored in OneDrive directories, specifically file locking problems that prevent Gradle from cleaning build directories.

## Common Error Pattern

```
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:clean'.
> java.io.IOException: Unable to delete directory 
    Failed to delete some children. This might happen because a process has files open or has its working directory set in the target directory.
    - app\build\kotlin\compileDebugKotlin\local-state
    - app\build\tmp\compileDebugJavaWithJavac\compileTransaction\backup-dir
```

## Root Causes

### 1. OneDrive File Locking

- **Issue**: OneDrive sync process locks build files
- **Impact**: Gradle cannot delete build directories
- **Solution**: Use OneDrive-safe build scripts

### 2. Gradle Daemon File Handles

- **Issue**: Background Gradle daemons keep file handles open
- **Impact**: Files remain locked between builds
- **Solution**: Use `--no-daemon` and stop daemons

### 3. Java Process Persistence

- **Issue**: Java processes from previous builds remain active
- **Impact**: Build files stay locked
- **Solution**: Kill Java processes before builds

### 4. Windows File System Permissions

- **Issue**: OneDrive changes file attributes and permissions
- **Impact**: Standard delete operations fail
- **Solution**: Reset attributes before cleanup

## Solution Scripts

### 1. Quick Fix: `build_onedrive_safe.bat`

**Use for**: Simple builds with basic OneDrive protection

```batch
build_onedrive_safe.bat debug
build_onedrive_safe.bat release
```

### 2. Comprehensive Fix: `build_onedrive_safe_fixed.bat`

**Use for**: Complex file locking issues with smart retry logic

```batch
build_onedrive_safe_fixed.bat debug
build_onedrive_safe_fixed.bat release
```

### 3. Manual Cleanup: `cleanup_onedrive_locks.bat`

**Use for**: Pre-build cleanup when builds consistently fail

```batch
cleanup_onedrive_locks.bat
```

## Manual Troubleshooting Steps

### Step 1: Stop All Locking Processes

```batch
# Stop Gradle daemons
gradlew.bat --stop

# Kill Java processes
taskkill /F /IM java.exe
taskkill /F /IM javaw.exe

# Close Android Studio (if open)
taskkill /F /IM studio64.exe
```

### Step 2: Reset File Attributes

```batch
# Remove readonly/hidden/system attributes
attrib -R -H -S app\build\*.* /S /D

# Try manual directory removal
rmdir /S /Q app\build\kotlin
rmdir /S /Q app\build\tmp
```

### Step 3: OneDrive Management

```batch
# Pause OneDrive sync temporarily
# Right-click OneDrive icon → Pause syncing

# Check OneDrive status
tasklist | findstr OneDrive.exe
```

### Step 4: Clean Build with Safe Options

```batch
gradlew.bat clean --no-daemon --no-configuration-cache --no-build-cache
gradlew.bat assembleDebug --no-daemon --no-configuration-cache --no-build-cache
```

## Prevention Strategies

### 1. Exclude Build Directories from OneDrive

Add to `.onedriveignore` (if supported):

```
**/build/
**/.gradle/
**/gradle/
*.tmp
*.log
```

### 2. Configure Gradle for OneDrive

In `gradle.properties`:

```properties
# Disable features that create file locks
org.gradle.daemon=false
org.gradle.parallel=false
org.gradle.caching=false

# Use Windows temp directory
java.io.tmpdir=C:\\temp
```

### 3. Move Gradle User Home

Set environment variable:

```batch
set GRADLE_USER_HOME=C:\gradle-cache
```

### 4. Use Alternative Development Setup

- **Source Code**: Keep in OneDrive for backup
- **Development**: Clone to local directory (C:\dev\BarcodeScanner)
- **Version Control**: Use Git for sync, not OneDrive

## Advanced Troubleshooting

### Check File Locks

```batch
# Find what's locking files
handle.exe app\build\kotlin
```

### Process Monitor

1. Download Process Monitor from Microsoft
2. Filter for your project directory
3. Identify which processes are accessing build files

### Registry Cleanup (Admin Required)

```batch
# Clean Gradle registry entries
reg delete "HKEY_CURRENT_USER\Software\Gradle" /f
```

### Alternative Build Locations

```batch
# Build to different location
gradlew.bat assembleDebug -Pbuild.dir=C:\temp\build
```

## Error-Specific Solutions

### "local-state" Directory Locked

```batch
# Specific to Kotlin compilation
rmdir /S /Q app\build\kotlin\compileDebugKotlin\local-state
attrib -R -H -S app\build\kotlin\*.* /S
```

### "compileTransaction" Directory Locked

```batch
# Specific to Java compilation
rmdir /S /Q app\build\tmp\compileDebugJavaWithJavac\compileTransaction
rmdir /S /Q app\build\tmp\compileReleaseJavaWithJavac\compileTransaction
```

### Gradle Wrapper Issues

```batch
# Clean and re-download wrapper
rmdir /S /Q .gradle
gradlew.bat wrapper --gradle-version 8.11.1
```

## Performance Optimizations

### 1. Use SSD for OneDrive Cache

- Move OneDrive cache to SSD
- Improves sync performance

### 2. Optimize OneDrive Settings

- Set "Files On-Demand" for build directories
- Reduce sync frequency during development

### 3. Use Gradle Build Cache

- Enable only when OneDrive is not syncing
- Store cache outside OneDrive directory

## Success Indicators

### Build Success Markers

```
✅ SUCCESS: debug APK built successfully!
📱 Location: app\build\outputs\apk\debug\app-debug.apk
📦 Size: [size] bytes ([size] MB)
```

### Clean Success Markers

```
✅ All problematic directories cleaned successfully!
🎯 Ready for build!
```

## Emergency Procedures

### Complete Reset

```batch
# Nuclear option - complete cleanup
cleanup_onedrive_locks.bat
rmdir /S /Q app\build
rmdir /S /Q .gradle
rmdir /S /Q build
gradlew.bat clean --no-daemon
```

### Administrator Mode

If normal scripts fail:

1. Right-click Command Prompt → "Run as administrator"
2. Navigate to project directory
3. Run cleanup scripts with elevated privileges

### Offline Mode

```batch
# Build without network/OneDrive
build_offline.bat debug
```

## Best Practices Summary

1. ✅ **Use provided OneDrive-safe scripts**
2. ✅ **Stop Gradle daemons between builds**
3. ✅ **Pause OneDrive during active development**
4. ✅ **Keep source in OneDrive, build locally**
5. ✅ **Use Git for version control**
6. ✅ **Monitor build directory sizes**
7. ✅ **Clean regularly with safe scripts**

## Contact and Support

For persistent issues:

1. Check Windows Event Viewer for OneDrive errors
2. Update OneDrive client to latest version
3. Consider moving development to local Git repository
4. Use build_gradle9.bat for modern Gradle features

## Script Compatibility

| Script | OneDrive Safe | Clean Handling | Retry Logic | Best For |
|--------|---------------|----------------|-------------|----------|
| `build_onedrive_safe.bat` | ✅ | Basic | No | Quick builds |
| `build_onedrive_safe_fixed.bat` | ✅ | Advanced | Yes | Persistent issues |
| `cleanup_onedrive_locks.bat` | ✅ | Complete | Yes | Pre-build cleanup |
| `build_gradle9.bat` | ⚠️ | Standard | No | Modern features |
| `build_offline.bat` | ✅ | Standard | No | Network issues |

Choose the appropriate script based on your specific OneDrive file locking scenario.
