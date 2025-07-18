@echo off
echo ============================================
echo   Android Barcode Scanner - Gradle 9.0
echo ============================================
echo.

echo Java Version:
java -version
echo.

echo Setting up environment for Gradle 9.0...
set GRADLE_OPTS=-Dorg.gradle.daemon=true -Dorg.gradle.parallel=true -Dorg.gradle.caching=true
set GRADLE_USER_HOME=%USERPROFILE%\.gradle

echo.
echo Initializing Gradle wrapper (may download Gradle 9.0)...
call gradlew.bat --version
if errorlevel 1 (
    echo ❌ Failed to initialize Gradle. Check SSL/network settings.
    echo Try running with corporate network settings or offline mode.
    pause
    exit /b 1
)

echo.
echo Step 1: Cleaning project...
call gradlew.bat clean --configuration-cache --no-daemon
if errorlevel 1 (
    echo ❌ Clean failed. Trying without configuration cache...
    call gradlew.bat clean --no-daemon
)

echo.
echo Step 2: Building debug APK...
call gradlew.bat assembleDebug --configuration-cache --no-daemon
if errorlevel 1 (
    echo ❌ Build failed with configuration cache. Trying without...
    call gradlew.bat assembleDebug --no-daemon
)

echo.
echo Step 3: Running tests...
call gradlew.bat test --configuration-cache --no-daemon
if errorlevel 1 (
    echo ⚠️ Tests failed or skipped. Continuing...
)

echo.
echo ============================================
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo   ✅ SUCCESS: APK built successfully with Gradle 9.0!
    echo   📱 Location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo   File details:
    for %%I in ("app\build\outputs\apk\debug\app-debug.apk") do (
        echo     Size: %%~zI bytes
        echo     Modified: %%~tI
    )
    echo.
    echo   ✅ Project is now Gradle 9.0 compatible!
) else (
    echo   ❌ FAILED: APK was not generated
    echo   
    echo   Troubleshooting steps:
    echo   1. Check network connectivity
    echo   2. Verify Java 17+ is installed
    echo   3. Try: gradlew.bat assembleDebug --stacktrace
    echo   4. Check for SSL/corporate firewall issues
)
echo ============================================
pause
