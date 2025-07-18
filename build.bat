@echo off
echo ============================================
echo   Android Barcode Scanner Build Script
echo ============================================
echo.

set "BUILD_TYPE=%1"
if "%BUILD_TYPE%"=="" set "BUILD_TYPE=debug"

echo Building %BUILD_TYPE% APK...
echo.

if /i "%BUILD_TYPE%"=="release" (
    call gradlew.bat assembleRelease --configuration-cache
    set "APK_PATH=app\build\outputs\apk\release\app-release-unsigned.apk"
) else (
    call gradlew.bat assembleDebug --configuration-cache
    set "APK_PATH=app\build\outputs\apk\debug\app-debug.apk"
)

echo.
echo ============================================
if exist "%APK_PATH%" (
    echo   ✅ SUCCESS: %BUILD_TYPE% APK built successfully!
    echo   📱 Location: %APK_PATH%
    echo.
    echo   File details:
    dir "%APK_PATH%"
) else (
    echo   ❌ FAILED: %BUILD_TYPE% APK was not generated
    echo   Check the output above for errors.
)
echo ============================================
echo.
echo Usage: %0 [debug^|release]
echo   debug   - Build debug APK (default)
echo   release - Build release APK (unsigned)
echo.
pause
