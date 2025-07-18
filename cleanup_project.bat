@echo off
echo ============================================
echo   Project Cleanup - Remove Old Files
echo ============================================
echo.
echo This script will remove old/unused build files and references.
echo.
set /p confirm="Continue with cleanup? (y/N): "
if /i not "%confirm%"=="y" (
    echo Cleanup cancelled.
    pause
    exit /b 0
)

echo.
echo Starting cleanup...
cd /d "c:\Users\egeiger\OneDrive - Ovintiv\Documents\GitHub\BarcodeScanner"

echo.
echo 🗑️ Removing old build scripts...

REM Remove old build scripts that reference gradle-8.1.1
if exist "build_debug_apk.bat" (
    del "build_debug_apk.bat"
    echo   ✓ Removed build_debug_apk.bat
)

if exist "build_release_apk.bat" (
    del "build_release_apk.bat"
    echo   ✓ Removed build_release_apk.bat
)

if exist "build_offline.bat" (
    del "build_offline.bat"
    echo   ✓ Removed build_offline.bat
)

if exist "build_apk.bat" (
    del "build_apk.bat"
    echo   ✓ Removed build_apk.bat
)

if exist "build_final.bat" (
    del "build_final.bat"
    echo   ✓ Removed build_final.bat
)

if exist "build_with_ssl_fix.bat" (
    del "build_with_ssl_fix.bat"
    echo   ✓ Removed build_with_ssl_fix.bat
)

if exist "build_java11.gradle" (
    del "build_java11.gradle"
    echo   ✓ Removed build_java11.gradle
)

echo.
echo 🗑️ Removing old gradle distributions...

REM Remove any local gradle distribution directories
if exist "gradle-8.1.1" (
    rmdir /s /q "gradle-8.1.1"
    echo   ✓ Removed gradle-8.1.1 directory
)

if exist "gradle-8.7" (
    rmdir /s /q "gradle-8.7"
    echo   ✓ Removed gradle-8.7 directory
)

if exist "gradle-7.5" (
    rmdir /s /q "gradle-7.5"
    echo   ✓ Removed gradle-7.5 directory
)

REM Remove gradle distribution zip files
if exist "gradle-8.1.1-bin.zip" (
    del "gradle-8.1.1-bin.zip"
    echo   ✓ Removed gradle-8.1.1-bin.zip
)

if exist "gradle-8.7-bin.zip" (
    del "gradle-8.7-bin.zip"
    echo   ✓ Removed gradle-8.7-bin.zip
)

if exist "gradle-7.5-bin.zip" (
    del "gradle-7.5-bin.zip"
    echo   ✓ Removed gradle-7.5-bin.zip
)

echo.
echo 🧹 Cleaning build caches...

REM Clean gradle caches (optional)
if exist ".gradle" (
    rmdir /s /q ".gradle"
    echo   ✓ Removed .gradle cache directory
)

echo.
echo ============================================
echo   ✅ Project cleanup completed!
echo.
echo   Remaining build scripts:
echo   - build.bat (modern build script)
echo   - build_gradle9.bat (Gradle 9.0 script)
echo   - gradlew.bat (Gradle wrapper)
echo   - validate_gradle9.bat (validation script)
echo.
echo   Project is now clean and Gradle 9.0 ready!
echo ============================================
pause
